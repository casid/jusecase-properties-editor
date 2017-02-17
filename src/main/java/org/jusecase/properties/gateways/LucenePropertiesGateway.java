package org.jusecase.properties.gateways;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.jusecase.properties.entities.Property;

import javax.inject.Singleton;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Singleton
public class LucenePropertiesGateway implements PropertiesGateway {
    private static final String ID = "id";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String FILE_NAME = "fileName";

    private List<Path> files;
    private Set<Path> dirtyFiles = new HashSet<>();
    private List<String> keys;

    private RAMDirectory ramDirectory;
    private IndexWriter indexWriter;
    private DirectoryReader indexReader;
    private IndexSearcher indexSearcher;

    @Override
    public void loadProperties(List<Path> files) {
        this.files = files;
        this.dirtyFiles.clear();
        this.keys = null;

        try {
            close();

            ramDirectory = new RAMDirectory();
            indexWriter = new IndexWriter(ramDirectory, new IndexWriterConfig());
            for (Path file : files) {
                loadProperties(file);
            }
            indexWriter.commit();

            indexReader = DirectoryReader.open(ramDirectory);
            indexSearcher = new IndexSearcher(indexReader);
        } catch (IOException e) {
            throw new GatewayException("Failed to create lucene index!", e);
        }
    }

    private void close() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }

        if (indexReader != null) {
            indexReader.close();
        }

        if (ramDirectory != null) {
            ramDirectory.close();
        }
    }

    private void loadProperties(Path file) throws IOException {
        Property property = new Property();
        property.fileName = file.getFileName().toString();
        Properties properties = loadJavaProperties(file);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            property.key = entry.getKey().toString();
            property.value = entry.getValue().toString();
            indexWriter.addDocument(createDocument(property));
        }
    }

    @Override
    public List<String> getKeys() {
        if (!isInitialized()) {
            return new ArrayList<>();
        }

        if (keys == null) {
            try {
                SortedSet<String> keySet = new TreeSet<>();
                Fields fields = MultiFields.getFields(indexSearcher.getIndexReader());
                Terms terms = fields.terms(KEY);
                if (terms != null) {
                    TermsEnum iterator = terms.iterator();
                    BytesRef byteRef;

                    while ((byteRef = iterator.next()) != null) {
                        keySet.add(byteRef.utf8ToString());
                    }
                }
                keys = new ArrayList<>(keySet);
            } catch (IOException e) {
                throw new GatewayException("Failed to search for all keys", e);
            }
        }
        return keys;
    }

    @Override
    public List<Property> getProperties(String key) {
        if (!isInitialized()) {
            return new ArrayList<>();
        }

        Query query = new TermQuery(new Term(KEY, key));
        try {
            TopDocs result = indexSearcher.search(query, files.size());
            if (result.totalHits == 0) {
                return new ArrayList<>();
            }

            Map<String, String> fileNameToValue = new HashMap<>();
            for (ScoreDoc scoreDoc : result.scoreDocs) {
                Document document = indexSearcher.getIndexReader().document(scoreDoc.doc);
                fileNameToValue.put(document.get(FILE_NAME), document.get(VALUE));
            }

            List<Property> properties = new ArrayList<>(files.size());
            for (Path file : files) {
                Property property = new Property();
                property.key = key;
                property.fileName = file.getFileName().toString();
                property.value = fileNameToValue.get(property.fileName);
                properties.add(property);
            }

            return properties;
        } catch (IOException e) {
            throw new GatewayException("Failed perform search query", e);
        }
    }

    @Override
    public List<String> search(String queryString) {
        if (!isInitialized() || queryString.isEmpty()) {
            return getKeys();
        }

        try {
            Query query = createSearchQuery(queryString);
            SortedSet<String> keySet = new TreeSet<>();
            TopDocs result = indexSearcher.search(query, indexSearcher.getIndexReader().numDocs());
            for (ScoreDoc scoreDoc : result.scoreDocs) {
                Document document = indexSearcher.getIndexReader().document(scoreDoc.doc);
                keySet.add(document.get(KEY));
            }

            return new ArrayList<>(keySet);
        } catch (IOException e) {
            throw new GatewayException("Failed perform search query", e);
        }
    }

    @Override
    public void updateValue(Property property) {
        updateIndex(() -> {
            Term term = new Term(ID, createId(property));
            if (property.value == null) {
                indexWriter.deleteDocuments(term);
            } else {
                indexWriter.updateDocument(term, createDocument(property));
            }
            markAsDirty(property.fileName);
        });
    }

    @Override
    public void addKey(String key) {
        updateIndex(() -> {
            Path file = files.get(0);

            Property property = new Property();
            property.key = key;
            property.value = "";

            property.fileName = file.getFileName().toString();
            indexWriter.addDocument(createDocument(property));
            markAsDirty(property.fileName);

            keys = null;
        });
    }

    @Override
    public void save() {
        if (isInitialized()) {
            for (Path file : files) {
                if (dirtyFiles.contains(file)) {
                    save(file);
                    dirtyFiles.remove(file);
                }
            }
        }
    }

    @Override
    public void saveAll() {
        if (isInitialized()) {
            files.forEach(this::save);
        }
    }

    private void save(Path file) {
        Query query = new TermQuery(new Term(FILE_NAME, file.getFileName().toString()));
        try {
            TopDocs result = indexSearcher.search(query, getKeys().size());
            Properties properties = new CleanProperties();

            for (ScoreDoc scoreDoc : result.scoreDocs) {
                Document document = indexSearcher.getIndexReader().document(scoreDoc.doc);
                properties.put(document.get(KEY), document.get(VALUE));
            }

            writeJavaProperties(file, properties);
        } catch (IOException e) {
            throw new GatewayException("Failed to save properties to " + file, e);
        }
    }

    private void updateIndex(IndexUpdateTask task) {
        if (!isInitialized()) {
            return;
        }

        try {
            task.update();
            indexWriter.commit();

            DirectoryReader newReader = DirectoryReader.openIfChanged(indexReader, indexWriter);
            if (newReader != null) {
                indexReader.close();
                indexReader = newReader;
                indexSearcher = new IndexSearcher(indexReader);
            }
        } catch (IOException e) {
            throw new GatewayException("Failed to update lucene search index", e);
        }
    }

    private boolean isInitialized() {
        return indexSearcher != null;
    }

    private Query createSearchQuery(String queryString) {
        BooleanQuery.Builder keyOrValueQueryBuilder = new BooleanQuery.Builder();
        keyOrValueQueryBuilder.add(new WildcardQuery(new Term(KEY, "*" + queryString + "*")), BooleanClause.Occur.SHOULD);

        BooleanQuery.Builder valueQueryBuilder = new BooleanQuery.Builder();
        for (String queryWord : queryString.split("[, -]")) {
            queryWord = queryWord.trim();
            if (!queryWord.isEmpty()) {
                BooleanClause.Occur clause = BooleanClause.Occur.FILTER;
                if (queryWord.length() <= 3) {
                    clause = BooleanClause.Occur.SHOULD;
                }
                valueQueryBuilder.add(new PrefixQuery(new Term(VALUE, queryWord.toLowerCase())), clause);
            }
        }
        keyOrValueQueryBuilder.add(valueQueryBuilder.build(), BooleanClause.Occur.SHOULD);

        return new BooleanQuery.Builder()
                .add(keyOrValueQueryBuilder.build(), BooleanClause.Occur.MUST)
                .build();
    }

    private String createId(String fileName, String key) {
        return fileName + key;
    }

    private String createId(Property property) {
        return createId(property.fileName, property.key);
    }

    private Document createDocument(Property property) {
        Document document = new Document();
        document.add(new StringField(ID, createId(property), Field.Store.YES));
        document.add(new StringField(KEY, property.key, Field.Store.YES));
        document.add(new TextField(VALUE, property.value, Field.Store.YES));
        document.add(new StringField(FILE_NAME, property.fileName, Field.Store.YES));

        return document;
    }

    private void markAsDirty(String fileName) {
        for (Path file : files) {
            if (fileName.equals(file.getFileName().toString())) {
                dirtyFiles.add(file);
                return;
            }
        }
    }

    private interface IndexUpdateTask {
        void update() throws IOException;
    }

    public Properties loadJavaProperties(Path file) throws IOException {
        try (InputStream inputStream = Files.newInputStream(file)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        }
    }

    private void writeJavaProperties(Path file, Properties properties) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(file)) {
            properties.store(outputStream, null);
        }
    }

    private static class CleanProperties extends Properties {
        private static class StripFirstLineStream extends FilterOutputStream {

            private boolean firstLineSeen = false;

            public StripFirstLineStream(final OutputStream out) {
                super(out);
            }

            @Override
            public void write(final int b) throws IOException {
                if (firstLineSeen) {
                    super.write(b);
                } else if (b == '\n') {
                    firstLineSeen = true;
                }
            }
        }

        @Override
        public synchronized Enumeration<Object> keys() {
            return Collections.enumeration(new TreeSet<>(super.keySet()));
        }

        @Override
        public void store(final OutputStream out, final String comments) throws IOException {
            super.store(new StripFirstLineStream(out), comments);
        }
    }
}

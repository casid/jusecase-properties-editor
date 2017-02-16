package org.jusecase.properties.gateways;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Singleton
public class LucenePropertiesGateway implements PropertiesGateway {
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String FILE_NAME = "fileName";

    private final PropertyParser propertyParser = new PropertyParserNative();

    private List<Path> files;
    private List<String> keys;

    private RAMDirectory ramDirectory;
    private IndexSearcher indexSearcher;

    public LucenePropertiesGateway() {
        ramDirectory = new RAMDirectory();
    }

    @Override
    public void loadProperties(List<Path> files) {
        this.files = files;
        this.keys = null;

        try (IndexWriter writer = new IndexWriter(ramDirectory, new IndexWriterConfig())) {
            for (Path file : files) {
                loadProperties(writer, file);
            }

            writer.commit();

        } catch (IOException e) {
            throw new RuntimeException("Failed to create lucene index!", e);
        }

        try {
            indexSearcher = new IndexSearcher(DirectoryReader.open(ramDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadProperties(IndexWriter writer, Path file) {
        try {
            String fileName = file.getFileName().toString();
            Map<String, String> properties = propertyParser.parse(file);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                Document document = new Document();
                document.add(new StringField(KEY, property.getKey(), Field.Store.YES));
                document.add(new TextField(VALUE, property.getValue(), Field.Store.YES));
                document.add(new StringField(FILE_NAME, fileName, Field.Store.YES));

                writer.addDocument(document);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                throw new RuntimeException(e);
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
            List<Property> properties = new ArrayList<>(result.totalHits);

            for (ScoreDoc scoreDoc : result.scoreDocs) {
                Document document = indexSearcher.getIndexReader().document(scoreDoc.doc);
                Property property = new Property();
                property.key = document.get(KEY);
                property.value = document.get(VALUE);
                property.fileName = document.get(FILE_NAME);
                properties.add(property);
            }

            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
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

    private boolean isInitialized() {
        return indexSearcher != null;
    }
}

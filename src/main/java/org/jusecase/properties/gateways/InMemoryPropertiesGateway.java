package org.jusecase.properties.gateways;

import org.apache.commons.codec.digest.DigestUtils;
import org.jusecase.properties.entities.Key;
import org.jusecase.properties.entities.KeyPopulation;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.usecases.Search;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
public class InMemoryPropertiesGateway implements PropertiesGateway {
    private List<Path> files;
    private Set<String> fileNames;
    private Set<Path> dirtyFiles;
    private Map<String, FileSnapshot> fileSnapshots;
    private Set<String> keys;
    private Map<String, Key> keyPool;
    private List<Property> properties;
    private Map<String, List<Property>> propertiesByKey;
    private String[] ignoreLocalesForKeyPopulation = {};
    private Set<String> ignoreFilesForKeyPopulation = new HashSet<>();
    private boolean initialized;
    private boolean useMultipleThreads = true;

    @Override
    public void loadProperties(List<Path> files) {
        initialized = false;

        this.files = files;
        this.fileNames = files.stream().map(f -> f.getFileName().toString()).collect(Collectors.toSet());
        this.dirtyFiles = new HashSet<>();
        this.fileSnapshots = new ConcurrentHashMap<>();
        this.keys = new TreeSet<>();
        this.keyPool = new HashMap<>();
        this.properties = new ArrayList<>();
        this.propertiesByKey = new HashMap<>();

        for ( String locale : ignoreLocalesForKeyPopulation ) {
            ignoreFilesForKeyPopulation.add(resolveFileName(locale));
        }

        loadProperties();
        initialized = true;
    }

    private void loadProperties() {
        List<LoadTask> loadTasks = new ArrayList<>();
        for (Path file : files) {
            loadTasks.add(new LoadTask(file));
        }

        if (useMultipleThreads) {
            loadTasks.parallelStream().forEach(LoadTask::load);
        } else {
            loadTasks.forEach(LoadTask::load);
        }

        for (LoadTask loadTask : loadTasks) {
            for (Property property : loadTask.properties) {
                addProperty(property);
            }
        }
    }

    private void updateFileSnapshot(Path file) throws IOException {
        String fileName = file.getFileName().toString();
        fileSnapshots.put(fileName, computeFileSnapshot(file));
    }

    private FileSnapshot computeFileSnapshot(Path file) throws IOException {
        FileSnapshot fileSnapshot = new FileSnapshot();
        fileSnapshot.bytes = Files.size(file);
        fileSnapshot.hash = computeFileHash(file);
        fileSnapshot.lineSeparator = guessLineSeparator(file);
        return fileSnapshot;
    }

    private String computeFileHash(Path file) throws IOException {
        try (InputStream is = Files.newInputStream(file)) {
            return DigestUtils.md5Hex(is);
        }
    }

    private String guessLineSeparator(Path file) throws IOException {
        String lineSeparator = System.lineSeparator();
        try (InputStreamReader is = new InputStreamReader(Files.newInputStream(file), "8859_1")) {
            int character;
            while ((character = is.read()) >= 0) {
                if (character == '\r') {
                    return "\r\n";
                } else if (character == '\n') {
                    return "\n";
                }
            }
        }
        return lineSeparator;
    }

    private void addProperty(Property property) {
        this.keys.add(property.key);
        this.properties.add(property);
        List<Property> propertiesByKey = this.propertiesByKey.computeIfAbsent(property.key, s -> new ArrayList<>());
        propertiesByKey.add(property);
        updateKeyState(property.key, propertiesByKey);
    }

    private void updateKeyState(String key, List<Property> propertiesByKey) {
        int propertiesWithContent = 0;
        if (propertiesByKey != null) {
            propertiesWithContent = ignoreFilesForKeyPopulation.size();
            for (Property property : propertiesByKey) {
                if (property.key != null && !ignoreFilesForKeyPopulation.contains(property.fileName)) {
                    ++propertiesWithContent;
                }
            }
        }

        if (propertiesWithContent < files.size()) {
            getKey(key).setPopulation(KeyPopulation.Sparse);
        } else {
            getKey(key).setPopulation(KeyPopulation.Complete);
        }
    }

    @Override
    public Key getKey(String key) {
        return keyPool.computeIfAbsent(key, s -> new Key(key));
    }

    @Override
    public List<Key> getKeys() {
        if (!isInitialized()) {
            return new ArrayList<>();
        }

        return getKeys(keys);
    }

    private List<Key> getKeys(Collection<String> keys) {
        return keys.stream().map(this::getKey).collect(Collectors.toList());
    }

    @Override
    public List<Property> getProperties(String key) {
        if (!isInitialized()) {
            return new ArrayList<>();
        }

        List<Property> properties = propertiesByKey.get(key);
        if (properties == null) {
            return new ArrayList<>();
        }

        Map<String, String> fileNameToValue = new HashMap<>();
        for (Property property : properties) {
            fileNameToValue.put(property.fileName, property.value);
        }

        List<Property> result = new ArrayList<>(files.size());
        for (Path file : files) {
            Property property = new Property();
            property.key = key;
            property.fileName = file.getFileName().toString();
            property.value = fileNameToValue.get(property.fileName);
            result.add(property);
        }

        return result;
    }

    @Override
    public void renameKey(String key, String newKey) {
        if (!isInitialized() || !propertiesByKey.containsKey(key)) {
            return;
        }

        if (keys.contains(newKey)) {
            throw new GatewayException("A key with this name already exists");
        }

        keys.remove(key);
        keys.add(newKey);

        List<Property> properties = propertiesByKey.remove(key);
        for (Property property : properties) {
            property.key = newKey;
        }
        propertiesByKey.put(newKey, properties);

        markAsDirty();
    }

    @Override
    public void duplicateKey(String key, String newKey) {
        if (!isInitialized() || !propertiesByKey.containsKey(key)) {
            return;
        }

        if (keys.contains(newKey)) {
            throw new GatewayException("A key with this name already exists");
        }

        for (Property property : propertiesByKey.get(key)) {
            Property newProperty = new Property();
            newProperty.key = newKey;
            newProperty.fileName = property.fileName;
            newProperty.value = property.value;
            newProperty.valueLowercase = property.value.toLowerCase();
            addProperty(newProperty);

            markAsDirty(property.fileName);
        }
    }

    @Override
    public void deleteKey(String key) {
        if (!isInitialized() || !propertiesByKey.containsKey(key)) {
            return;
        }

        for (Property property : propertiesByKey.get(key)) {
            removePropertyFromList(properties, property);
            markAsDirty(property.fileName);
        }

        propertiesByKey.remove(key);
        keys.remove(key);
    }

    @Override
    public void setIgnoreLocalesForKeyPopulation( String... locales ) {
        ignoreLocalesForKeyPopulation = locales;
    }

    @Override
    public List<Key> search(Search.Request request) {
        if (!isInitialized()) {
            return new ArrayList<>();
        }

        if (request.keysToSearch != null) {
            List<Property> properties = new ArrayList<>();
            Iterator<String> iterator = request.keysToSearch.iterator();
            while (iterator.hasNext()) {
                List<Property> propertiesByKey = this.propertiesByKey.get(iterator.next());
                if (propertiesByKey != null) {
                    properties.addAll(propertiesByKey);
                } else {
                    iterator.remove();
                }
            }

            return search(request, request.keysToSearch, properties);
        } else {
            return search(request, keys, properties);
        }
    }

    private List<Key> search(Search.Request request, Collection<String> keys, List<Property> properties) {
        if (request.query.isEmpty()) {
            return getKeys(keys);
        }

        Set<Key> result = ConcurrentHashMap.newKeySet();
        KeyMatcher keyMatcher = createKeyMatcher(request, result);
        ValueMatcher valueMatcher = createValueMatcher(request, result);

        if (useMultipleThreads) {
            keys.parallelStream().forEach(keyMatcher);
            properties.parallelStream().forEach(valueMatcher);
        } else {
            keys.forEach(keyMatcher);
            properties.forEach(valueMatcher);
        }

        ArrayList<Key> resultKeys = new ArrayList<>(result);
        Collections.sort(resultKeys);

        return resultKeys;
    }

    private KeyMatcher createKeyMatcher(Search.Request request, Set<Key> result) {
        if (request.regex) {
            return new RegexKeyMatcher(request, result);
        } else {
            return new ContainsKeyMatcher(request, result);
        }
    }

    private ValueMatcher createValueMatcher(Search.Request request, Set<Key> result) {
        if (request.regex) {
            return new RegexValueMatcher(request, result);
        } else {
            return new ContainsValueMatcher(request, result);
        }
    }

    @Override
    public void updateValue(Property property) {
        if (!isInitialized()) {
            return;
        }

        if (property.value == null) {
            deleteProperty(property);
        } else {
            List<Property> storedProperties = propertiesByKey.get(property.key);
            Property propertyInList = findPropertyInList(storedProperties, property);
            if (propertyInList != null) {
                propertyInList.value = property.value;
                propertyInList.valueLowercase = property.value.toLowerCase();
            } else {
                property.valueLowercase = property.value.toLowerCase();
                addProperty(property);
            }
        }
        markAsDirty(property.fileName);
    }

    private void deleteProperty(Property property) {
        removePropertyFromList(properties, property);
        List<Property> propertiesByKey = this.propertiesByKey.get(property.key);
        if (propertiesByKey != null) {
            removePropertyFromList(propertiesByKey, property);
        }
        updateKeyState(property.key, propertiesByKey);
    }

    private void removePropertyFromList(List<Property> properties, Property property) {
        int indexToDelete = -1;
        int i = 0;
        for (Property p : properties) {
            if (p.key.equals(property.key) && p.fileName.equals(property.fileName)) {
                indexToDelete = i;
                break;
            }
            ++i;
        }

        if (indexToDelete != -1) {
            properties.remove(indexToDelete);
        }
    }

    private Property findPropertyInList(List<Property> properties, Property property) {
        for (Property p : properties) {
            if (p.key.equals(property.key) && p.fileName.equals(property.fileName)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void addKey(String key) {
        if (!isInitialized()) {
            return;
        }

        if (keys.contains(key)) {
            throw new GatewayException("A key with this name already exists");
        }

        Path file = files.get(0);

        Property property = new Property();
        property.key = key;
        property.value = property.valueLowercase = "";
        property.fileName = file.getFileName().toString();
        addProperty(property);

        markAsDirty(property.fileName);
    }

    @Override
    public void save() {
        if (isInitialized()) {
            List<Path> filesToSave = files.stream().filter(file -> dirtyFiles.contains(file)).collect(Collectors.toList());
            saveFiles(filesToSave);
            dirtyFiles.clear();
        }
    }

    @Override
    public void saveAll() {
        if (isInitialized()) {
            saveFiles(files);
        }
    }

    private void saveFiles(List<Path> files) {
        if (useMultipleThreads) {
            files.parallelStream().forEach(this::save);
        } else {
            files.forEach(this::save);
        }
    }

    @Override
    public void addProperties(List<Property> properties) {
        if (!isInitialized()) {
            return;
        }

        for (Property property : properties) {
            if (!fileNames.contains(property.fileName)) {
                throw new GatewayException("Unknown file name " + property.fileName + " for property");
            }
        }

        properties.forEach(property -> {
            if (keys.contains(property.key)) {
                deleteProperty(property);
            }

            if (property.value != null) {
                Property propertyClone = new Property();
                propertyClone.key = property.key;
                propertyClone.value = property.value;
                propertyClone.valueLowercase = property.value.toLowerCase();
                propertyClone.fileName = property.fileName;
                addProperty(propertyClone);

                markAsDirty(property.fileName);
            }
        });
    }

    @Override
    public void deleteProperties(List<Property> properties) {
        if (!isInitialized()) {
            return;
        }

        for (Property property : properties) {
            deleteProperty(property);
            markAsDirty(property.fileName);
        }

        for (Property property : properties) {
            List<Property> propertiesByKey = this.propertiesByKey.get(property.key);
            if (propertiesByKey != null && propertiesByKey.isEmpty()) {
                this.propertiesByKey.remove(property.key);
                this.keys.remove(property.key);
            }
        }
    }

    @Override
    public boolean hasUnsavedChanges() {
        return isInitialized() && !dirtyFiles.isEmpty();
    }

    @Override
    public boolean hasExternalChanges() {
        if (!isInitialized()) {
            return false;
        }

        for (Path file : files) {
            try {
                FileSnapshot lastSnapshot = fileSnapshots.get(file.getFileName().toString());
                FileSnapshot currentSnapshot = computeFileSnapshot(file);
                if (lastSnapshot.bytes != currentSnapshot.bytes || !Objects.equals(lastSnapshot.hash, currentSnapshot.hash)) {
                    return true;
                }
            } catch (IOException e) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void updateFileSnapshots() {
        for (Path file : files) {
            try {
                updateFileSnapshot(file);
            } catch (IOException e) {
                throw new GatewayException("Failed to update file snapshot for " + file, e);
            }
        }
    }

    @Override
    public String resolveFileName(String locale) {
        String expectedFileName = resolveBundleName();
        if (expectedFileName != null) {
            if (!locale.isEmpty()) {
                expectedFileName += "_" + locale;
            }
            expectedFileName += ".properties";

            for (String fileName : fileNames) {
                if (fileName.equals(expectedFileName)) {
                    return fileName;
                }
            }
        }
        return null;
    }

    private String resolveBundleName() {
        if (fileNames == null || fileNames.isEmpty()) {
            return null;
        }

        String bundleName = fileNames.iterator().next();
        int index = bundleName.indexOf('_');
        if (index >= 0) {
            return bundleName.substring(0, index);
        }

        index = bundleName.lastIndexOf('.');
        return bundleName.substring(index);
    }

    private void save(Path file) {
        try {
            CleanProperties properties = new CleanProperties();

            String fileName = file.getFileName().toString();
            for (Property property : this.properties) {
                if (fileName.equals(property.fileName)) {
                    properties.put(property.key, property.value);
                }
            }

            writeJavaProperties(file, properties, fileSnapshots.get(fileName).lineSeparator);
            updateFileSnapshot(file);
        } catch (IOException e) {
            throw new GatewayException("Failed to save properties to " + file, e);
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    private void markAsDirty(String fileName) {
        for (Path file : files) {
            if (fileName.equals(file.getFileName().toString())) {
                dirtyFiles.add(file);
                return;
            }
        }
    }

    private void markAsDirty() {
        dirtyFiles.addAll(files);
    }

    private Properties loadJavaProperties(Path file) throws IOException {
        try (InputStream inputStream = Files.newInputStream(file)) {
            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Properties properties = new Properties();
                properties.load(reader);
                return properties;
            }
        }
    }

    private void writeJavaProperties(Path file, CleanProperties properties, String lineSeparator) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(file)) {
            properties.storeSpecial(outputStream, lineSeparator);
        }
    }

    @Override
    public List<Path> getFiles() {
        return files;
    }

    public void setUseMultipleThreads(boolean useMultipleThreads) {
        this.useMultipleThreads = useMultipleThreads;
    }

    private static class FileSnapshot {
        long bytes;
        String hash;
        String lineSeparator;
    }

    private class LoadTask {
        private final Path file;
        private final List<Property> properties = new ArrayList<>();

        LoadTask(Path file) {
            this.file = file;
        }

        void load() {
            try {
                Properties properties = loadJavaProperties(file);
                updateFileSnapshot(file);

                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    Property property = new Property();
                    property.fileName = file.getFileName().toString();
                    property.key = entry.getKey().toString();
                    property.value = entry.getValue().toString();
                    property.valueLowercase = property.value.toLowerCase();

                    this.properties.add(property);
                }
            } catch (IOException e) {
                throw new GatewayException("Failed to load properties!", e);
            }
        }

        public List<Property> getProperties() {
            return properties;
        }
    }

    private abstract class KeyMatcher implements Consumer<String> {
        final String queryString;
        final Set<Key> result;

        KeyMatcher( Search.Request request, Set<Key> result ) {
            this.queryString = request.query;
            this.result = result;
        }

        @Override
        public void accept( String key ) {
            if (isMatch(key)) {
                result.add(getKey(key));
            }
        }

        protected abstract boolean isMatch( String key );
    }

    private class ContainsKeyMatcher extends KeyMatcher {

        ContainsKeyMatcher( Search.Request request, Set<Key> result ) {
            super(request, result);
        }

        @Override
        protected boolean isMatch( String key ) {
            return key.contains(queryString);
        }
    }

    private class RegexKeyMatcher extends KeyMatcher {

        final Pattern queryPattern;

        RegexKeyMatcher( Search.Request request, Set<Key> result ) {
            super(request, result);
            queryPattern = Pattern.compile(queryString);
        }

        @Override
        protected boolean isMatch( String key ) {
            return queryPattern.matcher(key).matches();
        }
    }

    private abstract class ValueMatcher implements Consumer<Property> {
        final Search.Request request;
        final String queryString;
        final Set<Key> result;

        ValueMatcher( Search.Request request, Set<Key> result ) {
            this.request = request;
            if (request.caseSensitive) {
                this.queryString = request.query;
            } else {
                this.queryString = request.query.toLowerCase();
            }
            this.result = result;
        }

        @Override
        public void accept( Property property ) {
            if (!result.contains(getKey(property.key)) && isMatch(property)) {
                result.add(getKey(property.key));
            }
        }

        protected abstract boolean isMatch( Property property );
    }

    private class ContainsValueMatcher extends ValueMatcher {

        ContainsValueMatcher( Search.Request request, Set<Key> result ) {
            super(request, result);
        }

        @Override
        protected boolean isMatch( Property property ) {
            if (request.caseSensitive) {
                return property.value.contains(queryString);
            } else {
                return property.valueLowercase.contains(queryString);
            }
        }
    }

    private class RegexValueMatcher extends ValueMatcher {

        final Pattern queryPattern;

        RegexValueMatcher( Search.Request request, Set<Key> result ) {
            super(request, result);
            queryPattern = Pattern.compile(queryString);
        }

        @Override
        protected boolean isMatch( Property property ) {
            if (request.caseSensitive) {
                return queryPattern.matcher(property.value).matches();
            } else {
                return queryPattern.matcher(property.valueLowercase).matches();
            }
        }
    }
}

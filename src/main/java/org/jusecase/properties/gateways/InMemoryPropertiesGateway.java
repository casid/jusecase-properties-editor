package org.jusecase.properties.gateways;

import org.jusecase.properties.entities.Property;

import javax.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Singleton
public class InMemoryPropertiesGateway implements PropertiesGateway {
    private List<Path> files;
    private Set<Path> dirtyFiles = new HashSet<>();
    private Set<String> keys;
    private List<Property> properties;
    private Map<String, List<Property>> propertiesByKey;
    private boolean initialized;

    @Override
    public void loadProperties(List<Path> files) {
        initialized = false;

        this.files = files;
        this.dirtyFiles.clear();
        this.keys = new TreeSet<>();
        this.properties = new ArrayList<>();
        this.propertiesByKey = new HashMap<>();

        try {
            for (Path file : files) {
                loadProperties(file);
            }

            initialized = true;
        } catch (IOException e) {
            throw new GatewayException("Failed to load properties!", e);
        }
    }

    private void loadProperties(Path file) throws IOException {
        Properties properties = loadJavaProperties(file);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            Property property = new Property();
            property.fileName = file.getFileName().toString();
            property.key = entry.getKey().toString();
            property.value = entry.getValue().toString();

            addProperty(property);
        }
    }

    private void addProperty( Property property ) {
        this.keys.add(property.key);
        this.properties.add(property);
        List<Property> propertiesByKey = this.propertiesByKey.computeIfAbsent(property.key, s -> new ArrayList<>());
        propertiesByKey.add(property);
    }

    @Override
    public List<String> getKeys() {
        if (!isInitialized()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(keys);
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
    public void renameKey( String key, String newKey ) {
        if (!isInitialized() || !propertiesByKey.containsKey(key)) {
            return;
        }

        if (keys.contains(newKey)) {
            throw new GatewayException("A key with this name already exists");
        }

        keys.remove(key);
        keys.add(newKey);

        List<Property> properties = propertiesByKey.remove(key);
        for ( Property property : properties ) {
            property.key = newKey;
        }
        propertiesByKey.put(newKey, properties);

        markAsDirty();
    }

    @Override
    public void duplicateKey( String key, String newKey ) {
        if (!isInitialized() || !propertiesByKey.containsKey(key)) {
            return;
        }

        if (keys.contains(newKey)) {
            throw new GatewayException("A key with this name already exists");
        }

        for ( Property property : propertiesByKey.get(key) ) {
            Property newProperty = new Property();
            newProperty.key = newKey;
            newProperty.fileName = property.fileName;
            newProperty.value = property.value;
            addProperty(newProperty);

            markAsDirty(property.fileName);
        }
    }

    @Override
    public List<String> search(String queryString) {
        if (!isInitialized() || queryString.isEmpty()) {
            return getKeys();
        }

        Set<String> result = new TreeSet<>();
        for ( String key : getKeys() ) {
            if (key.contains(queryString)) {
                result.add(key);
            }
        }

        for (Property property : properties) {
            if (!result.contains(property.key)) {
                if (property.value.contains(queryString)) {
                    result.add(property.key);
                }
            }
        }

        return new ArrayList<>(result);
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
    }

    private void removePropertyFromList(List<Property> properties, Property property) {
        int indexToDelete = -1;
        int i = 0;
        for ( Property p : properties ) {
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
        for ( Property p : properties ) {
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

        if (getKeys().contains(key)) {
            throw new GatewayException("A key with this name already exists");
        }

        Path file = files.get(0);

        Property property = new Property();
        property.key = key;
        property.value = "";
        property.fileName = file.getFileName().toString();
        addProperty(property);

        markAsDirty(property.fileName);
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
            files.parallelStream().forEach(this::save);
        }
    }

    private void save(Path file) {
        try {
            Properties properties = new CleanProperties();

            String fileName = file.getFileName().toString();
            for (Property property : this.properties) {
                if (fileName.equals(property.fileName)) {
                    properties.put(property.key, property.value);
                }
            }

            writeJavaProperties(file, properties);
        } catch (IOException e) {
            throw new GatewayException("Failed to save properties to " + file, e);
        }
    }

    private boolean isInitialized() {
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
}
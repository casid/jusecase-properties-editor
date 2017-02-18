package org.jusecase.properties.gateways;

import org.jusecase.properties.entities.Property;

import java.nio.file.Path;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.testPath;

public class PropertiesGatewayTrainer implements PropertiesGateway {
    private Set<Path> loadedProperties = new HashSet<>();
    private Property updatedValue;
    private String addedKey;
    private String deletedKey;
    private String duplicatedKey;
    private Map<String, List<Property>> propertiesForKey = new HashMap<>();
    private List<Property> addedProperties;

    @Override
    public void loadProperties(List<Path> files) {
        loadedProperties.addAll(files);
    }

    @Override
    public List<String> getKeys() {
        return null;
    }

    @Override
    public List<Property> getProperties(String key) {
        return propertiesForKey.get(key);
    }

    @Override
    public void renameKey( String key, String newKey ) {

    }

    @Override
    public void duplicateKey( String key, String newKey ) {
        duplicatedKey = key + "->" + newKey;
    }

    @Override
    public void deleteKey( String key ) {
        deletedKey = key;
    }

    @Override
    public List<String> search(String query) {
        return null;
    }

    @Override
    public void updateValue(Property property) {
        updatedValue = property;
    }

    @Override
    public void addKey(String key) {
        addedKey = key;
    }

    @Override
    public void save() {

    }

    @Override
    public void saveAll() {

    }

    @Override
    public void addProperties(List<Property> properties) {
        addedProperties = properties;
    }

    public void thenLoadedPropertiesAre(Set<Path> expected) {
        assertThat(loadedProperties).isEqualTo(expected);
    }

    public void thenLoadedPropertiesAre(String... fileNamesInTestResources) {
        Set<Path> expected = new HashSet<>();
        for (String fileName : fileNamesInTestResources) {
            expected.add(a(testPath(fileName)));
        }

        thenLoadedPropertiesAre(expected);
    }

    public void thenNoPropertiesAreLoaded() {
        assertThat(loadedProperties).isEmpty();
    }

    public void thenNoValueIsUpdated() {

    }

    public Property getUpdatedValue() {
        return updatedValue;
    }

    public void thenNoKeyIsAdded() {
        assertThat(addedKey).isNull();
    }

    public String getAddedKey() {
        return addedKey;
    }

    public String getDeletedKey() {
        return deletedKey;
    }

    public void givenProperties(String key, List<Property> properties) {
        propertiesForKey.put(key, properties);
    }

    public List<Property> getAddedProperties() {
        return addedProperties;
    }

    public String getDuplicatedKey() {
        return duplicatedKey;
    }
}
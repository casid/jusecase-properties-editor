package org.jusecase.properties.gateways;

import org.jusecase.properties.entities.Key;
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
    private List<Property> deletedProperties;
    private String renamedKey;
    private boolean externalChanges;
    private boolean unsavedChanges;

    @Override
    public void loadProperties(List<Path> files) {
        loadedProperties.addAll(files);
    }

    @Override
    public Key getKey(String key) {
        return null;
    }

    @Override
    public List<Key> getKeys() {
        return null;
    }

    @Override
    public List<Property> getProperties(String key) {
        return propertiesForKey.get(key);
    }

    @Override
    public void renameKey( String key, String newKey ) {
        renamedKey = key + "->" + newKey;
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
    public List<Key> search( String query, boolean regex ) {
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

    @Override
    public void deleteProperties( List<Property> properties ) {
        deletedProperties = properties;
    }

    @Override
    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }

    @Override
    public boolean hasExternalChanges() {
        return externalChanges;
    }

    @Override
    public void updateFileSnapshots() {

    }

   @Override
   public String resolveFileName( String locale ) {
      if ("unknown".equals(locale)) {
          return null;
      }
      if (locale.isEmpty()) {
         return "resources.properties";
      }

      return "resources_" + locale + ".properties";
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

    public List<Property> getDeletedProperties() {
        return deletedProperties;
    }

    public String getDuplicatedKey() {
        return duplicatedKey;
    }

    public String getRenamedKey() {
        return renamedKey;
    }

    public void givenExternalChanges(boolean externalChanges) {
        this.externalChanges = externalChanges;
    }

    public void givenUnsavedChanges(boolean unsavedChanges) {
        this.unsavedChanges = unsavedChanges;
    }
}
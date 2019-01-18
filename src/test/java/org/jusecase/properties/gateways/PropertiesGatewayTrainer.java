package org.jusecase.properties.gateways;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.testPath;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jusecase.properties.entities.Key;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.usecases.Search;


public class PropertiesGatewayTrainer implements PropertiesGateway {

   private Set<Path>                   loadedProperties = new HashSet<>();
   private Property                    updatedValue;
   private String                      addedKey;
   private String                      deletedKey;
   private String                      duplicatedKey;
   private Map<String, List<Property>> propertiesForKey = new HashMap<>();
   private List<Property>              addedProperties;
   private List<Property>              deletedProperties;
   private String                      renamedKey;
   private boolean                     externalChanges;
   private boolean                     unsavedChanges;
   private List<Path>                  files;
   private List<Key>                   searchResults    = new ArrayList<>();
   private boolean                     initialized      = true;


   @Override
   public void addKey( String key ) {
      addedKey = key;
   }

   @Override
   public void addProperties( List<Property> properties ) {
      addedProperties = properties;
   }

   @Override
   public void deleteKey( String key ) {
      deletedKey = key;
   }

   @Override
   public void deleteProperties( List<Property> properties ) {
      deletedProperties = properties;
   }

   @Override
   public void duplicateKey( String key, String newKey ) {
      duplicatedKey = key + "->" + newKey;
   }

   public String getAddedKey() {
      return addedKey;
   }

   public List<Property> getAddedProperties() {
      return addedProperties;
   }

   public String getDeletedKey() {
      return deletedKey;
   }

   public List<Property> getDeletedProperties() {
      return deletedProperties;
   }

   public String getDuplicatedKey() {
      return duplicatedKey;
   }

   @Override
   public List<Path> getFiles() {
      return files;
   }

   @Override
   public Key getKey( String key ) {
      return null;
   }

   @Override
   public List<Key> getKeys() {
      return null;
   }

   @Override
   public List<Property> getProperties( String key ) {
      return propertiesForKey.get(key);
   }

   public String getRenamedKey() {
      return renamedKey;
   }

   public Property getUpdatedValue() {
      return updatedValue;
   }

   public void givenExternalChanges( boolean externalChanges ) {
      this.externalChanges = externalChanges;
   }

   public void givenFiles( List<Path> files ) {
      this.files = files;
   }

   public void givenInitialized( boolean initialized ) {
      this.initialized = initialized;
   }

   public void givenProperties( String key, List<Property> properties ) {
      propertiesForKey.put(key, properties);
   }

   public void givenUnsavedChanges( boolean unsavedChanges ) {
      this.unsavedChanges = unsavedChanges;
   }

   public void givenSearchResults( Key... searchResults ) {
      this.searchResults = Arrays.asList(searchResults);
   }

   @Override
   public boolean hasExternalChanges() {
      return externalChanges;
   }

   @Override
   public boolean hasUnsavedChanges() {
      return unsavedChanges;
   }

   @Override
   public boolean isInitialized() {
      return initialized;
   }

   @Override
   public void loadProperties( List<Path> files ) {
      loadedProperties.addAll(files);
   }

   @Override
   public void renameKey( String key, String newKey ) {
      renamedKey = key + "->" + newKey;
   }

   @Override
   public String resolveFileName( String locale ) {
      if ( "unknown".equals(locale) ) {
         return null;
      }
      if ( locale.isEmpty() ) {
         return "resources.properties";
      }

      return "resources_" + locale + ".properties";
   }

   @Override
   public void save() {

   }

   @Override
   public void saveAll() {

   }

   @Override
   public List<Key> search( Search.Request request ) {
      return searchResults;
   }

   public void thenLoadedPropertiesAre( Set<Path> expected ) {
      assertThat(loadedProperties).isEqualTo(expected);
   }

   public void thenLoadedPropertiesAre( String... fileNamesInTestResources ) {
      Set<Path> expected = new HashSet<>();
      for ( String fileName : fileNamesInTestResources ) {
         expected.add(a(testPath(fileName)));
      }

      thenLoadedPropertiesAre(expected);
   }

   public void thenNoKeyIsAdded() {
      assertThat(addedKey).isNull();
   }

   public void thenNoPropertiesAreLoaded() {
      assertThat(loadedProperties).isEmpty();
   }

   public void thenNoValueIsUpdated() {

   }

   @Override
   public void updateFileSnapshots() {

   }

   @Override
   public void updateValue( Property property ) {
      updatedValue = property;
   }
}
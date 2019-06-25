package org.jusecase.properties.gateways;

import org.jusecase.properties.entities.Key;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.usecases.Search;

import java.nio.file.Path;
import java.util.List;

public interface PropertiesGateway {

    boolean isInitialized();

    void loadProperties(List<Path> files);

    Key getKey(String key);

    List<Key> getKeys();

    List<Key> search(Search.Request request);

    List<Property> getProperties(String key);

    void renameKey( String key, String newKey );

    void duplicateKey( String key, String newKey );

    void deleteKey( String key );

    void setIgnoreLocalesForKeyPopulation( String ... locales );

    void updateValue(Property property);

    void addKey(String key);

    void save();

    void saveAll();

    void addProperties(List<Property> properties);

    void deleteProperties(List<Property> properties);

    boolean hasUnsavedChanges();

    boolean hasExternalChanges();

    void updateFileSnapshots();

    String resolveFileName(String locale);

    List<Path> getFiles();
}

package org.jusecase.properties.gateways;

import org.jusecase.properties.entities.Key;
import org.jusecase.properties.entities.Property;

import java.nio.file.Path;
import java.util.List;

public interface PropertiesGateway {

    void loadProperties(List<Path> files);

    Key getKey(String key);

    List<Key> getKeys();

    List<Key> search(String query);

    List<Property> getProperties(String key);

    void renameKey( String key, String newKey );

    void duplicateKey( String key, String newKey );

    void deleteKey( String key );

    void updateValue(Property property);

    void addKey(String key);

    void save();

    void saveAll();

    void addProperties(List<Property> properties);

    boolean hasUnsavedChanges();

    boolean hasExternalChanges();

    void updateFileSnapshots();
}

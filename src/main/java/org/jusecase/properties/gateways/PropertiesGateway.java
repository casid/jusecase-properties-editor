package org.jusecase.properties.gateways;

import org.jusecase.properties.entities.Property;

import java.nio.file.Path;
import java.util.List;

public interface PropertiesGateway {

    void loadProperties(List<Path> files);

    List<String> getKeys();

    List<Property> getProperties(String key);

    void renameKey( String key, String newKey );

    void duplicateKey( String key, String newKey );

    void deleteKey( String key );

    List<String> search(String query);

    void updateValue(Property property);

    void addKey(String key);

    void save();

    void saveAll();
}

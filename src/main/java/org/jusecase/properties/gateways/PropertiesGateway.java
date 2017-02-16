package org.jusecase.properties.gateways;

import java.nio.file.Path;
import java.util.List;

public interface PropertiesGateway {
    void loadProperties(List<Path> files);

    List<String> getKeys();

    List<Property> getProperties(String key);

    List<String> search(String query);
}

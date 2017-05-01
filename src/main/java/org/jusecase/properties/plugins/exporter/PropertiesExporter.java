package org.jusecase.properties.plugins.exporter;

import org.jusecase.properties.entities.Property;
import org.jusecase.properties.plugins.Plugin;

import java.util.List;


public interface PropertiesExporter extends Plugin {
    void exportProperties(List<Property> properties);
}

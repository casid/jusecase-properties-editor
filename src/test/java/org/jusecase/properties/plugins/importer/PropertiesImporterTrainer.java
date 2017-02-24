package org.jusecase.properties.plugins.importer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jusecase.properties.entities.Property;
import org.jusecase.properties.plugins.PluginTrainer;


public class PropertiesImporterTrainer extends PluginTrainer implements PropertiesImporter {

    List<Property> properties = new ArrayList<>();

    public void givenProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public List<Property> importProperties( Path file ) {
        return properties;
    }
}
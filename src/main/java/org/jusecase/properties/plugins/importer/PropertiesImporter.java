package org.jusecase.properties.plugins.importer;

import java.nio.file.Path;
import java.util.List;

import org.jusecase.properties.entities.Property;
import org.jusecase.properties.plugins.Plugin;


public interface PropertiesImporter extends Plugin {
    List<Property> importProperties( Path file );
}

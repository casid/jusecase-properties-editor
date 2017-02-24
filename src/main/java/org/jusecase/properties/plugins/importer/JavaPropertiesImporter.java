package org.jusecase.properties.plugins.importer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jusecase.properties.entities.Property;


public class JavaPropertiesImporter implements PropertiesImporter {

    @Override
    public String getPluginId() {
        return "jusecase-java-properties-importer";
    }

    @Override
    public String getPluginName() {
        return "Java Properties";
    }

    @Override
    public List<Property> importProperties( Path file ) {
        String locale = guessLocale(file);

        try (InputStream inputStream = Files.newInputStream(file)) {
            Properties javaProperties = new Properties();
            javaProperties.load(inputStream);

            List<Property> properties = new ArrayList<>();
            for ( Map.Entry<Object, Object> javaProperty : javaProperties.entrySet() ) {
                Property property = new Property();
                property.fileName = locale;
                property.key = javaProperty.getKey().toString();
                property.value = javaProperty.getValue().toString();
                properties.add(property);
            }

            return properties;
        }
        catch ( IOException e ) {
            throw new RuntimeException("Failed to import java properties", e);
        }
    }

    private String guessLocale(Path file) {
        String fileName = file.getFileName().toString();
        int start = fileName.indexOf('_');
        if (start >= 0) {
            int end = fileName.indexOf('.');
            return fileName.substring(start + 1, end);
        }
        return "";
    }
}

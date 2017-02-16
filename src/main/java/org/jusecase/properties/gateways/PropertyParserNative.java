package org.jusecase.properties.gateways;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

public class PropertyParserNative implements PropertyParser {
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> parse(Path file) throws IOException {
        try (InputStream inputStream = Files.newInputStream(file)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return (Map)properties;
        }
    }
}

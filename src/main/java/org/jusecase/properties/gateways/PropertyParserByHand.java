package org.jusecase.properties.gateways;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class PropertyParserByHand implements PropertyParser {

    @Override
    public Map<String, String> parse(Path file) throws IOException {
        Map<String, String> properties = new HashMap<>();
        try (Stream<String> lines = Files.lines(file, StandardCharsets.ISO_8859_1)) {
            lines.forEach(line -> parseLine(properties, line));
        }
        return properties;
    }

    private void parseLine(Map<String, String> properties, String line) {
        String key = null;
        String value = null;

        int length = line.length();
        for (int i = 0; i < length; ++i) {
            char character = line.charAt(i);
            boolean assignmentCharacter = character == ' ' || character == '\t' || character == '=';
            if (key == null && assignmentCharacter) {
                key = line.substring(0, i);
            } else if (key != null && !assignmentCharacter) {
                value = line.substring(i);
                break;
            }
        }

        properties.put(key, value);
    }
}

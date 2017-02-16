package org.jusecase.properties.gateways;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface PropertyParser {
    Map<String, String> parse(Path file) throws IOException;
}

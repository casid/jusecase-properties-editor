package org.jusecase.properties.entities;

import org.jusecase.builders.Builder;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestPathBuilder implements Builder<Path> {
    private final String fileName;

    public TestPathBuilder(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Path build() {
        return Paths.get("src", "test", "resources", fileName);
    }
}

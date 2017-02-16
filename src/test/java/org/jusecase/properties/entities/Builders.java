package org.jusecase.properties.entities;

public class Builders {
    public static SettingsBuilder settings() {
        return new SettingsBuilder();
    }

    public static TestPathBuilder testPath(String fileName) {
        return new TestPathBuilder(fileName);
    }
}

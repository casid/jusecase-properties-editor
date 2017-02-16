package org.jusecase.properties.entities;

public class SettingsBuilder implements SettingsBuilderMethods<Settings, SettingsBuilder> {
    private Settings settings = new Settings();

    @Override
    public Settings getEntity() {
        return settings;
    }
}

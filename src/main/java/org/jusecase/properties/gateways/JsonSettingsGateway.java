package org.jusecase.properties.gateways;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jusecase.properties.entities.Settings;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class JsonSettingsGateway implements SettingsGateway {
    private final Path file;
    private final ObjectMapper mapper = new ObjectMapper();
    private Settings settings;

    @Inject
    public JsonSettingsGateway(@Named("settingsFile") Path file) {
        this.file = file;
    }

    @Override
    public Settings getSettings() {
        if (settings == null) {
            reloadFromDisk();
        }
        return settings;
    }

    @Override
    public void saveSettings(Settings settings) {
        try {
            this.settings = settings;
            Files.createDirectories(file.getParent());
            mapper.writeValue(file.toFile(), settings);
        } catch (IOException e) {
            throw new GatewayException("Failed to store app settings", e);
        }
    }

    @Override
    public void reloadFromDisk() {
        try {
            settings = mapper.readValue(file.toFile(), Settings.class);
        } catch (IOException e) {
            settings = new Settings(); // If no settings exist yet, create new ones
        }
    }
}

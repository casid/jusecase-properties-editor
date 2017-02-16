package org.jusecase.properties.gateways;

import org.jusecase.properties.entities.Settings;

public interface SettingsGateway {
    Settings getSettings();
    void saveSettings(Settings settings);
    void reloadFromDisk();
}

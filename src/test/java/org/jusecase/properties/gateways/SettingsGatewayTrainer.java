package org.jusecase.properties.gateways;

import org.assertj.core.api.Assertions;
import org.jusecase.properties.entities.Settings;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SettingsGatewayTrainer implements SettingsGateway {
    private Settings settings = new Settings();
    private Settings savedSettings;

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void saveSettings(Settings settings) {
        savedSettings = settings;
    }

    @Override
    public void reloadFromDisk() {

    }

    public void givenSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings getSavedSettings() {
        return savedSettings;
    }

    public void givenSearchHistory(String ... queries) {
        settings.searchHistory.addAll(Arrays.asList(queries));
    }

    public void thenSearchHistoryIs(String ... expected) {
        assertThat(settings.searchHistory).containsExactly(expected);
    }
}
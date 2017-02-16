package org.jusecase.properties.gateways;

import org.junit.Test;
import org.jusecase.properties.entities.Settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.settings;

public abstract class SettingsGatewayTest {
    protected SettingsGateway gateway;

    @Test
    public void nonExistingSettings() {
        Settings settings = gateway.getSettings();
        assertThat(settings).isNotNull();
    }

    @Test
    public void saveAndLoad() {
        Settings expected = a(settings()
                .withLastFile("/dev/null")
        );

        gateway.saveSettings(expected);
        gateway.reloadFromDisk();
        Settings actual = gateway.getSettings();

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }
}
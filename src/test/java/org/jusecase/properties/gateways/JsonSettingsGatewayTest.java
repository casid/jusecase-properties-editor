package org.jusecase.properties.gateways;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jusecase.properties.entities.Settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.settings;

public class JsonSettingsGatewayTest extends SettingsGatewayTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        gateway = new JsonSettingsGateway(temporaryFolder.getRoot().toPath().resolve("editor").resolve("settings.json"));
    }

    @Test
    public void roundTrip() {
        Settings settings = a(settings()
                .withLastFile("last-file.properties")
        );

        gateway.saveSettings(settings);
        gateway.reloadFromDisk();

        assertThat(gateway.getSettings()).isEqualToComparingFieldByField(settings);
    }
}
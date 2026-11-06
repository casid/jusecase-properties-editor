package org.jusecase.properties.gateways;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jusecase.properties.entities.Settings;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.settings;

public class JsonSettingsGatewayTest extends SettingsGatewayTest {

    @TempDir
    Path temporaryFolder;

    @BeforeEach
    public void setUp() throws Exception {
        gateway = new JsonSettingsGateway(temporaryFolder.resolve("editor").resolve("settings.json"));
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
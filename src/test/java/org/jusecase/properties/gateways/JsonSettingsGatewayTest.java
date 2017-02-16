package org.jusecase.properties.gateways;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class JsonSettingsGatewayTest extends SettingsGatewayTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        gateway = new JsonSettingsGateway(temporaryFolder.getRoot().toPath().resolve("editor").resolve("settings.json"));
    }
}
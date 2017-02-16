package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.gateways.SettingsGatewayTrainer;
import org.jusecase.properties.usecases.Initialize.Request;
import org.jusecase.properties.usecases.Initialize.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.settings;
import static org.jusecase.properties.entities.Builders.testPath;

public class InitializeTest extends UsecaseTest<Request, Response> {
    private PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();
    private SettingsGatewayTrainer settingsGatewayTrainer = new SettingsGatewayTrainer();

    @Before
    public void setUp() {
        usecase = new Initialize(propertiesGatewayTrainer, settingsGatewayTrainer);
        request = new Request();
    }

    @Test
    public void noLastFile() {
        givenLastFile(null);
        whenRequestIsExecuted();
        propertiesGatewayTrainer.thenNoPropertiesAreLoaded();
    }

    @Test
    public void lastFileThatDoesNotExist_noPropertiesAreLoaded() {
        givenLastFile(a(testPath("this-does-not-exist.properties")).toString());
        whenRequestIsExecuted();
        propertiesGatewayTrainer.thenNoPropertiesAreLoaded();
    }

    @Test
    public void lastFileThatExists_propertiesAreLoaded() {
        givenLastFile(a(testPath("resources.properties")).toString());
        whenRequestIsExecuted();
        thenLoadedPropertiesAre(
                "resources.properties",
                "resources_de.properties",
                "resources_es.properties"
        );
    }

    private void givenLastFile(String lastFile) {
        settingsGatewayTrainer.givenSettings(a(settings().withLastFile(lastFile)));
    }

    private void thenLoadedPropertiesAre(String... fileNames) {
        propertiesGatewayTrainer.thenLoadedPropertiesAre(fileNames);
        assertThat(response.fileNames).containsExactly(fileNames);
    }
}
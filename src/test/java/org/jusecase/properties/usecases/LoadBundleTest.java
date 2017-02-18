package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.gateways.SettingsGatewayTrainer;
import org.jusecase.properties.gateways.UndoableRequestGateway;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.testPath;

public class LoadBundleTest extends UsecaseTest<LoadBundle.Request, LoadBundle.Response> {
    private PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();
    private SettingsGatewayTrainer settingsGatewayTrainer = new SettingsGatewayTrainer();
    private UndoableRequestGateway undoableRequestGateway = new UndoableRequestGateway();

    @Before
    public void setUp() throws Exception {
        usecase = new LoadBundle(propertiesGatewayTrainer, settingsGatewayTrainer, undoableRequestGateway);
        request = new LoadBundle.Request();
    }

    @Test
    public void missingPropertiesFile() {
        request.propertiesFile = null;
        whenRequestIsExecuted();
        thenErrorMessageIs("Properties file must not be null!");
    }

    @Test
    public void nonExistingPropertiesFile() {
        request.propertiesFile = Paths.get("foo", "bar", "probably-this-file-does-not-exist-on-any.system.lol");
        whenRequestIsExecuted();
        thenErrorMessageIs("The selected file does not exist!");
    }

    @Test
    public void selectedPropertiesFile() {
        givenPropertiesFile("resources.properties");
        whenRequestIsExecuted();
        thenLoadedPropertiesAre("resources.properties", "resources_de.properties", "resources_es.properties");
    }

    @Test
    public void selectedPropertiesFile_notRootLocale() {
        givenPropertiesFile("resources_de.properties");
        whenRequestIsExecuted();
        thenLoadedPropertiesAre("resources.properties", "resources_de.properties", "resources_es.properties");
    }

    @Test
    public void selectedPropertiesFile_pathIsStored() {
        givenPropertiesFile("resources_de.properties");
        whenRequestIsExecuted();
        assertThat(settingsGatewayTrainer.getSavedSettings().lastFile).isEqualTo(a(testPath("resources_de.properties")).toString());
    }

    @Test
    public void selectedPropertiesFile_undoHistoryIsCleared() {
        givenPropertiesFile("resources.properties");
        undoableRequestGateway.add(new UndoableRequest());

        whenRequestIsExecuted();

        assertThat(undoableRequestGateway.size()).isEqualTo(0);
    }

    private void thenLoadedPropertiesAre(String... fileNames) {
        propertiesGatewayTrainer.thenLoadedPropertiesAre(fileNames);
        assertThat(response.fileNames).containsExactly(fileNames);
    }

    private void givenPropertiesFile(String fileName) {
        request.propertiesFile = a(testPath(fileName));
    }
}
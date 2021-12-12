package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.properties.entities.Key;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.plugins.PluginManager;
import org.jusecase.properties.plugins.exporter.PropertiesExporterTrainer;
import org.jusecase.properties.plugins.importer.PropertiesImporterTrainer;
import org.jusecase.properties.usecases.Export.Request;
import org.jusecase.properties.usecases.Export.Response;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.Builders.list;
import static org.jusecase.properties.entities.Builders.property;

public class ExportTest extends UsecaseTest<Request, Response> {

    PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();
    PropertiesExporterTrainer propertiesExporterTrainer = new PropertiesExporterTrainer();
    PluginManager pluginManager = new PluginManager();

    @Before
    public void setUp() {
        usecase = new Export(propertiesGatewayTrainer, pluginManager);
        pluginManager.registerPlugin(propertiesExporterTrainer);

        request.pluginId = "pluginId";
        givenKeys("key");
    }

    @Test
    public void nullKeys() {
        request.keys = null;
        whenRequestIsExecuted();
        thenErrorMessageIs("Keys must not be null.");
    }

    @Test
    public void emptyKeys() {
        request.keys = a(list());
        whenRequestIsExecuted();
        thenErrorMessageIs("Keys must not be empty.");
    }

    @Test
    public void nullPlugin() {
        request.pluginId = null;
        whenRequestIsExecuted();
        thenErrorMessageIs("No plugin 'null' found to export properties.");
    }

    @Test
    public void notExistingPlugin() {
        request.pluginId = "???";
        whenRequestIsExecuted();
        thenErrorMessageIs("No plugin '???' found to export properties.");
    }

    @Test
    public void wrongPluginType() {
        pluginManager.registerPlugin(new PropertiesImporterTrainer());
        whenRequestIsExecuted();
        thenErrorMessageIs("No plugin 'pluginId' found to export properties.");
    }

    @Test
    public void keyDoesNotExist() {
        propertiesGatewayTrainer.givenProperties("key", null);
        whenRequestIsExecuted();
        propertiesExporterTrainer.thenNoPropertiesAreExported();
    }

    @Test
    public void oneKey() {
        propertiesGatewayTrainer.givenProperties("key", a(list(
                a(property().withKey("key").withValue("value").withFileName("en"))
        )));

        whenRequestIsExecuted();

        assertThat(propertiesExporterTrainer.getExportedProperties()).usingElementComparatorOnFields("key", "value", "fileName").containsExactly(
                a(property().withKey("key").withValue("value").withFileName("en"))
        );
    }

    @Test
    public void manyKeys() {
        givenKeys("key", "key2");
        propertiesGatewayTrainer.givenProperties("key", a(list(
                a(property().withKey("key").withValue("value").withFileName("en"))
        )));
        propertiesGatewayTrainer.givenProperties("key2", a(list(
                a(property().withKey("key2").withValue("value 2").withFileName("en")),
                a(property().withKey("key2").withValue("wert 2").withFileName("de"))
        )));

        whenRequestIsExecuted();

        assertThat(propertiesExporterTrainer.getExportedProperties()).usingElementComparatorOnFields("key", "value", "fileName").containsExactly(
                a(property().withKey("key").withValue("value").withFileName("en")),
                a(property().withKey("key2").withValue("value 2").withFileName("en")),
                a(property().withKey("key2").withValue("wert 2").withFileName("de"))
        );
    }

    @Test
    public void response_isEmptySoFar() {
        whenRequestIsExecuted();
        assertThat(response).isNotNull();
    }

    private void givenKeys(String ... keys) {
        request.keys = Arrays.stream(keys).map(Key::new).collect(Collectors.toList());
    }
}
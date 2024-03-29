package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.plugins.PluginManager;
import org.jusecase.properties.plugins.importer.PropertiesImporterTrainer;
import org.jusecase.properties.usecases.Import.Request;
import org.jusecase.properties.usecases.Import.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.Builders.list;
import static org.jusecase.properties.entities.Builders.property;
import static org.jusecase.properties.entities.Builders.testPath;


public class ImportTest extends UsecaseTest<Request, Response> {
    PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();
    PropertiesImporterTrainer propertiesImporterTrainer = new PropertiesImporterTrainer();
    PluginManager pluginManager = new PluginManager();

    @Before
    public void setUp() {
        usecase = new Import(propertiesGatewayTrainer, pluginManager);
        request.pluginId = "pluginId";
        request.files = a(list(a(testPath("something-else.txt"))));

        propertiesImporterTrainer.givenPluginId("pluginId");
        pluginManager.registerPlugin(propertiesImporterTrainer);
    }

    @Test
    public void unknownPlugin() {
        request.pluginId = "unknown";
        whenRequestIsExecuted();
        thenErrorMessageIs("No plugin 'unknown' found to import properties");
    }

    @Test
    public void notInitialized() {
        propertiesGatewayTrainer.givenInitialized(false);
        whenRequestIsExecuted();
        thenErrorMessageIs("No properties opened yet. You probably want to do File -> Open first.");
    }

    @Test
    public void success() {
        List<Property> importedProperties = a(list(
              a(property().withFileName("de")),
              a(property().withFileName("en_US"))
        ));
        propertiesImporterTrainer.givenProperties(importedProperties);

        whenRequestIsExecuted();

        assertThat(propertiesGatewayTrainer.getAddedProperties()).isEqualTo(importedProperties);
        assertThat(request.importedProperties).isEqualTo(importedProperties);
        assertThat(importedProperties.get(0).fileName).isEqualTo("resources_de.properties");
        assertThat(importedProperties.get(1).fileName).isEqualTo("resources_en_US.properties");
    }

    @Test
    public void unknownPropertiesFileForLocale() {
        List<Property> importedProperties = a(list(
              a(property().withFileName("de")),
              a(property().withFileName("unknown"))
        ));
        propertiesImporterTrainer.givenProperties(importedProperties);

        whenRequestIsExecuted();

        assertThat(propertiesGatewayTrainer.getAddedProperties()).hasSize(1);
        assertThat(importedProperties.get(0).fileName).isEqualTo("resources_de.properties");
    }

    @Test
    public void overwrittenPropertiesAreAddedToRequest() {
        List<Property> importedProperties = a(list(
              a(property().withKey("key1").withFileName("de")),
              a(property().withKey("key2").withFileName("de"))
        ));
        propertiesImporterTrainer.givenProperties(importedProperties);
        propertiesGatewayTrainer.givenProperties("key1", a(list(
              a(property().withKey("key1").withFileName("resources_de.properties"))
        )));

        whenRequestIsExecuted();

        assertThat(request.overwrittenProperties).hasSize(1);
        assertThat(request.overwrittenProperties.get(0).key).isEqualTo("key1");
    }

    @Test
    public void overwrittenPropertiesAreAddedToRequest_onlyForImportedLocales() {
        List<Property> importedProperties = a(list(
              a(property().withKey("key1").withFileName("de"))
        ));
        propertiesImporterTrainer.givenProperties(importedProperties);
        propertiesGatewayTrainer.givenProperties("key1", a(list(
              a(property().withKey("key1").withFileName("resources_de.properties")),
              a(property().withKey("key1").withFileName("resources_en.properties"))
        )));

        whenRequestIsExecuted();

        assertThat(request.overwrittenProperties).hasSize(1);
        assertThat(request.overwrittenProperties.get(0).fileName).isEqualTo("resources_de.properties");
    }

    @Test
    public void response() {
        List<Property> importedProperties = a(list(
              a(property().withKey("key1").withFileName("")),
              a(property().withKey("key2").withFileName("")),
              a(property().withKey("key3").withFileName(""))
        ));
        propertiesImporterTrainer.givenProperties(importedProperties);
        propertiesGatewayTrainer.givenProperties("key1", a(list(
              a(property().withKey("key1"))
        )));

        whenRequestIsExecuted();

        assertThat(response.amountChanged).isEqualTo(1);
        assertThat(response.amountAdded).isEqualTo(2);
    }

    @Test
    public void undo_name() {
        assertThat(request.name).isNull();
        whenRequestIsExecuted();
        assertThat(request.name).isEqualTo("import pluginName");
    }

    @Test
    public void undo()  {
        request.importedProperties = a(list(a(property())));
        request.overwrittenProperties = a(list(a(property()), a(property())));
        request.undo = true;

        whenRequestIsExecuted();

        assertThat(propertiesGatewayTrainer.getDeletedProperties()).isEqualTo(request.importedProperties);
        assertThat(propertiesGatewayTrainer.getAddedProperties()).isEqualTo(request.overwrittenProperties);
    }

    @Test
    public void redo_propertiesAreNotLoadedAgain() {
        List<Property> importedProperties = a(list(a(property())));
        request.importedProperties = importedProperties;

        whenRequestIsExecuted();

        assertThat(propertiesGatewayTrainer.getAddedProperties()).isEqualTo(importedProperties);
    }
}
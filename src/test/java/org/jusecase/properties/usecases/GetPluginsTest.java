package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.plugins.PluginManager;
import org.jusecase.properties.plugins.PluginTrainer;
import org.jusecase.properties.plugins.importer.PropertiesImporter;
import org.jusecase.properties.plugins.importer.PropertiesImporterTrainer;
import org.jusecase.properties.usecases.GetPlugins.Request;
import org.jusecase.properties.usecases.GetPlugins.Response;

import static org.assertj.core.api.Assertions.assertThat;


public class GetPluginsTest extends UsecaseTest<Request, Response> {
    private PluginManager pluginManager = new PluginManager();

    @Before
    public void setUp() {
        usecase = new GetPlugins(pluginManager);
    }

    @Test
    public void noPlugins() {
        whenRequestIsExecuted();
        assertThat(response.plugins).isEmpty();
    }

    @Test
    public void onePlugin() {
        pluginManager.registerPlugin(new PluginTrainer());
        whenRequestIsExecuted();
        assertThat(response.plugins).hasSize(1);
    }

    @Test
    public void filterByPluginClass_noMatch() {
        pluginManager.registerPlugin(new PluginTrainer());
        request.pluginClass = PropertiesImporter.class;

        whenRequestIsExecuted();

        assertThat(response.plugins).hasSize(0);
    }

    @Test
    public void filterByPluginClass_match() {
        pluginManager.registerPlugin(new PluginTrainer());
        pluginManager.registerPlugin(new PropertiesImporterTrainer());
        request.pluginClass = PropertiesImporter.class;

        whenRequestIsExecuted();

        assertThat(response.plugins).hasSize(1);
    }
}
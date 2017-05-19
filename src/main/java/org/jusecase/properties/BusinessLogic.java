package org.jusecase.properties;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;
import org.jusecase.UsecaseExecutor;
import org.jusecase.executors.guice.GuiceUsecaseExecutor;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.*;
import org.jusecase.properties.plugins.Plugin;
import org.jusecase.properties.plugins.PluginManager;
import org.jusecase.properties.plugins.exporter.JsonPropertiesExporter;
import org.jusecase.properties.plugins.importer.JavaPropertiesImporter;
import org.jusecase.properties.usecases.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class BusinessLogic extends GuiceUsecaseExecutor {

    private UndoableRequestGateway undoableRequestGateway = new UndoableRequestGateway();
    private PluginManager pluginManager;
    private Map<Class<?>, Class<?>> externalDependencies = new HashMap<>();

    public BusinessLogic() {
        setInjector(Guice.createInjector(
                new GatewayModule(),
                new PluginModule(),
                new ExternalDependenciesModule(),
                new BusinessLogicModule()
        ));

        addUsecase(LoadBundle.class);
        addUsecase(ReloadBundle.class);
        addUsecase(SaveBundle.class);
        addUsecase(Search.class);
        addUsecase(GetProperties.class);
        addUsecase(Initialize.class);
        addUsecase(EditValue.class);
        addUsecase(NewKey.class);
        addUsecase(RenameKey.class);
        addUsecase(DuplicateKey.class);
        addUsecase(DeleteKey.class);
        addUsecase(Undo.class);
        addUsecase(Redo.class);
        addUsecase(GetUndoStatus.class);
        addUsecase(CheckModifications.class);
        addUsecase(IgnoreModifications.class);
        addUsecase(Import.class);
        addUsecase(GetPlugins.class);
        addUsecase(IsAllowedToQuit.class);
        addUsecase(Export.class);
        addUsecase(GetChangedKeys.class);

        registerPlugin(JavaPropertiesImporter.class);
        registerPlugin(JsonPropertiesExporter.class);
    }

    private class GatewayModule extends AbstractModule {
        private Path settingsDirectory = Paths.get(System.getProperty("user.home")).toAbsolutePath();
        private Path settingsFile = settingsDirectory.resolve(".jusecase-properties-editor").resolve("settings.json");

        @Override
        protected void configure() {
            bind(Path.class).annotatedWith(Names.named("settingsFile")).toInstance(settingsFile);

            bind(PropertiesGateway.class).to(InMemoryPropertiesGateway.class);
            bind(SettingsGateway.class).to(JsonSettingsGateway.class);
            bind(UndoableRequestGateway.class).toInstance(undoableRequestGateway);
        }
    }

    private class PluginModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(PluginManager.class).toProvider(BusinessLogic.this::getPluginManager);
        }
    }

    private class ExternalDependenciesModule extends AbstractModule {
        @Override
        protected void configure() {
            addExternalDependencies(externalDependencies);
            externalDependencies.forEach(( from, to ) -> {
                //noinspection unchecked
                bind(from).to((Class)to);
            });
        }
    }

    private class BusinessLogicModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(UsecaseExecutor.class).toInstance(BusinessLogic.this);
        }
    }

    @Override
    public <Request, Response> Response execute(Request request) {
        Response response = super.execute(request);
        if (request instanceof UndoableRequest) {
            UndoableRequest undoableRequest = (UndoableRequest) request;
            if (!undoableRequestGateway.contains(undoableRequest)) {
                if (undoableRequest.name == null) {
                    undoableRequest.name = undoableRequest.getClass().getEnclosingClass().getSimpleName();
                }
                undoableRequestGateway.add(undoableRequest);
            }
        }
        return response;
    }

    public void registerPlugin(Class<? extends Plugin> pluginClass) {
        getPluginManager().registerPlugin(pluginClass);
    }

    public PluginManager getPluginManager() {
        if (pluginManager == null) {
            pluginManager = new PluginManager(getInjector());
        }
        return pluginManager;
    }

    @SuppressWarnings("unused") // Used by derived projects
    protected void addExternalDependencies( Map<Class<?>, Class<?>> externalDependencies ) {
    }
}

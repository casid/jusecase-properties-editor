package org.jusecase.properties;

import org.jusecase.executors.manual.ManualUsecaseExecutor;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.*;
import org.jusecase.properties.plugins.Plugin;
import org.jusecase.properties.plugins.PluginManager;
import org.jusecase.properties.plugins.diff.DiffPlugin;
import org.jusecase.properties.plugins.diff.GitDiffPlugin;
import org.jusecase.properties.plugins.importer.JavaPropertiesImporter;
import org.jusecase.properties.usecases.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BusinessLogic extends ManualUsecaseExecutor {

    private final UndoableRequestGateway undoableRequestGateway = new UndoableRequestGateway();

    private final GatewayModule gatewayModule;
    private final PluginModule pluginModule;

    public BusinessLogic() {
        gatewayModule = new GatewayModule();
        pluginModule = new PluginModule();

        addUsecase(new LoadBundle(gatewayModule.propertiesGateway, gatewayModule.settingsGateway, undoableRequestGateway));
        addUsecase(new ReloadBundle(gatewayModule.propertiesGateway, gatewayModule.settingsGateway, undoableRequestGateway));
        addUsecase(new SaveBundle(gatewayModule.propertiesGateway));
        addUsecase(new Search(gatewayModule.propertiesGateway, this, gatewayModule.settingsGateway));
        addUsecase(new GetProperties(gatewayModule.propertiesGateway));
        addUsecase(new Initialize(gatewayModule.propertiesGateway, gatewayModule.settingsGateway, undoableRequestGateway));
        addUsecase(new EditValue(gatewayModule.propertiesGateway));
        addUsecase(new NewKey(gatewayModule.propertiesGateway));
        addUsecase(new RenameKey(gatewayModule.propertiesGateway));
        addUsecase(new RenameKeys(gatewayModule.propertiesGateway));
        addUsecase(new DuplicateKey(gatewayModule.propertiesGateway));
        addUsecase(new DuplicateKeys(gatewayModule.propertiesGateway));
        addUsecase(new DeleteKey(gatewayModule.propertiesGateway));
        addUsecase(new Undo(this, undoableRequestGateway));
        addUsecase(new Redo(this, undoableRequestGateway));
        addUsecase(new GetUndoStatus(undoableRequestGateway));
        addUsecase(new CheckModifications(gatewayModule.propertiesGateway));
        addUsecase(new IgnoreModifications(gatewayModule.propertiesGateway));
        addUsecase(new Import(gatewayModule.propertiesGateway, pluginModule.pluginManager));
        addUsecase(new GetPlugins(pluginModule.pluginManager));
        addUsecase(new IsAllowedToQuit(gatewayModule.propertiesGateway));
        addUsecase(new Export(gatewayModule.propertiesGateway, pluginModule.pluginManager));
        addUsecase(new GetChangedKeys(undoableRequestGateway, pluginModule.diffPlugin, gatewayModule.propertiesGateway));
        addUsecase(new LoadLookAndFeel(gatewayModule.settingsGateway));
        addUsecase(new SaveLookAndFeel(gatewayModule.settingsGateway));
        addUsecase(new GetSearchHistory(gatewayModule.settingsGateway));

        registerPlugin(new JavaPropertiesImporter());
    }

    public static final class GatewayModule {
        public final Path settingsDirectory = Paths.get(System.getProperty("user.home")).toAbsolutePath();
        public final Path settingsFile = settingsDirectory.resolve(".jusecase-properties-editor").resolve("settings.json");
        public final PropertiesGateway propertiesGateway;
        public final SettingsGateway settingsGateway;

        public GatewayModule() {
            propertiesGateway = new InMemoryPropertiesGateway();
            settingsGateway = new JsonSettingsGateway(settingsFile);
        }
    }

    public static final class PluginModule {
        public final PluginManager pluginManager = new PluginManager();
        public final DiffPlugin diffPlugin = new GitDiffPlugin();
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

    public PluginModule getPluginModule() {
        return pluginModule;
    }

    public GatewayModule getGatewayModule() {
        return gatewayModule;
    }

    public void registerPlugin(Plugin plugin) {
        getPluginManager().registerPlugin(plugin);
    }

    public PluginManager getPluginManager() {
        return getPluginModule().pluginManager;
    }
}

package org.jusecase.properties;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;
import org.jusecase.executors.guice.GuiceUsecaseExecutor;
import org.jusecase.properties.gateways.JsonSettingsGateway;
import org.jusecase.properties.gateways.LucenePropertiesGateway;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.gateways.SettingsGateway;
import org.jusecase.properties.usecases.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BusinessLogic extends GuiceUsecaseExecutor {
    public BusinessLogic() {
        super(Guice.createInjector(new GatewayModule()));
        addUsecase(LoadBundle.class);
        addUsecase(SaveBundle.class);
        addUsecase(Search.class);
        addUsecase(GetProperties.class);
        addUsecase(Initialize.class);
        addUsecase(EditValue.class);
    }

    private static class GatewayModule extends AbstractModule {
        private Path settingsDirectory = Paths.get(System.getProperty("user.home")).toAbsolutePath();
        private Path settingsFile = settingsDirectory.resolve(".jusecase-properties-editor").resolve("settings.json");

        @Override
        protected void configure() {
            bind(Path.class).annotatedWith(Names.named("settingsFile")).toInstance(settingsFile);

            bind(PropertiesGateway.class).to(LucenePropertiesGateway.class);
            bind(SettingsGateway.class).to(JsonSettingsGateway.class);
        }
    }
}

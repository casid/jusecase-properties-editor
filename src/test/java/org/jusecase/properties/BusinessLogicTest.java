package org.jusecase.properties;

import org.junit.Test;
import org.jusecase.executors.UsecaseRequestResolver;
import org.jusecase.properties.plugins.Plugin;
import org.jusecase.properties.plugins.importer.JavaPropertiesImporter;
import org.jusecase.properties.usecases.*;

import static org.assertj.core.api.Assertions.assertThat;

public class BusinessLogicTest {
    BusinessLogic businessLogic = new BusinessLogic();
    UsecaseRequestResolver requestResolver = new UsecaseRequestResolver();

    @Test
    public void usecases() {
        thenUsecaseCanBeExecuted(LoadBundle.class);
        thenUsecaseCanBeExecuted(ReloadBundle.class);
        thenUsecaseCanBeExecuted(SaveBundle.class);
        thenUsecaseCanBeExecuted(Search.class);
        thenUsecaseCanBeExecuted(GetProperties.class);
        thenUsecaseCanBeExecuted(Initialize.class);
        thenUsecaseCanBeExecuted(EditValue.class);
        thenUsecaseCanBeExecuted(NewKey.class);
        thenUsecaseCanBeExecuted(RenameKey.class);
        thenUsecaseCanBeExecuted(RenameKeys.class);
        thenUsecaseCanBeExecuted(DuplicateKey.class);
        thenUsecaseCanBeExecuted(DeleteKey.class);
        thenUsecaseCanBeExecuted(Undo.class);
        thenUsecaseCanBeExecuted(Redo.class);
        thenUsecaseCanBeExecuted(GetUndoStatus.class);
        thenUsecaseCanBeExecuted(CheckModifications.class);
        thenUsecaseCanBeExecuted(IgnoreModifications.class);
        thenUsecaseCanBeExecuted(Import.class);
        thenUsecaseCanBeExecuted(GetPlugins.class);
        thenUsecaseCanBeExecuted(IsAllowedToQuit.class);
        thenUsecaseCanBeExecuted(Export.class);
        thenUsecaseCanBeExecuted(GetChangedKeys.class);
        thenUsecaseCanBeExecuted(LoadLookAndFeel.class);
        thenUsecaseCanBeExecuted(SaveLookAndFeel.class);
        thenUsecaseCanBeExecuted(GetSearchHistory.class);
    }

    @Test
    public void plugins() {
        thenPluginIsAvailable(JavaPropertiesImporter.ID, JavaPropertiesImporter.class);
    }

    private void thenPluginIsAvailable(String id, Class<? extends Plugin> pluginClass) {
        assertThat(businessLogic.getPluginManager().getPlugin(id)).isInstanceOf(pluginClass);
    }

    private void thenUsecaseCanBeExecuted(Class<?> usecaseClass) {
        Class<?> requestClass = requestResolver.getRequestClass(usecaseClass);
        this.thenUsecaseCanBeExecuted(requestClass, usecaseClass);
    }

    private void thenUsecaseCanBeExecuted(Class<?> requestClass, Class<?> usecaseClass) {
        Object usecase = businessLogic.getUsecase(requestClass);
        assertThat(usecase).describedAs("No usecase was found for request '" + requestClass.getName() + "'").isNotNull();
        assertThat(usecase.getClass()).isEqualTo(usecaseClass);
    }
}
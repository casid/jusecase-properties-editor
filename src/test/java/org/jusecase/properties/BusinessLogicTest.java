package org.jusecase.properties;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseExecutorTest;
import org.jusecase.properties.usecases.*;

public class BusinessLogicTest extends UsecaseExecutorTest {
    BusinessLogic businessLogic;

    @Before
    public void setUp() {
        givenExecutor(businessLogic = new BusinessLogic());
    }

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
    }
}
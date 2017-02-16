package org.jusecase.properties;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseExecutorTest;
import org.jusecase.properties.usecases.GetProperties;
import org.jusecase.properties.usecases.Initialize;
import org.jusecase.properties.usecases.LoadBundle;
import org.jusecase.properties.usecases.Search;

public class BusinessLogicTest extends UsecaseExecutorTest {
    @Before
    public void setUp() {
        givenExecutor(new BusinessLogic());
    }

    @Test
    public void name() {
        thenUsecaseCanBeExecuted(LoadBundle.class);
        thenUsecaseCanBeExecuted(Search.class);
        thenUsecaseCanBeExecuted(GetProperties.class);
        thenUsecaseCanBeExecuted(Initialize.class);
    }
}
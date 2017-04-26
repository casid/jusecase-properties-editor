package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.usecases.CheckModifications.Request;
import org.jusecase.properties.usecases.CheckModifications.Response;

public class CheckModificationsTest extends UsecaseTest<Request, Response> {

    private PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();

    @Before
    public void setUp() {
        usecase = new CheckModifications(propertiesGatewayTrainer);
    }

    @Test
    public void noChanges() {
        whenRequestIsExecuted();
        thenResponseIs(Response.NoActionRequired);
    }

    @Test
    public void externalChanges() {
        propertiesGatewayTrainer.givenExternalChanges(true);
        whenRequestIsExecuted();
        thenResponseIs(Response.ReloadSilently);
    }

    @Test
    public void unsavedChanges() {
        propertiesGatewayTrainer.givenUnsavedChanges(true);
        whenRequestIsExecuted();
        thenResponseIs(Response.NoActionRequired);
    }

    @Test
    public void externalChangesAndUnsavedChanges() {
        propertiesGatewayTrainer.givenExternalChanges(true);
        propertiesGatewayTrainer.givenUnsavedChanges(true);
        whenRequestIsExecuted();
        thenResponseIs(Response.AskUser);
    }
}
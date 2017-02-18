package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.usecases.NewKey.Request;
import org.jusecase.properties.usecases.NewKey.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class NewKeyTest extends UsecaseTest<Request, Response> {

    PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();

    @Before
    public void setUp() {
        usecase = new NewKey(propertiesGatewayTrainer);
        request = new Request();
    }

    @Test
    public void nullName() {
        request.key = null;

        whenRequestIsExecuted();

        thenErrorMessageIs("Key name must not be empty");
        propertiesGatewayTrainer.thenNoKeyIsAdded();
    }

    @Test
    public void emptyName() {
        request.key = "";

        whenRequestIsExecuted();

        thenErrorMessageIs("Key name must not be empty");
        propertiesGatewayTrainer.thenNoKeyIsAdded();
    }

    @Test
    public void success() {
        request.key = "my.new.key";

        whenRequestIsExecuted();

        assertThat(propertiesGatewayTrainer.getAddedKey()).isEqualTo("my.new.key");
    }

    @Test
    public void undo() {
        request.undo = true;
        request.key = "my.new.key";

        whenRequestIsExecuted();


        assertThat(propertiesGatewayTrainer.getDeletedKey()).isEqualTo("my.new.key");
    }
}
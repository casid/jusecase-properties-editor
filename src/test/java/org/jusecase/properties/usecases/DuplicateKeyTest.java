package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.usecases.DuplicateKey.Request;
import org.jusecase.properties.usecases.DuplicateKey.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class DuplicateKeyTest extends UsecaseTest<Request, Response> {

    private PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();

    @Before
    public void setUp() {
        usecase = new DuplicateKey(propertiesGatewayTrainer);
    }

    @Test
    public void success() {
        request.key = "key";
        request.newKey = "key.clone";

        whenRequestIsExecuted();

        assertThat(propertiesGatewayTrainer.getDuplicatedKey()).isEqualTo("key->key.clone");
    }

    @Test
    public void undo() {
        request.undo = true;
        request.key = "key";
        request.newKey = "key.clone";

        whenRequestIsExecuted();

        assertThat(propertiesGatewayTrainer.getDeletedKey()).isEqualTo("key.clone");
    }
}
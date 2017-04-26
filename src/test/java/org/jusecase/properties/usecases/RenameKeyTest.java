package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.usecases.RenameKey.Request;
import org.jusecase.properties.usecases.RenameKey.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class RenameKeyTest extends UsecaseTest<Request, Response> {

    private PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();

    @Before
    public void setUp() {
        usecase = new RenameKey(propertiesGatewayTrainer);
    }

    @Test
    public void success() {
        request.key = "key";
        request.newKey = "key.renamed";

        whenRequestIsExecuted();

        assertThat(propertiesGatewayTrainer.getRenamedKey()).isEqualTo("key->key.renamed");
    }

    @Test
    public void undo() {
        request.undo = true;
        request.key = "key";
        request.newKey = "key.renamed";

        whenRequestIsExecuted();

        assertThat(propertiesGatewayTrainer.getRenamedKey()).isEqualTo("key.renamed->key");
    }
}
package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.usecases.DeleteKey.Request;
import org.jusecase.properties.usecases.DeleteKey.Response;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteKeyTest extends UsecaseTest<Request, Response> {
    private PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();

    @Before
    public void setUp() {
        usecase = new DeleteKey(propertiesGatewayTrainer);
        request = new Request();
    }

    @Test
    public void delete() {
        request.key = "test";
        whenRequestIsExecuted();
        assertThat(propertiesGatewayTrainer.getDeletedKey()).isEqualTo("test");
    }

    @Test
    public void delete_propertiesAreStoredForUndo() {
        List<Property> propertiesForKey = new ArrayList<>();
        propertiesGatewayTrainer.givenProperties("test", propertiesForKey);
        request.key = "test";

        whenRequestIsExecuted();

        assertThat(request.deletedProperties).isEqualTo(propertiesForKey);
    }

    @Test
    public void undo() {
        request.undo = true;
        request.deletedProperties = new ArrayList<>();

        whenRequestIsExecuted();

        assertThat(propertiesGatewayTrainer.getAddedProperties()).isSameAs(request.deletedProperties);
    }
}
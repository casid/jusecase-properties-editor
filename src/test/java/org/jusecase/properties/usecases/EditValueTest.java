package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.usecases.EditValue.Request;
import org.jusecase.properties.usecases.EditValue.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.property;

public class EditValueTest extends UsecaseTest<Request, Response> {

    private PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();

    @Before
    public void setUp() {
        usecase = new EditValue(propertiesGatewayTrainer);
        request = new Request();
        request.value = "new value";
        request.property = a(property());
    }

    @Test
    public void noProperty() {
        request.property = null;
        whenRequestIsExecuted();
        propertiesGatewayTrainer.thenNoValueIsUpdated();
    }

    @Test
    public void success() {
        whenRequestIsExecuted();

        Property updated = propertiesGatewayTrainer.getUpdatedValue();
        assertThat(updated).isSameAs(request.property);
        assertThat(updated.value).isEqualTo(request.value);
    }

    @Test
    public void success_oldValueIsStored() {
        request.property.value = "old value";
        whenRequestIsExecuted();
        assertThat(request.oldValue).isEqualTo("old value");
    }

    @Test
    public void undo() {
        request.undo = true;
        request.oldValue = "old value";

        whenRequestIsExecuted();

        Property updated = propertiesGatewayTrainer.getUpdatedValue();
        assertThat(updated).isSameAs(request.property);
        assertThat(updated.value).isEqualTo(request.oldValue);
    }
}
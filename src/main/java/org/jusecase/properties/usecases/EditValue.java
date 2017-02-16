package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.gateways.PropertiesGateway;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EditValue implements Usecase<EditValue.Request, EditValue.Response> {

    private final PropertiesGateway propertiesGateway;

    @Inject
    public EditValue(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.property != null) {
            request.property.value = request.value;
            propertiesGateway.updateValue(request.property);
        }
        return null;
    }

    public static class Request {
        public Property property;
        public String value;
    }

    public static class Response {

    }
}

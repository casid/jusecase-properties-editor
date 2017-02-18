package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.entities.UndoableRequest;
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
            if (request.undo) {
                request.property.value = request.oldValue;
            } else {
                request.oldValue = request.property.value;
                request.property.value = request.value;
            }
            propertiesGateway.updateValue(request.property);
        }
        return new Response();
    }

    public static class Request extends UndoableRequest {
        public Request() {
            this.name = "edit value";
        }

        public Property property;
        public String value;

        String oldValue;
    }

    public static class Response {
    }
}

package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGateway;

import java.util.List;


public class DeleteKey implements Usecase<DeleteKey.Request, DeleteKey.Response> {

    private final PropertiesGateway propertiesGateway;

    public DeleteKey(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.undo) {
            propertiesGateway.addProperties(request.deletedProperties);
        } else {
            request.deletedProperties = propertiesGateway.getProperties(request.key);
            propertiesGateway.deleteKey(request.key);
        }
        return new Response();
    }

    public static class Request extends UndoableRequest {
        public String key;
        List<Property> deletedProperties;

        public Request() {
            name = "delete key";
        }
    }

    public static class Response {
    }
}

package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGateway;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NewKey implements Usecase<NewKey.Request, NewKey.Response> {

    private final PropertiesGateway propertiesGateway;

    @Inject
    public NewKey(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.key == null || request.key.isEmpty()) {
            throw new UsecaseException("Key name must not be empty");
        }

        if (request.undo) {
            propertiesGateway.deleteKey(request.key);
        } else {
            propertiesGateway.addKey(request.key);
        }

        return new Response();
    }

    public static class Request extends UndoableRequest {
        public Request() {
            name = "new key";
        }

        public String key;
    }

    public static class Response {
    }
}

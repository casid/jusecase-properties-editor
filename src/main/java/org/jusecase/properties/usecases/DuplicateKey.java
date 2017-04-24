package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.plugins.validation.KeyValidator;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class DuplicateKey implements Usecase<DuplicateKey.Request, DuplicateKey.Response> {

    private final PropertiesGateway propertiesGateway;
    private final KeyValidator keyValidator = new KeyValidator();

    @Inject
    public DuplicateKey(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.undo) {
            propertiesGateway.deleteKey(request.newKey);
        } else {
            keyValidator.validate(request.newKey);
            propertiesGateway.duplicateKey(request.key, request.newKey);
        }
        return new Response();
    }

    public static class Request extends UndoableRequest {
        public String key;
        public String newKey;

        public Request() {
            name = "duplicate key";
        }
    }

    public static class Response {
    }
}

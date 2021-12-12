package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.plugins.validation.KeyValidator;

public class RenameKey implements Usecase<RenameKey.Request, RenameKey.Response> {

    private final PropertiesGateway propertiesGateway;
    private final KeyValidator keyValidator = new KeyValidator();

    public RenameKey(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.undo) {
            propertiesGateway.renameKey(request.newKey, request.key);
        } else {
            keyValidator.validate(request.newKey);
            propertiesGateway.renameKey(request.key, request.newKey);
        }
        return new Response();
    }

    public static class Request extends UndoableRequest {
        public String key;
        public String newKey;
    }

    public static class Response {
    }
}

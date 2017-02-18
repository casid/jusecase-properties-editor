package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGateway;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class RenameKey implements Usecase<RenameKey.Request, RenameKey.Response> {

    private final PropertiesGateway propertiesGateway;

    @Inject
    public RenameKey(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.undo) {
            propertiesGateway.renameKey(request.newKey, request.key);
        } else {
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

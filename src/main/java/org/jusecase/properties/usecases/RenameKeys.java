package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.plugins.validation.KeyValidator;

import java.util.List;

public class RenameKeys implements Usecase<RenameKeys.Request, RenameKeys.Response> {

    private final PropertiesGateway propertiesGateway;
    private final KeyValidator keyValidator = new KeyValidator();

    public RenameKeys(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.undo) {
            for (int i = 0; i < request.newKeys.size(); ++i) {
                propertiesGateway.renameKey(request.newKeys.get(i), request.keys.get(i));
            }
        } else {
            request.newKeys.forEach(keyValidator::validate);
            for (int i = 0; i < request.keys.size(); ++i) {
                propertiesGateway.renameKey(request.keys.get(i), request.newKeys.get(i));
            }
        }
        return new Response();
    }

    public static class Request extends UndoableRequest {
        public List<String> keys;
        public List<String> newKeys;

        public Request() {
            name = "rename keys";
        }
    }

    public static class Response {
    }
}

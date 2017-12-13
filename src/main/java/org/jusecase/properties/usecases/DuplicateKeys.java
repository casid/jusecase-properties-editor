package org.jusecase.properties.usecases;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.plugins.validation.KeyValidator;


@Singleton
public class DuplicateKeys implements Usecase<DuplicateKeys.Request, DuplicateKeys.Response> {

    private final PropertiesGateway propertiesGateway;
    private final KeyValidator keyValidator = new KeyValidator();

    @Inject
    public DuplicateKeys(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.undo) {
            request.newKeys.forEach(propertiesGateway::deleteKey);
        } else {
            request.newKeys.forEach(keyValidator::validate);
            for (int i = 0; i < request.keys.size(); ++i) {
                propertiesGateway.duplicateKey(request.keys.get(i), request.newKeys.get(i));
            }
        }
        return new Response();
    }

    public static class Request extends UndoableRequest {
        public List<String> keys;
        public List<String> newKeys;

        public Request() {
            name = "duplicate keys";
        }
    }

    public static class Response {
    }
}

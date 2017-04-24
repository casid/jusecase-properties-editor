package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.plugins.validation.KeyValidator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NewKey implements Usecase<NewKey.Request, NewKey.Response> {

    private final PropertiesGateway propertiesGateway;
    private final KeyValidator keyValidator = new KeyValidator();

    @Inject
    public NewKey(PropertiesGateway propertiesGateway) {
         this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.undo) {
            propertiesGateway.deleteKey(request.key);
        } else {
            keyValidator.validate(request.key);
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

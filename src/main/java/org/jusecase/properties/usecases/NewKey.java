package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

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

        propertiesGateway.addKey(request.key);
        return new Response();
    }

    public static class Request {
        public String key;
    }

    public static class Response {
        public List<String> keys;
    }
}

package org.jusecase.properties.usecases;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;


@Singleton
public class DuplicateKey implements Usecase<DuplicateKey.Request, DuplicateKey.Response> {

    private final PropertiesGateway propertiesGateway;

    @Inject
    public DuplicateKey(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        propertiesGateway.duplicateKey(request.key, request.newKey);
        return new Response();
    }

    public static class Request {
        public String key;
        public String newKey;
    }

    public static class Response {
    }
}

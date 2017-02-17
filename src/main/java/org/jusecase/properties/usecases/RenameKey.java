package org.jusecase.properties.usecases;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;


@Singleton
public class RenameKey implements Usecase<RenameKey.Request, RenameKey.Response> {

    private final PropertiesGateway propertiesGateway;

    @Inject
    public RenameKey(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        propertiesGateway.renameKey(request.key, request.newKey);
        return new Response();
    }

    public static class Request {
        public String key;
        public String newKey;
    }

    public static class Response {
    }
}

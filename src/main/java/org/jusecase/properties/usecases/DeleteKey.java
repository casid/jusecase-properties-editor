package org.jusecase.properties.usecases;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;


@Singleton
public class DeleteKey implements Usecase<DeleteKey.Request, DeleteKey.Response> {

    private final PropertiesGateway propertiesGateway;

    @Inject
    public DeleteKey(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        propertiesGateway.deleteKey(request.key);
        return new Response();
    }

    public static class Request {
        public String key;
    }

    public static class Response {
    }
}

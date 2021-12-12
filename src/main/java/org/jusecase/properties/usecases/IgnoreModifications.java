package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;

public class IgnoreModifications implements Usecase<IgnoreModifications.Request, IgnoreModifications.Response> {

    private final PropertiesGateway propertiesGateway;

    public IgnoreModifications(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        propertiesGateway.updateFileSnapshots();
        return new Response();
    }

    public static class Request {

    }

    public class Response {
    }
}

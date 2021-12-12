package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;

public class IsAllowedToQuit implements Usecase<IsAllowedToQuit.Request, IsAllowedToQuit.Response> {

    private final PropertiesGateway propertiesGateway;

    public IsAllowedToQuit(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        Response response = new Response();
        response.askUser = propertiesGateway.hasUnsavedChanges();
        return response;
    }

    public static class Request {

    }

    public class Response {
        public boolean askUser;
    }
}

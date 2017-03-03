package org.jusecase.properties.usecases;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;


@Singleton
public class IsAllowedToQuit implements Usecase<IsAllowedToQuit.Request, IsAllowedToQuit.Response> {

    private final PropertiesGateway propertiesGateway;

    @Inject
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

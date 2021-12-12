package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;

public class CheckModifications implements Usecase<CheckModifications.Request, CheckModifications.Response> {

    private final PropertiesGateway propertiesGateway;

    public CheckModifications(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (propertiesGateway.hasExternalChanges()) {
            if (propertiesGateway.hasUnsavedChanges()) {
                return Response.AskUser;
            } else {
                return Response.ReloadSilently;
            }
        }

        return Response.NoActionRequired;
    }

    public static class Request {

    }

    public enum Response {
        NoActionRequired,
        ReloadSilently,
        AskUser
    }
}

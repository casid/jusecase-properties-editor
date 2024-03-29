package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;

public class SaveBundle implements Usecase<SaveBundle.Request, SaveBundle.Response> {
    private final PropertiesGateway propertiesGateway;

    public SaveBundle(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.saveAll) {
            propertiesGateway.saveAll();
        } else {
            propertiesGateway.save();
        }
        return new Response();
    }

    public static class Request {
        public boolean saveAll;
    }

    public static class Response {
    }
}

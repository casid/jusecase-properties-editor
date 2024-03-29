package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.entities.Property;

import java.util.List;

public class GetProperties implements Usecase<GetProperties.Request, GetProperties.Response> {
    private final PropertiesGateway propertiesGateway;

    public GetProperties(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        Response response = new Response();
        response.properties = propertiesGateway.getProperties(request.key);
        return response;
    }

    public static class Request {
        public String key;
    }

    public static class Response {
        public List<Property> properties;
    }
}

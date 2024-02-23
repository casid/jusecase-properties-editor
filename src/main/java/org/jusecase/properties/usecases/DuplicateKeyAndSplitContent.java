package org.jusecase.properties.usecases;

import java.util.List;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.plugins.validation.KeyValidator;


public class DuplicateKeyAndSplitContent implements Usecase<DuplicateKeyAndSplitContent.Request, DuplicateKeyAndSplitContent.Response> {

    private final PropertiesGateway propertiesGateway;
    private final KeyValidator keyValidator = new KeyValidator();

    public DuplicateKeyAndSplitContent(PropertiesGateway propertiesGateway) {
        this.propertiesGateway = propertiesGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.undo) {
            propertiesGateway.deleteKey(request.newKey);
        } else {
            keyValidator.validate(request.newKey);

            if (!propertiesGateway.getProperties(request.newKey).isEmpty()) {
                throw new UsecaseException("A key with this name already exists");
            }

            List<Property> properties = propertiesGateway.getProperties(request.key);

            for ( Property property : properties ) {
                property.key = request.newKey;

                if (property.value != null) {
                    String[] split = property.value.split(request.splitRegex, request.limit);
                    if ( request.splitIndex < split.length ) {
                        property.value = split[request.splitIndex];
                    }
                }
            }

            propertiesGateway.addProperties(properties);
        }
        return new Response();
    }

    public static class Request extends UndoableRequest {
        public String key;
        public String newKey;
        public String splitRegex;
        public int splitIndex;
        public int limit;

        public Request() {
            name = "duplicate key and split content";
        }
    }

    public static class Response {
    }
}

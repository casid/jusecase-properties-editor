package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.UsecaseExecutor;
import org.jusecase.properties.entities.Key;
import org.jusecase.properties.gateways.PropertiesGateway;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;

@Singleton
public class Search implements Usecase<Search.Request, Search.Response> {
    private final PropertiesGateway propertiesGateway;
    private final UsecaseExecutor usecaseExecutor;

    @Inject
    public Search(PropertiesGateway propertiesGateway, UsecaseExecutor usecaseExecutor) {
        this.propertiesGateway = propertiesGateway;
        this.usecaseExecutor = usecaseExecutor;
    }

    @Override
    public Response execute(Request request) {
        if (request.changes) {
            request.keysToSearch = getChangedKeys();
        }

        Response response = new Response();
        response.keys = propertiesGateway.search(request);
        return response;
    }

    protected Collection<String> getChangedKeys() {
        GetChangedKeys.Response response = usecaseExecutor.execute(new GetChangedKeys.Request());
        return response.keys;
    }

    public static class Request {
        public String query;
        public boolean regex;
        public boolean caseSensitive;
        public boolean changes;
        public Collection<String> keysToSearch;
    }

    public static class Response {
        public List<Key> keys;
    }
}

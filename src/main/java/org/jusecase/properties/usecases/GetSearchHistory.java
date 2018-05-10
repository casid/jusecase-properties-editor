package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.UsecaseExecutor;
import org.jusecase.properties.entities.Key;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.gateways.SettingsGateway;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Singleton
public class GetSearchHistory implements Usecase<GetSearchHistory.Request, GetSearchHistory.Response> {
    private final SettingsGateway settingsGateway;

    @Inject
    public GetSearchHistory(SettingsGateway settingsGateway) {
        this.settingsGateway = settingsGateway;
    }

    @Override
    public Response execute(Request request) {
        ArrayList<String> queries = new ArrayList<>(settingsGateway.getSettings().searchHistory);
        queries.remove(request.currentQuery);

        Response response = new Response();
        response.queries = queries;
        return response;
    }

    public static class Request {
        public String currentQuery;
    }

    public static class Response {
        public List<String> queries;
    }
}

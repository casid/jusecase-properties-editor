package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.SettingsGateway;

import java.util.ArrayList;
import java.util.List;

public class GetSearchHistory implements Usecase<GetSearchHistory.Request, GetSearchHistory.Response> {
    private final SettingsGateway settingsGateway;

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

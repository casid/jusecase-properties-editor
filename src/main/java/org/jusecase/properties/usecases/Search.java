package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.UsecaseExecutor;
import org.jusecase.properties.entities.Key;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.gateways.SettingsGateway;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;

@Singleton
public class Search implements Usecase<Search.Request, Search.Response> {
    private final PropertiesGateway propertiesGateway;
    private final UsecaseExecutor usecaseExecutor;
    private final SettingsGateway settingsGateway;

    private Collection<String> changedKeys;

    @Inject
    public Search(PropertiesGateway propertiesGateway, UsecaseExecutor usecaseExecutor, SettingsGateway settingsGateway) {
        this.propertiesGateway = propertiesGateway;
        this.usecaseExecutor = usecaseExecutor;
        this.settingsGateway = settingsGateway;
    }

    @Override
    public Response execute(Request request) {
        if (request.changes) {
            if (changedKeys == null || request.reloadChanges) {
                changedKeys = getChangedKeys();
            }
            request.keysToSearch = changedKeys;
        } else {
            changedKeys = null;
        }

        List<Key> keys = propertiesGateway.search(request);
        if (!keys.isEmpty()) {
            addToHistory(request.query);
        }

        Response response = new Response();
        response.keys = keys;
        return response;
    }

    private void addToHistory(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }

        List<String> history = settingsGateway.getSettings().searchHistory;
        if (isNewHistoryEntryRequired(history, query)) {
            history.add(0, query);
        } else {
            history.set(0, query);
        }

        int maxSize = settingsGateway.getSettings().maxSearchHistorySize;
        while (history.size() > maxSize) {
            history.remove(maxSize);
        }
    }

    private boolean isNewHistoryEntryRequired(List<String> history, String query) {
        if (history.isEmpty()) {
            return true;
        }

        String lastQuery = history.get(0);
        return !query.startsWith(lastQuery);
    }


    private Collection<String> getChangedKeys() {
        GetChangedKeys.Response response = usecaseExecutor.execute(new GetChangedKeys.Request());
        return response.keys;
    }

    public static class Request {
        public String query;
        public boolean regex;
        public boolean caseSensitive;
        public boolean changes;
        public boolean reloadChanges;
        public Collection<String> keysToSearch;
    }

    public static class Response {
        public List<Key> keys;
    }
}

package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.UsecaseExecutor;
import org.jusecase.properties.entities.Key;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.gateways.SettingsGateway;

import java.util.Collection;
import java.util.List;

public class Search implements Usecase<Search.Request, Search.Response> {
    private final PropertiesGateway propertiesGateway;
    private final UsecaseExecutor usecaseExecutor;
    private final SettingsGateway settingsGateway;

    private Collection<String> changedKeys;

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
            int i = history.indexOf(query);
            if (i >= 0) {
                history.remove(i);
                history.add(0, query);
            } else {
                history.set(0, query);
            }
        }

        int maxSize = settingsGateway.getSettings().maxSearchHistorySize;
        while (history.size() > maxSize) {
            history.remove(maxSize);
        }

        settingsGateway.saveSettings(settingsGateway.getSettings());
    }

    private boolean isNewHistoryEntryRequired(List<String> history, String query) {
        if (history.isEmpty()) {
            return true;
        }

        if (history.contains(query)) {
            return false;
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

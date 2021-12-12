package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Key;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.plugins.PluginManager;
import org.jusecase.properties.plugins.exporter.PropertiesExporter;

import java.util.ArrayList;
import java.util.List;

public class Export implements Usecase<Export.Request, Export.Response> {

    private final PropertiesGateway propertiesGateway;
    private final PluginManager pluginManager;

    public Export(PropertiesGateway propertiesGateway, PluginManager pluginManager) {
        this.propertiesGateway = propertiesGateway;
        this.pluginManager = pluginManager;
    }

    @Override
    public Response execute(Request request) {
        validateRequest(request);
        PropertiesExporter plugin = getPlugin(request);

        List<Property> properties = new ArrayList<>();
        for (Key key : request.keys) {
            List<Property> propertiesForKey = propertiesGateway.getProperties(key.getKey());
            if (propertiesForKey != null) {
                properties.addAll(propertiesForKey);
            }
        }
        if (!properties.isEmpty()) {
            plugin.exportProperties(properties);
        }

        return new Response();
    }

    protected PropertiesExporter getPlugin(Request request) {
        PropertiesExporter plugin = pluginManager.getPlugin(request.pluginId, PropertiesExporter.class);
        if (plugin == null) {
            throw new UsecaseException("No plugin '" + request.pluginId + "' found to export properties.");
        }
        return plugin;
    }

    protected void validateRequest(Request request) {
        if (request.keys == null) {
            throw new UsecaseException("Keys must not be null.");
        }

        if (request.keys.isEmpty()) {
            throw new UsecaseException("Keys must not be empty.");
        }
    }

    public static class Request {
        public List<Key> keys;
        public String pluginId;
    }

    public static class Response {
    }
}

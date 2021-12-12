package org.jusecase.properties.usecases;

import java.util.ArrayList;
import java.util.List;

import org.jusecase.Usecase;
import org.jusecase.properties.plugins.Plugin;
import org.jusecase.properties.plugins.PluginManager;

public class GetPlugins implements Usecase<GetPlugins.Request, GetPlugins.Response> {

    private final PluginManager pluginManager;

    public GetPlugins(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public Response execute(Request request) {
        Response response = new Response();
        response.plugins = pluginManager.getPlugins(request.pluginClass);
        return response;
    }



    public static class Request {
        public Class<? extends Plugin> pluginClass = Plugin.class;
    }

    public static class Response {
        public List<Plugin> plugins = new ArrayList<>();
    }
}

package org.jusecase.properties.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager {
    private final Map<String, Plugin> plugins = new HashMap<>();

    public void registerPlugin(Plugin plugin) {
        plugins.put(plugin.getPluginId(), plugin);
    }

    public Plugin getPlugin(String pluginId) {
        return plugins.get(pluginId);
    }

    public <T extends Plugin> T getPlugin(String pluginId, Class<T> pluginClass) {
        Plugin plugin = getPlugin(pluginId);
        if (plugin != null && pluginClass.isAssignableFrom(plugin.getClass())) {
            //noinspection unchecked
            return (T) plugin;
        }

        return null;
    }

    public <T extends Plugin> List<Plugin> getPlugins(Class<T> pluginClass) {
        List<Plugin> result = new ArrayList<>();
        for (Plugin plugin : plugins.values()) {
            if (pluginClass.isAssignableFrom(plugin.getClass())) {
                result.add(plugin);
            }
        }

        return result;
    }
}

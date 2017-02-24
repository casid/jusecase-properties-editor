package org.jusecase.properties.plugins;


public class PluginTrainer implements Plugin {

    private String pluginId = "pluginId";
    private String pluginName = "pluginName";

    public void givenPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public void givenPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public String getPluginId() {
       return pluginId;
    }

    @Override
    public String getPluginName() {
        return pluginName;
    }
}
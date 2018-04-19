package org.jusecase.properties.entities;

import org.jusecase.properties.ui.LookAndFeel;

import java.util.HashMap;
import java.util.Map;


public class Settings {
    public String lastFile;
    public LookAndFeel lookAndFeel;
    public Map<String, Object> pluginSettings = new HashMap<>();
}

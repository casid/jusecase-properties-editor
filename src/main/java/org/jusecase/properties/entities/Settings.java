package org.jusecase.properties.entities;

import org.jusecase.properties.ui.LookAndFeel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Settings {
    public String lastFile;
    public LookAndFeel lookAndFeel;
    public Map<String, Object> pluginSettings = new HashMap<>();
    public List<String> searchHistory = new ArrayList<>();
    public int maxSearchHistorySize = 10;
}

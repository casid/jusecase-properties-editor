package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Settings;
import org.jusecase.properties.gateways.SettingsGateway;
import org.jusecase.properties.ui.LookAndFeel;

public class SaveLookAndFeel implements Usecase<SaveLookAndFeel.Request, Void> {
    private final SettingsGateway settingsGateway;

    public SaveLookAndFeel(SettingsGateway settingsGateway) {
        this.settingsGateway = settingsGateway;
    }

    @Override
    public Void execute(Request request) {
        Settings settings = settingsGateway.getSettings();
        settings.lookAndFeel = request.lookAndFeel;
        settingsGateway.saveSettings(settings);
        return null;
    }

    public static class Request {
        public LookAndFeel lookAndFeel;
    }
}

package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Settings;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.gateways.SettingsGateway;
import org.jusecase.properties.ui.LookAndFeel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SaveLookAndFeel implements Usecase<SaveLookAndFeel.Request, Void> {
    private final SettingsGateway settingsGateway;

    @Inject
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

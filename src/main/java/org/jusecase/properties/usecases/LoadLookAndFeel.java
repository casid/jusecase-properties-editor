package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Settings;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.gateways.SettingsGateway;
import org.jusecase.properties.ui.LookAndFeel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoadLookAndFeel implements Usecase<LoadLookAndFeel.Request, LookAndFeel> {
    private final SettingsGateway settingsGateway;

    @Inject
    public LoadLookAndFeel(SettingsGateway settingsGateway) {
        this.settingsGateway = settingsGateway;
    }

    @Override
    public LookAndFeel execute(Request request) {
        Settings settings = settingsGateway.getSettings();
        if (settings.lookAndFeel == null) {
            return LookAndFeel.Default;
        }

        return settings.lookAndFeel;
    }

    public static class Request {
    }
}

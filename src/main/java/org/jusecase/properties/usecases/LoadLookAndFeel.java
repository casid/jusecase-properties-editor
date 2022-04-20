package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Settings;
import org.jusecase.properties.gateways.SettingsGateway;
import org.jusecase.properties.ui.LookAndFeel;

public class LoadLookAndFeel implements Usecase<LoadLookAndFeel.Request, LookAndFeel> {

    @Override
    public LookAndFeel execute(Request request) {
        return LookAndFeel.Default;
    }

    public static class Request {
    }
}

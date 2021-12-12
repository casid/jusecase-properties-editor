package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Settings;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.gateways.SettingsGateway;
import org.jusecase.properties.gateways.UndoableRequestGateway;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ReloadBundle implements Usecase<ReloadBundle.Request, ReloadBundle.Response> {

    private final SettingsGateway settingsGateway;
    private final LoadBundle loadBundle;

    public ReloadBundle(PropertiesGateway propertiesGateway, SettingsGateway settingsGateway, UndoableRequestGateway undoableRequestGateway) {
        this.settingsGateway = settingsGateway;
        this.loadBundle = new LoadBundle(propertiesGateway, null, undoableRequestGateway);
    }

    @Override
    public Response execute(Request request) {
        Response response = new Response();
        Settings settings = settingsGateway.getSettings();
        if (settings.lastFile != null) {
            LoadBundle.Response loadBundleResponse = tryToLoadLastFile(Paths.get(settings.lastFile));
            if (loadBundleResponse != null) {
                response.fileNames = loadBundleResponse.fileNames;
                response.keys = loadBundleResponse.keys;
            }
        }
        return response;
    }

    private LoadBundle.Response tryToLoadLastFile(Path lastFile) {
        try {
            LoadBundle.Request request = new LoadBundle.Request();
            request.propertiesFile = lastFile;
            return loadBundle.execute(request);
        } catch (Throwable e) {
            return null;
        }
    }

    public static class Request {
    }

    public static class Response extends LoadBundle.Response {
    }
}

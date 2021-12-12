package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.gateways.SettingsGateway;
import org.jusecase.properties.gateways.UndoableRequestGateway;

public class Initialize implements Usecase<Initialize.Request, Initialize.Response> {

    private final ReloadBundle reloadBundle;

    public Initialize(PropertiesGateway propertiesGateway, SettingsGateway settingsGateway, UndoableRequestGateway undoableRequestGateway) {
        this.reloadBundle = new ReloadBundle(propertiesGateway, settingsGateway, undoableRequestGateway);
    }

    @Override
    public Response execute(Request request) {
        Response response = new Response();

        ReloadBundle.Response reloadBundleResponse = reloadBundle.execute(new ReloadBundle.Request());
        response.fileNames = reloadBundleResponse.fileNames;
        response.keys = reloadBundleResponse.keys;

        return response;
    }

    public static class Request {
    }

    public static class Response extends LoadBundle.Response {
    }
}

package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.Settings;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.gateways.SettingsGateway;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class LoadBundle implements Usecase<LoadBundle.Request, LoadBundle.Response> {

    private final PropertiesGateway propertiesGateway;
    private final SettingsGateway settingsGateway;

    @Inject
    public LoadBundle(PropertiesGateway propertiesGateway, SettingsGateway settingsGateway) {
        this.propertiesGateway = propertiesGateway;
        this.settingsGateway = settingsGateway;
    }

    public Response execute(Request request) {
        if (request.propertiesFile == null) {
            throw new RuntimeException("Properties file must not be null!");
        }

        if (!Files.exists(request.propertiesFile)) {
            throw new RuntimeException("The selected file does not exist!");
        }

        List<Path> propertyFiles = findPropertyFiles(request.propertiesFile);
        propertiesGateway.loadProperties(propertyFiles);
        updateSettings(request);

        Response response = new Response();
        response.keys = propertiesGateway.getKeys();
        response.fileNames = propertyFiles.stream().map(path -> path.getFileName().toString()).collect(Collectors.toList());
        return response;
    }

    private void updateSettings(Request request) {
        if (settingsGateway != null) {
            Settings settings = settingsGateway.getSettings();
            settings.lastFile = request.propertiesFile.toString();
            settingsGateway.saveSettings(settings);
        }
    }

    private List<Path> findPropertyFiles(Path file) {
        List<Path> propertyFiles = new ArrayList<>();
        Path parent = file.getParent();
        String filePrefix = getPropertyFilePrefix(file);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(parent, "*.properties")) {
            for (Path potentialPropertyFile : directoryStream) {
                if (potentialPropertyFile.getFileName().toString().startsWith(filePrefix)) {
                    propertyFiles.add(potentialPropertyFile);
                }
            }
        } catch (IOException e) {
            throw new UsecaseException("Failed to detect properties in directory!", e);
        }
        return propertyFiles;
    }

    private String getPropertyFilePrefix(Path file) {
        String prefix = file.getFileName().toString();
        int prefixEndIndex = prefix.indexOf('_');
        if (prefixEndIndex < 0) {
            prefixEndIndex = prefix.indexOf('.');
        }
        prefix = prefix.substring(0, prefixEndIndex);
        return prefix;
    }

    public static class Request {
        public Path propertiesFile;
    }

    public static class Response {
        public List<String> keys;
        public List<String> fileNames;
    }
}

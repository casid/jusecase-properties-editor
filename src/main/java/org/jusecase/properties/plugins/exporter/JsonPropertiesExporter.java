package org.jusecase.properties.plugins.exporter;

import org.jusecase.properties.entities.Property;
import org.jusecase.properties.usecases.UsecaseException;

import java.util.List;

public class JsonPropertiesExporter implements PropertiesExporter {

    public static final String ID = "jusecase-json-properties-exporter";

    @Override
    public String getPluginId() {
        return ID;
    }

    @Override
    public String getPluginName() {
        return "JSON";
    }

    @Override
    public void exportProperties(List<Property> properties) {
        // TODO!
        throw new UsecaseException("Not yet able to actually export!");
    }
}

package org.jusecase.properties.plugins.exporter;

import org.jusecase.properties.entities.Property;
import org.jusecase.properties.plugins.PluginTrainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesExporterTrainer extends PluginTrainer implements PropertiesExporter {

    private List<Property> exportedProperties;

    @Override
    public void exportProperties(List<Property> properties) {
        exportedProperties = properties;
    }

    public List<Property> getExportedProperties() {
        return exportedProperties;
    }

    public void thenNoPropertiesAreExported() {
        assertThat(getExportedProperties()).isNull();
    }
}
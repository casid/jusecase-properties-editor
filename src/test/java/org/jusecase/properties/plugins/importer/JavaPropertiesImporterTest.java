package org.jusecase.properties.plugins.importer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.testPath;

import java.util.List;

import org.junit.Test;
import org.jusecase.properties.entities.Property;


public class JavaPropertiesImporterTest {
    private JavaPropertiesImporter importer = new JavaPropertiesImporter();

    @Test
    public void import_de() {
        List<Property> properties = importer.importProperties(a(testPath("resources_de.properties")));
        assertThat(properties).hasSize(9);
        assertThat(properties.get(0).fileName).isEqualTo("de");
    }

    @Test
    public void import_root() {
        List<Property> properties = importer.importProperties(a(testPath("resources.properties")));
        assertThat(properties).hasSize(11);
        assertThat(properties.get(0).fileName).isEqualTo("");
    }
}
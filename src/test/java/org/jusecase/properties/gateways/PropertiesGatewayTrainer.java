package org.jusecase.properties.gateways;

import org.jusecase.properties.entities.Property;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.testPath;

public class PropertiesGatewayTrainer implements PropertiesGateway {
    private Set<Path> loadedProperties = new HashSet<>();
    private Property updatedValue;

    @Override
    public void loadProperties(List<Path> files) {
        loadedProperties.addAll(files);
    }

    @Override
    public List<String> getKeys() {
        return null;
    }

    @Override
    public List<Property> getProperties(String key) {
        return null;
    }

    @Override
    public List<String> search(String query) {
        return null;
    }

    @Override
    public void updateValue(Property property) {
        updatedValue = property;
    }

    @Override
    public void save() {

    }

    public void thenLoadedPropertiesAre(Set<Path> expected) {
        assertThat(loadedProperties).isEqualTo(expected);
    }

    public void thenLoadedPropertiesAre(String... fileNamesInTestResources) {
        Set<Path> expected = new HashSet<>();
        for (String fileName : fileNamesInTestResources) {
            expected.add(a(testPath(fileName)));
        }

        thenLoadedPropertiesAre(expected);
    }

    public void thenNoPropertiesAreLoaded() {
        assertThat(loadedProperties).isEmpty();
    }

    public void thenNoValueIsUpdated() {
        assertThat(updatedValue).isNull();
    }

    public Property getUpdatedValue() {
        return updatedValue;
    }
}
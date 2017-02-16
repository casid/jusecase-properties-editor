package org.jusecase.properties.gateways;

import org.junit.Test;
import org.jusecase.properties.entities.Property;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.properties.entities.Builders.property;

public abstract class PropertiesGatewayTest {
    private static final int SAMPLE_KEY_COUNT = 11;

    protected PropertiesGateway gateway;


    @Test
    public void loadProperties_none() {
        givenProperties();
        assertThat(gateway.getKeys()).hasSize(0);
    }

    @Test
    public void loadProperties_one() {
        givenProperties("resources.properties");
        assertThat(gateway.getKeys()).hasSize(SAMPLE_KEY_COUNT);
    }

    @Test
    public void loadProperties_two() {
        givenProperties("resources.properties", "resources_de.properties");
        assertThat(gateway.getKeys()).hasSize(SAMPLE_KEY_COUNT);
    }

    @Test
    public void getKeys_uninitialized() {
        assertThat(gateway.getKeys()).isEmpty();
    }

    @Test
    public void getKeys() {
        givenProperties("resources.properties");
        assertThat(gateway.getKeys().get(0)).isEqualTo("sample.camelCase");
        assertThat(gateway.getKeys().get(1)).isEqualTo("sample.long1");
    }

    @Test
    public void getByKey_uninitialized() {
        assertThat(gateway.getProperties("no npe wanted here!")).isEmpty();
    }

    @Test
    public void getByKey_match() {
        givenProperties("resources.properties");

        List<Property> properties = gateway.getProperties("sample1");

        assertThat(properties.size()).isEqualTo(1);
        assertThat(properties.get(0).key).isEqualTo("sample1");
        assertThat(properties.get(0).value).isEqualTo("Sample 1");
    }

    @Test
    public void getByKey_match_multipleFiles() {
        givenProperties("resources.properties", "resources_de.properties");

        List<Property> properties = gateway.getProperties("sample1");

        assertThat(properties.size()).isEqualTo(2);
        assertThat(properties.get(0).key).isEqualTo("sample1");
        assertThat(properties.get(0).value).isEqualTo("Sample 1");
        assertThat(properties.get(0).fileName).isEqualTo("resources.properties");
        assertThat(properties.get(1).key).isEqualTo("sample1");
        assertThat(properties.get(1).value).isEqualTo("Beispiel 1");
        assertThat(properties.get(1).fileName).isEqualTo("resources_de.properties");
    }

    @Test
    public void getByKey_noMatch() {
        givenProperties("resources.properties");
        List<Property> properties = gateway.getProperties("sample");
        assertThat(properties.size()).isEqualTo(0);
    }

    @Test
    public void search_uninitialized() {
        assertThat(gateway.search("no npe wanted here!")).isEmpty();
    }

    @Test
    public void search_empty() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("");
        assertThat(keys).hasSize(SAMPLE_KEY_COUNT);
    }

    @Test
    public void search_keyPrefix() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("sample.lo");
        assertThat(keys).containsExactly("sample.long1", "sample.long2");
    }

    @Test
    public void search_keyPart() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("le.lo");
        assertThat(keys).containsExactly("sample.long1", "sample.long2");
    }

    @Test
    public void search_keyCamelCase() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("camelCase");
        assertThat(keys).containsExactly("sample.camelCase");
    }

    @Test
    public void search_value1() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("Lucene");
        assertThat(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_value2() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("search");
        assertThat(keys).containsExactly("sample.long1", "sample.long2");
    }

    @Test
    public void search_whitespaceInQuery() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("Apache Lucene");
        assertThat(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_queryWithThirdWordMatchingEverything() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("Apache Lucene s");
        assertThat(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_separatedByMinus() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("full-featured");
        assertThat(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_separatedByComma() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("Powerful, Accurate");
        assertThat(keys).containsExactly("sample.long2");
    }

    @Test
    public void search_multipleFiles() {
        givenProperties("resources.properties", "resources_de.properties");
        List<String> keys = gateway.search("sample");
        assertThat(keys).hasSize(SAMPLE_KEY_COUNT);
    }

    @Test
    public void search_fullSentence() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("Apache Lucene is a high-performance, full-featured text search");
        assertThat(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_onlyAFewLettersOfSentence() {
        givenProperties("resources.properties");
        List<String> keys = gateway.search("Ap");
        assertThat(keys).contains("sample.long1");
    }

    @Test
    public void updateValue_uninitialized() {
        gateway.updateValue(a(property()));
    }

    @Test
    public void updateValue() {
        givenProperties("resources.properties", "resources_de.properties");
        Property german = gateway.getProperties("sample8").get(1);

        german.value = "Allmächd!!!";
        gateway.updateValue(german);

        assertThat(gateway.getProperties("sample8").get(1).value).isEqualTo("Allmächd!!!");
    }

    @Test
    public void updateValue_null() {
        givenProperties("resources.properties", "resources_de.properties");
        Property german = gateway.getProperties("sample8").get(1);

        german.value = null;
        gateway.updateValue(german);

        assertThat(gateway.getProperties("sample8")).hasSize(1); // Remove null property value from result list
    }

    private void givenProperties(String ... fileNames) {
        List<Path> paths = new ArrayList<>();
        for (String fileName : fileNames) {
            paths.add(resolvePath(fileName));
        }
        gateway.loadProperties(paths);
    }

    private Path resolvePath(String fileName) {
        return Paths.get("src", "test", "resources", fileName);
    }
}
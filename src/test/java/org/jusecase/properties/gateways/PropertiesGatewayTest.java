package org.jusecase.properties.gateways;

import org.junit.Test;
import org.jusecase.properties.entities.Property;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.Builders.list;
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
    public void loadProperties_twice() {
        givenProperties("resources.properties", "resources_de.properties");
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
        assertThat(properties).isEmpty();
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
        assertThat(keys).containsExactly("sample.long1");
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
        List<String> keys = gateway.search("Apache Lucene is");
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

        assertThat(gateway.getProperties("sample8").get(1).value).isNull();
    }

    @Test
    public void updateValue_previousValueIsNull() {
        givenProperties("resources.properties", "resources_de.properties");
        Property german = gateway.getProperties("sample.long1").get(1);

        german.value = "endlich ein wert";
        gateway.updateValue(german);

        assertThat(gateway.getProperties("sample.long1").get(1).value).isEqualTo("endlich ein wert");
    }

    @Test
    public void addKey_uninitialized() {
        gateway.addKey("key"); // shall not throw
    }

    @Test
    public void addKey() {
        givenProperties("resources.properties", "resources_de.properties");

        gateway.addKey("key");

        List<Property> properties = gateway.getProperties("key");
        assertThat(properties.get(0).key).isEqualTo("key");
        assertThat(properties.get(0).fileName).isEqualTo("resources.properties");
        assertThat(properties.get(0).value).isEqualTo("");
        assertThat(properties.get(1).value).isEqualTo(null); // initial empty value only added to first file!
    }

    @Test
    public void addKey_keyCacheIsReset() {
        givenProperties("resources.properties");
        gateway.getKeys();

        gateway.addKey("key");

        assertThat(gateway.getKeys()).contains("key");
    }

    @Test(expected = GatewayException.class)
    public void addKey_keyAlreadyExists() {
        givenProperties("resources.properties");
        gateway.addKey("sample1");
    }

    @Test
    public void addProperties_uninitialized() {
        gateway.addProperties(new ArrayList<>()); // shall not throw
    }

    @Test(expected = GatewayException.class)
    public void addProperties_unknownFileName() {
        givenProperties("resources.properties");
        gateway.addProperties(a(list(
                a(property().withFileName("unknown.properties"))
        )));
    }

    @Test
    public void addProperties() {
        givenProperties("resources.properties", "resources_de.properties");

        gateway.addProperties(a(list(
                a(property().withFileName("resources.properties").withKey("one").withValue("One")),
                a(property().withFileName("resources_de.properties").withKey("one").withValue("Eins"))
        )));

        List<Property> properties = gateway.getProperties("one");
        assertThat(properties).hasSize(2);
        assertThat(properties.get(0).fileName).isEqualTo("resources.properties");
        assertThat(properties.get(0).key).isEqualTo("one");
        assertThat(properties.get(0).value).isEqualTo("One");
        assertThat(properties.get(1).fileName).isEqualTo("resources_de.properties");
        assertThat(properties.get(1).key).isEqualTo("one");
        assertThat(properties.get(1).value).isEqualTo("Eins");
    }

    @Test
    public void renameKey_uninitialized() {
        gateway.renameKey("sample8", "sample9"); // shall not throw
    }

    @Test
    public void renameKey() {
        givenProperties("resources.properties");

        gateway.renameKey("sample8", "sample9");

        assertThat(gateway.getProperties("sample8")).isEmpty();
        assertThat(gateway.getProperties("sample9")).hasSize(1);
    }

    @Test(expected = GatewayException.class)
    public void renameKey_alreadyExists() {
        givenProperties("resources.properties");
        gateway.renameKey("sample7", "sample8");
    }

    @Test
    public void renameKey_doesNotExist() {
        givenProperties("resources.properties");
        gateway.renameKey("unknown", "sample9");
        assertThat(gateway.getProperties("unknown")).isEmpty();
    }

    @Test
    public void duplicateKey_uninitialized() {
        gateway.duplicateKey("sample8", "sample9"); // shall not throw
    }

    @Test(expected = GatewayException.class)
    public void duplicateKey_alreadyExists() {
        givenProperties("resources.properties");
        gateway.duplicateKey("sample7", "sample8");
    }

    @Test
    public void duplicateKey_doesNotExist() {
        givenProperties("resources.properties");
        gateway.duplicateKey("unknown", "sample9");
        assertThat(gateway.getProperties("unknown")).isEmpty();
    }

    @Test
    public void duplicateKey() {
        givenProperties("resources.properties", "resources_de.properties");

        gateway.duplicateKey("sample8", "sample9");

        assertThat(gateway.getProperties("sample8")).hasSize(2);
        assertThat(gateway.getProperties("sample9")).hasSize(2);
        assertThat(gateway.getProperties("sample9").get(0).key).isEqualTo("sample9");
        assertThat(gateway.getProperties("sample9").get(0).value).isEqualTo("Sample 8");
        assertThat(gateway.getProperties("sample9").get(0).fileName).isEqualTo("resources.properties");
        assertThat(gateway.getProperties("sample9").get(1).key).isEqualTo("sample9");
        assertThat(gateway.getProperties("sample9").get(1).value).isEqualTo("Beispiel 8");
        assertThat(gateway.getProperties("sample9").get(1).fileName).isEqualTo("resources_de.properties");
    }

    @Test
    public void deleteKey_uninitialized() {
        gateway.deleteKey("sample8"); // shall not throw
    }

    @Test
    public void deleteKey_doesNotExist() {
        givenProperties("resources.properties");
        gateway.deleteKey("unknown");
        assertThat(gateway.getKeys()).hasSize(SAMPLE_KEY_COUNT);
    }

    @Test
    public void deleteKey() {
        givenProperties("resources.properties", "resources_de.properties");
        gateway.deleteKey("sample8");
        assertThat(gateway.getProperties("sample8")).hasSize(0);
        assertThat(gateway.getKeys()).hasSize(SAMPLE_KEY_COUNT - 1);
    }

    @Test
    public void save_uninitialized() {
        gateway.save(); // shall not throw
    }

    @Test
    public void save() {
        givenProperties("for-save.properties");
        Property property = gateway.getProperties("sample").get(0);
        property.value = "I've been changed!";
        gateway.updateValue(property);

        gateway.save();

        gateway = new InMemoryPropertiesGateway();
        givenProperties("for-save.properties");
        assertThat(gateway.getProperties("sample").get(0).value).isEqualTo(property.value);

        // Reset to initial value
        property.value = "change me";
        gateway.updateValue(property);
        gateway.save();
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
package org.jusecase.properties.gateways;

import org.assertj.core.api.ListAssert;
import org.junit.Before;
import org.junit.Test;
import org.jusecase.properties.entities.Key;
import org.jusecase.properties.entities.KeyPopulation;
import org.jusecase.properties.entities.Property;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.Builders.list;
import static org.jusecase.properties.entities.Builders.property;
import static org.jusecase.properties.entities.Builders.testPath;

public abstract class PropertiesGatewayTest {
    private static final int SAMPLE_KEY_COUNT = 11;

    protected PropertiesGateway gateway;

    @Before
    public void setUp() {
        gateway = createGateway();
    }

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
    public void loadProperties_unsavedChanges() {
        givenProperties("resources.properties");
        assertThat(gateway.hasUnsavedChanges()).isFalse();
    }

    @Test
    public void getKeys_uninitialized() {
        assertThat(gateway.getKeys()).isEmpty();
    }

    @Test
    public void getKeys() {
        givenProperties("resources.properties");
        assertThat(gateway.getKeys().get(0).getKey()).isEqualTo("sample.camelCase");
        assertThat(gateway.getKeys().get(1).getKey()).isEqualTo("sample.long1");
    }

    @Test
    public void getKeys_state_oneFile() {
        givenProperties("resources.properties");
        for (Key key : gateway.getKeys()) {
            assertThat(key.getPopulation()).isEqualTo(KeyPopulation.Complete); // if only one file, all existing keys are complete
        }
    }

    @Test
    public void getKeys_state_twoFiles() {
        givenProperties("resources.properties", "resources_de.properties");
        assertThat(gateway.getKeys().get(0).getPopulation()).isEqualTo(KeyPopulation.Complete); // sample.camelCase
        assertThat(gateway.getKeys().get(1).getPopulation()).isEqualTo(KeyPopulation.Sparse); // sample.long1
    }

    @Test
    public void getKeys_unsavedChanges() {
        givenProperties("resources.properties");
        gateway.getKeys();
        assertThat(gateway.hasUnsavedChanges()).isFalse();
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
    public void getByKey_unsavedChanges() {
        givenProperties("resources.properties");
        gateway.getProperties("sample1");
        assertThat(gateway.hasUnsavedChanges()).isFalse();
    }

    @Test
    public void resolveFileName_none() {
        givenProperties();
        assertThat(gateway.resolveFileName("de")).isNull();
    }

    @Test
    public void resolveFileName_match() {
        givenProperties("resources_de.properties");
        assertThat(gateway.resolveFileName("de")).isEqualTo("resources_de.properties");
    }

    @Test
    public void resolveFileName_notTooGreedy() {
        givenProperties("resources_de.properties", "resources.properties");
        assertThat(gateway.resolveFileName("")).isEqualTo("resources.properties");
    }

    @Test
    public void resolveFileName_notTooGreedy2() {
        givenProperties("resources.properties", "resources_de.properties");
        assertThat(gateway.resolveFileName("")).isEqualTo("resources.properties");
        assertThat(gateway.resolveFileName("de")).isEqualTo("resources_de.properties");
        assertThat(gateway.resolveFileName("ru")).isNull();
    }

    @Test
    public void search_uninitialized() {
        assertThat(gateway.search("no npe wanted here!")).isEmpty();
    }

    @Test
    public void search_empty() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("");
        assertThat(keys).hasSize(SAMPLE_KEY_COUNT);
    }

    @Test
    public void search_keyPrefix() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("sample.lo");
        assertThatKeys(keys).containsExactly("sample.long1", "sample.long2");
    }

    @Test
    public void search_keyPart() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("le.lo");
        assertThatKeys(keys).containsExactly("sample.long1", "sample.long2");
    }

    @Test
    public void search_keyCamelCase() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("camelCase");
        assertThatKeys(keys).containsExactly("sample.camelCase");
    }

    @Test
    public void search_value1() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("Lucene");
        assertThatKeys(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_value2() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("search engine");
        assertThatKeys(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_whitespaceInQuery() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("Apache Lucene");
        assertThatKeys(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_queryWithThirdWordMatchingEverything() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("Apache Lucene is");
        assertThatKeys(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_separatedByMinus() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("full-featured");
        assertThatKeys(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_separatedByComma() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("Powerful, Accurate");
        assertThatKeys(keys).containsExactly("sample.long2");
    }

    @Test
    public void search_multipleFiles() {
        givenProperties("resources.properties", "resources_de.properties");
        List<Key> keys = gateway.search("sample");
        assertThatKeys(keys).hasSize(SAMPLE_KEY_COUNT);
    }

    @Test
    public void search_fullSentence() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("Apache Lucene is a high-performance, full-featured text search");
        assertThatKeys(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_lowercase() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("apache lucene");
        assertThatKeys(keys).containsExactly("sample.long1");
    }

    @Test
    public void search_onlyAFewLettersOfSentence() {
        givenProperties("resources.properties");
        List<Key> keys = gateway.search("Ap");
        assertThatKeys(keys).contains("sample.long1");
    }

    @Test
    public void search_unsavedChanges() {
        givenProperties("resources.properties");
        gateway.search("sample1");
        assertThat(gateway.hasUnsavedChanges()).isFalse();
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
        assertThat(gateway.getKey("sample8").getPopulation()).isEqualTo(KeyPopulation.Complete);
    }

    @Test
    public void updateValue_null() {
        givenProperties("resources.properties", "resources_de.properties");
        Property german = gateway.getProperties("sample8").get(1);

        german.value = null;
        gateway.updateValue(german);

        assertThat(gateway.getProperties("sample8").get(1).value).isNull();
        assertThat(gateway.getKey("sample8").getPopulation()).isEqualTo(KeyPopulation.Sparse);
    }

    @Test
    public void updateValue_previousValueIsNull() {
        givenProperties("resources.properties", "resources_de.properties");
        Property german = gateway.getProperties("sample.long1").get(1);

        german.value = "endlich ein wert";
        gateway.updateValue(german);

        assertThat(gateway.getProperties("sample.long1").get(1).value).isEqualTo("endlich ein wert");
        assertThat(gateway.getKey("sample.long1").getPopulation()).isEqualTo(KeyPopulation.Complete);
    }

    @Test
    public void updateValue_unsavedChanges() {
        givenProperties("resources.properties");
        Property property = gateway.getProperties("sample1").get(0);

        property.value = "something else";
        gateway.updateValue(property);

        assertThat(gateway.hasUnsavedChanges()).isTrue();
    }

    @Test
    public void updateValue_lowerCaseSearchStillWorks() {
        givenProperties("resources.properties");
        Property property = gateway.getProperties("sample1").get(0);

        property.value = "UPPERCASE";
        gateway.updateValue(property);

        assertThat(gateway.search("uppercase")).hasSize(1);
    }

    @Test
    public void updateValue_lowerCaseSearchStillWorks_ifPreviousValueIsNull() {
        givenProperties("resources.properties", "resources_de.properties");
        Property german = gateway.getProperties("sample.long1").get(1);

        german.value = "ENDLICH EIN WERT";
        gateway.updateValue(german);

        assertThat(gateway.search("endlich ein wert")).hasSize(1);
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

        assertThatKeys(gateway.getKeys()).contains("key");
    }

    @Test(expected = GatewayException.class)
    public void addKey_keyAlreadyExists() {
        givenProperties("resources.properties");
        gateway.addKey("sample1");
    }

    @Test
    public void addKey_unsavedChanges() {
        givenProperties("resources.properties");
        gateway.addKey("key");
        assertThat(gateway.hasUnsavedChanges()).isTrue();
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
    public void addProperties_nullValue_notAddedToInternalModel() {
        givenProperties("resources.properties", "resources_de.properties");

        gateway.addProperties(a(list(
                a(property().withFileName("resources.properties").withKey("one").withValue("value")),
                a(property().withFileName("resources_de.properties").withKey("one").withValue(null))
        )));

        assertThat(gateway.getKey("one").getPopulation()).isEqualTo(KeyPopulation.Sparse);
    }

    @Test
    public void addProperties_areCloned() {
        givenProperties("resources.properties");
        Property property = a(property().withValue("sneaky"));

        gateway.addProperties(a(list(property)));

        property.value = null;
        gateway.search("sneaky"); // We do not want a null pointer exception here (a clone of the property must be added to the gateway!)
    }

    @Test
    public void addProperties_willNoExistTwice() {
        givenProperties("resources.properties");
        Property property = a(property().withKey("sample1").withValue("Sample 1"));

        gateway.addProperties(a(list(property)));
        property.value = "new value";
        gateway.updateValue(property);

        assertThat(gateway.getProperties("sample1").get(0).value).isEqualTo("new value");
    }

    @Test
    public void addProperties_unsavedChanges() {
        givenProperties("resources.properties");
        gateway.addProperties(a(list(a(property()))));
        assertThat(gateway.hasUnsavedChanges()).isTrue();
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
    public void renameKey_unsavedChanges() {
        givenProperties("resources.properties");
        gateway.renameKey("sample8", "sample9");
        assertThat(gateway.hasUnsavedChanges()).isTrue();
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
    public void duplicateKey_unsavedChanges() {
        givenProperties("resources.properties");
        gateway.duplicateKey("sample8", "sample9");
        assertThat(gateway.hasUnsavedChanges()).isTrue();
    }

    @Test
    public void duplicateKey_lowerCaseSearchStillWorks() {
        givenProperties("resources.properties");
        gateway.duplicateKey("sample1", "sample1copy");
        assertThat(gateway.search("sample 1")).hasSize(2);
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
    public void deleteKey_unsavedChanges() {
        givenProperties("resources.properties");
        gateway.deleteKey("sample8");
        assertThat(gateway.hasUnsavedChanges()).isTrue();
    }

    @Test
    public void deleteProperties_uninitialized() {
        gateway.deleteProperties(a(list(a(property().withFileName("resources.properties").withKey("sample1"))))); // shall not throw
    }

    @Test
    public void deleteProperties() {
        givenProperties("resources.properties", "resources_de.properties");
        gateway.deleteProperties(a(list(
              a(property().withFileName("resources.properties").withKey("sample1")),
              a(property().withFileName("resources_de.properties").withKey("sample1")),
              a(property().withFileName("resources_de.properties").withKey("sample2"))
        )));
        assertThat(gateway.getProperties("sample1")).hasSize(0);
        assertThat(gateway.getProperties("sample2")).hasSize(2);
        assertThat(gateway.getProperties("sample2").get(1).value).isNull();
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

        assertThat(gateway.hasUnsavedChanges()).isTrue();
        gateway.save();
        assertThat(gateway.hasUnsavedChanges()).isFalse();
        assertThat(gateway.hasExternalChanges()).isFalse();

        gateway = createGateway();
        givenProperties("for-save.properties");
        assertThat(gateway.getProperties("sample").get(0).value).isEqualTo(property.value);

        // Reset to initial value
        property.value = "change me";
        gateway.updateValue(property);
        gateway.save();
    }

    @Test
    public void save_preservesLineEndings_unix() {
        writeTestFile("unix.properties", "key=value\n");
        givenProperties("unix.properties");

        gateway.saveAll();

        thenTestFileHasContent("unix.properties", "key=value\n");
        deleteTestFile("unix.properties");
    }

    @Test
    public void save_preservesLineEndings_windows() {
        writeTestFile("windows.properties", "key=value\r\n");
        givenProperties("windows.properties");

        gateway.saveAll();

        thenTestFileHasContent("windows.properties", "key=value\r\n");
        deleteTestFile("windows.properties");
    }

    @Test
    public void save_preservesLineEndings_unknown_doesNotCrash() {
        writeTestFile("unknown.properties", "key=value");
        givenProperties("unknown.properties");

        gateway.saveAll();

        deleteTestFile("unknown.properties");
    }

    @Test
    public void unsavedChanges_uninitialized() {
        assertThat(gateway.hasUnsavedChanges()).isFalse();
    }

    @Test
    public void externalChanges_uninitialized() {
        assertThat(gateway.hasExternalChanges()).isFalse();
    }

    @Test
    public void externalChanges_none() {
        givenProperties("external-changes.properties");
        assertThat(gateway.hasExternalChanges()).isFalse();
    }

    @Test
    public void externalChanges_differentSize() throws IOException {
        String fileName = "external-changes.properties";

        try {
            givenProperties(fileName);
            writeTestFile(fileName, "sample=changed");
            assertThat(gateway.hasExternalChanges()).isTrue();
        } finally {
            writeTestFile(fileName, "sample=change me");
        }
    }

    @Test
    public void externalChanges_sameSize_differentContent() throws IOException {
        String fileName = "external-changes.properties";

        try {
            givenProperties(fileName);
            writeTestFile(fileName, "sample=change yo");
            assertThat(gateway.hasExternalChanges()).isTrue();
        } finally {
            writeTestFile(fileName, "sample=change me");
        }
    }

    @Test
    public void externalChanges_sameContent() throws IOException {
        String fileName = "external-changes.properties";

        givenProperties(fileName);
        writeTestFile(fileName, "sample=change me");
        assertThat(gateway.hasExternalChanges()).isFalse();
    }

    protected abstract PropertiesGateway createGateway();

    private void givenProperties(String... fileNames) {
        List<Path> paths = new ArrayList<>();
        for (String fileName : fileNames) {
            paths.add(resolvePath(fileName));
        }
        gateway.loadProperties(paths);
    }

    private ListAssert<String> assertThatKeys(List<Key> keys) {
        List<String> keyStrings = keys.stream().map(Key::getKey).collect(Collectors.toList());
        return new ListAssert<>(keyStrings);
    }

    private Path resolvePath(String fileName) {
        return a(testPath(fileName));
    }

    private void writeTestFile(String fileName, String content) {
        try {
            Files.write(a(testPath(fileName)), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void thenTestFileHasContent(String fileName, String expectedContent) {
        Path file = a(testPath(fileName));
        assertThat(file).hasBinaryContent(expectedContent.getBytes());
    }

    private void deleteTestFile(String fileName) {
        try {
            Files.deleteIfExists(a(testPath(fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
package org.jusecase.properties.usecases;


import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.entities.Key;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.gateways.SettingsGatewayTrainer;
import org.jusecase.properties.usecases.Search.Request;
import org.jusecase.properties.usecases.Search.Response;

public class Search_HistoryTest extends UsecaseTest<Request, Response> {

    PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();
    SettingsGatewayTrainer settingsGatewayTrainer = new SettingsGatewayTrainer();

    @Before
    public void setUp() {
        usecase = new Search(propertiesGatewayTrainer, null, settingsGatewayTrainer);
        propertiesGatewayTrainer.givenSearchResults(new Key("foo"));
    }

    @Test
    public void nullQuery() {
        whenSearchQueryIsSent(null);
        settingsGatewayTrainer.thenSearchHistoryIs();
    }

    @Test
    public void emptyQuery() {
        whenSearchQueryIsSent("");
        settingsGatewayTrainer.thenSearchHistoryIs();
    }

    @Test
    public void typeSingleLetter() {
        whenSearchQueryIsSent("b");
        settingsGatewayTrainer.thenSearchHistoryIs("b");
    }

    @Test
    public void typeSingleWord() {
        whenSearchQueryIsSent("b");
        whenSearchQueryIsSent("bl");
        whenSearchQueryIsSent("bla");
        settingsGatewayTrainer.thenSearchHistoryIs("bla");
    }

    @Test
    public void deleteCharacter() {
        whenSearchQueryIsSent("bla");
        whenSearchQueryIsSent("bl");
        settingsGatewayTrainer.thenSearchHistoryIs("bl", "bla");
    }

    @Test
    public void pasteDifferentWords() {
        whenSearchQueryIsSent("foo");
        whenSearchQueryIsSent("bar");
        settingsGatewayTrainer.thenSearchHistoryIs("bar", "foo");
    }

    @Test
    public void maxHistorySize() {
        settingsGatewayTrainer.getSettings().maxSearchHistorySize = 2;

        whenSearchQueryIsSent("foo");
        whenSearchQueryIsSent("bar");
        whenSearchQueryIsSent("boo");

        settingsGatewayTrainer.thenSearchHistoryIs("boo", "bar");
    }

    @Test
    public void name() {
        settingsGatewayTrainer.givenSearchHistory("foo", "bar", "bla");
        whenSearchQueryIsSent("bar");
        settingsGatewayTrainer.thenSearchHistoryIs("bar", "foo", "bla");
    }

    @Test
    public void noResult() {
        propertiesGatewayTrainer.givenSearchResults();
        whenSearchQueryIsSent("u3rgp9p5");
        settingsGatewayTrainer.thenSearchHistoryIs();
    }

    protected void whenSearchQueryIsSent(String query) {
        request.query = query;
        whenRequestIsExecuted();
    }
}
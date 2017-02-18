package org.jusecase.properties.usecases;

import org.junit.Test;
import org.jusecase.properties.usecases.Redo.Request;
import org.jusecase.properties.usecases.Redo.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class RedoTest extends AbstractUndoRedoTest<Request, Response> {

    @Override
    public void setUp() {
        super.setUp();
        usecase = new Redo(usecaseExecutor, undoableRequestGateway);
        request = new Request();
    }

    @Test
    public void noRedoableRequests() {
        whenRedo();
    }

    @Test
    public void oneRedoableRequest() {
        givenDummyUsecaseRequests("bam");
        whenRedo();
        thenActionsAre("redo(bam)");
    }

    @Test
    public void twoRedoableRequests() {
        givenDummyUsecaseRequests("1", "2");

        whenRedo();
        whenRedo();

        thenActionsAre("redo(1)", "redo(2)");
    }

    @Test
    public void tooMuchRedo() {
        givenDummyUsecaseRequests("1");

        whenRedo();
        whenRedo();

        thenActionsAre("redo(1)");
    }

    @Test
    public void response() {
        givenDummyUsecaseRequests("bam");
        whenRedo();
        assertThat(((DummyUsecase.Request)response.redoRequest).action).isEqualTo("bam");
        assertThat(response.redoResponse).isEqualTo("bam");
    }

    @Override
    protected void givenDummyUsecaseRequests(String... actions) {
        super.givenDummyUsecaseRequests(actions);
        for (String ignored : actions) {
            undoableRequestGateway.markNextRequestForUndo();
        }
    }

    private void whenRedo() {
        whenRequestIsExecuted();
    }
}
package org.jusecase.properties.usecases;

import org.junit.Test;
import org.jusecase.properties.usecases.Undo.Request;
import org.jusecase.properties.usecases.Undo.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class UndoTest extends AbstractUndoRedoTest<Request, Response> {

    @Override
    public void setUp() {
        super.setUp();
        usecase = new Undo(usecaseExecutor, undoableRequestGateway);
        request = new Undo.Request();
    }

    @Test
    public void noUndoableRequests() {
        whenUndo();
    }

    @Test
    public void oneUndoableRequest() {
        givenDummyUsecaseRequests("bam");
        whenUndo();
        thenActionsAre("undo(bam)");
    }

    @Test
    public void twoUndoableRequests() {
        givenDummyUsecaseRequests("1", "2");

        whenUndo();
        whenUndo();

        thenActionsAre("undo(2)", "undo(1)");
    }

    @Test
    public void tooMuchUndo() {
        givenDummyUsecaseRequests("1");

        whenUndo();
        whenUndo();

        thenActionsAre("undo(1)");
    }

    @Test
    public void response() {
        givenDummyUsecaseRequests("bam");
        whenUndo();
        assertThat(((DummyUsecase.Request)response.undoRequest).action).isEqualTo("bam");
        assertThat(response.undoResponse).isEqualTo("bam");
    }

    private void whenUndo() {
        whenRequestIsExecuted();
    }
}
package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.properties.gateways.UndoableRequestGateway;
import org.jusecase.properties.usecases.GetUndoStatus.Request;
import org.jusecase.properties.usecases.GetUndoStatus.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.an;
import static org.jusecase.properties.entities.Builders.undoableRequest;

public class GetUndoStatusTest extends UsecaseTest<Request, Response> {
    private final UndoableRequestGateway undoableRequestGateway = new UndoableRequestGateway();

    @Before
    public void setUp() {
        usecase = new GetUndoStatus(undoableRequestGateway);
    }

    @Test
    public void nothingToDo() {
        whenRequestIsExecuted();
        assertThat(response.undoAction).isNull();
        assertThat(response.redoAction).isNull();
    }

    @Test
    public void oneThingDone() {
        undoableRequestGateway.add(an(undoableRequest().withName("Edit Value")));

        whenRequestIsExecuted();

        assertThat(response.undoAction).isEqualTo("Undo Edit Value");
        assertThat(response.redoAction).isNull();
    }

    @Test
    public void oneThingAlreadyUndone() {
        undoableRequestGateway.add(an(undoableRequest().withName("Edit Value")));
        undoableRequestGateway.markNextRequestForUndo();

        whenRequestIsExecuted();

        assertThat(response.undoAction).isNull();
        assertThat(response.redoAction).isEqualTo("Redo Edit Value");
    }
}
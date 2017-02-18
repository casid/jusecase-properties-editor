package org.jusecase.properties.usecases;

import org.junit.After;
import org.junit.Before;
import org.jusecase.Usecase;
import org.jusecase.UsecaseTest;
import org.jusecase.executors.manual.ManualUsecaseExecutor;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.UndoableRequestGateway;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractUndoRedoTest<Request, Response> extends UsecaseTest<Request, Response> {
    protected ManualUsecaseExecutor usecaseExecutor = new ManualUsecaseExecutor();
    protected UndoableRequestGateway undoableRequestGateway = new UndoableRequestGateway();

    protected List<String> actions = new ArrayList<>();

    @Before
    public void setUp() {
        usecaseExecutor.addUsecase(new UndoTest.DummyUsecase(actions));
    }

    @After
    public void tearDown() {
        if (error != null) {
            error.printStackTrace();
        }
        assertThat(error).isNull();
    }

    protected void thenActionsAre(String ... expected) {
        assertThat(actions).containsExactly(expected);
    }

    protected void givenDummyUsecaseRequests(String ... actions) {
        for (String action : actions) {
            DummyUsecase.Request dummyRequest = new DummyUsecase.Request();
            dummyRequest.action = action;
            undoableRequestGateway.add(dummyRequest);
        }
    }

    protected static class DummyUsecase implements Usecase<DummyUsecase.Request, String> {
        private final List<String> actions;

        private DummyUsecase(List<String> actions) {
            this.actions = actions;
        }

        @Override
        public String execute(Request request) {
            String action = (request.undo ? "undo" : "redo") + '(' + request.action + ')';
            actions.add(action);
            return request.action;
        }

        static class Request extends UndoableRequest {
            public String action;
        }
    }
}

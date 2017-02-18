package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.UsecaseExecutor;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.UndoableRequestGateway;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Redo implements Usecase<Redo.Request, Redo.Response> {

    private final UsecaseExecutor usecaseExecutor;
    private final UndoableRequestGateway undoableRequestGateway;

    @Inject
    public Redo(UsecaseExecutor usecaseExecutor, UndoableRequestGateway undoableRequestGateway) {
        this.usecaseExecutor = usecaseExecutor;
        this.undoableRequestGateway = undoableRequestGateway;
    }

    @Override
    public Response execute(Request request) {
        UndoableRequest requestToRedo = undoableRequestGateway.getNextRequestToRedo();
        if (requestToRedo == null) {
            return null;
        }

        requestToRedo.undo = false;

        Response response = new Response();
        response.redoRequest = requestToRedo;
        response.redoResponse = usecaseExecutor.execute(requestToRedo);

        undoableRequestGateway.markNextRequestForRedo();
        return response;
    }

    public static class Request {
    }

    public static class Response {
        public Object redoRequest;
        public Object redoResponse;
    }
}
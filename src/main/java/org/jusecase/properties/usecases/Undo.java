package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.UsecaseExecutor;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.UndoableRequestGateway;

public class Undo implements Usecase<Undo.Request, Undo.Response> {

    private final UsecaseExecutor usecaseExecutor;
    private final UndoableRequestGateway undoableRequestGateway;

    public Undo(UsecaseExecutor usecaseExecutor, UndoableRequestGateway undoableRequestGateway) {
        this.usecaseExecutor = usecaseExecutor;
        this.undoableRequestGateway = undoableRequestGateway;
    }

    @Override
    public Response execute(Request request) {
        UndoableRequest requestToUndo = undoableRequestGateway.getNextRequestToUndo();
        if (requestToUndo == null) {
            return null;
        }

        requestToUndo.undo = true;

        Response response = new Response();
        response.undoRequest = requestToUndo;
        response.undoResponse = usecaseExecutor.execute(requestToUndo);

        undoableRequestGateway.markNextRequestForUndo();
        return response;
    }

    public static class Request {
    }

    public static class Response {
        public Object undoRequest;
        public Object undoResponse;
    }
}
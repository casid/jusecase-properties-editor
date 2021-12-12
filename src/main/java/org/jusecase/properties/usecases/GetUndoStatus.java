package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.UndoableRequestGateway;

public class GetUndoStatus implements Usecase<GetUndoStatus.Request, GetUndoStatus.Response> {

    private final UndoableRequestGateway undoableRequestGateway;

    public GetUndoStatus(UndoableRequestGateway undoableRequestGateway) {
        this.undoableRequestGateway = undoableRequestGateway;
    }

    @Override
    public Response execute(Request request) {
        Response response = new Response();

        UndoableRequest nextRequestToUndo = undoableRequestGateway.getNextRequestToUndo();
        if (nextRequestToUndo != null) {
            response.undoAction = "Undo " + nextRequestToUndo.name;
        }

        UndoableRequest nextRequestToRedo = undoableRequestGateway.getNextRequestToRedo();
        if (nextRequestToRedo != null) {
            response.redoAction = "Redo " + nextRequestToRedo.name;
        }

        return response;
    }

    public static class Request {
    }

    public static class Response {
        public String undoAction;
        public String redoAction;
    }
}

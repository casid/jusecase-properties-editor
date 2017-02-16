package org.jusecase.properties.ui;

import org.jusecase.properties.BusinessLogic;

import java.util.function.Consumer;

public class UsecaseExecutor {
    private final BusinessLogic businessLogic = new BusinessLogic();

    public <Request, Response> void execute(Request request, Consumer<Response> responseConsumer) {
        Response response = businessLogic.execute(request);
        responseConsumer.accept(response);
    }
}

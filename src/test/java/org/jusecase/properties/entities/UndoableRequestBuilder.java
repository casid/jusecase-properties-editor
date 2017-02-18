package org.jusecase.properties.entities;

public class UndoableRequestBuilder implements UndoableRequestBuilderMethods<UndoableRequest, UndoableRequestBuilder> {
    private UndoableRequest request = new UndoableRequest();

    @Override
    public UndoableRequest getEntity() {
        return request;
    }
}

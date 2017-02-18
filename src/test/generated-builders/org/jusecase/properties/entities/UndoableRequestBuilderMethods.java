package org.jusecase.properties.entities;

import org.jusecase.builders.Builder;

@javax.annotation.Generated(value="jusecase-builders-generator")
public interface UndoableRequestBuilderMethods<T extends UndoableRequest, B extends Builder> extends Builder<T> {
    @Override
    default T build() {
        return getEntity();
    }

    T getEntity();

    default B withName(String value) {
        getEntity().name = value;
        return (B)this;
    }

    default B withUndo(boolean value) {
        getEntity().undo = value;
        return (B)this;
    }
}

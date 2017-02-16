package org.jusecase.properties.entities;

import org.jusecase.builders.Builder;

@javax.annotation.Generated(value="jusecase-builders-generator")
public interface PropertyBuilderMethods<T extends Property, B extends Builder> extends Builder<T> {
    @Override
    default T build() {
        return getEntity();
    }

    T getEntity();

    default B withFileName(String value) {
        getEntity().fileName = value;
        return (B)this;
    }

    default B withKey(String value) {
        getEntity().key = value;
        return (B)this;
    }

    default B withValue(String value) {
        getEntity().value = value;
        return (B)this;
    }
}

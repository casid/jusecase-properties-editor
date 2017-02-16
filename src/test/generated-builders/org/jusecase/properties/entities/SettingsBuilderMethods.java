package org.jusecase.properties.entities;

import org.jusecase.builders.Builder;

@javax.annotation.Generated(value="jusecase-builders-generator")
public interface SettingsBuilderMethods<T extends Settings, B extends Builder> extends Builder<T> {
    @Override
    default T build() {
        return getEntity();
    }

    T getEntity();

    default B withLastFile(String value) {
        getEntity().lastFile = value;
        return (B)this;
    }
}

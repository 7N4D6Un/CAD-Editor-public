package com.github.franckyi.databindings.api;

public interface ObjectProperty<T> extends Property<T>, ObservableObjectValue<T> {
    static <T> ObjectProperty<T> create() {
        return DataBindings.getPropertyFactory().createObjectProperty();
    }

    static <T> ObjectProperty<T> create(T value) {
        return DataBindings.getPropertyFactory().createObjectProperty(value);
    }

    default void setValue(T value) {
        set(value);
    }
}

package com.github.franckyi.databindings.api;

import com.github.franckyi.databindings.api.factory.PropertyFactory;

public interface Property<T> extends ObservableValue<T> {
    void set(T value);

    void bind(ObservableValue<? extends T> value);

    void unbind();

    boolean isBound();

    ObservableValue<? extends T> getBoundValue();

    void bindBidirectional(Property<T> other);

    void unbindBidirectional();
}

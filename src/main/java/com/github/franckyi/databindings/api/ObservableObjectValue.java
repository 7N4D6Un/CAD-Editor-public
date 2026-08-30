package com.github.franckyi.databindings.api;

import java.util.function.Supplier;

public interface ObservableObjectValue<T> extends ObservableValue<T> {
    static <T> ObservableObjectValue<T> unmodifiable(T value) {
        return new Unmodifiable<>() {
            @Override
            public T get() {
                return value;
            }
        };
    }

    abstract class Unmodifiable<T> extends ObservableValue.Unmodifiable<T> implements ObservableObjectValue<T> {
    }

    static <T> ObservableObjectValue<T> readOnly(ObjectProperty<T> property) {
        return DataBindings.getPropertyFactory().createReadOnlyProperty(property);
    }

    static <T> ObservableObjectValue<T> observe(Supplier<T> valueSupplier, ObservableValue<?>... triggers) {
        return DataBindings.getMappingFactory().createMapping(valueSupplier, triggers);
    }

    default T getValue() {
        return get();
    }

    default boolean hasValue() {
        return get() != null;
    }
}

package com.github.franckyi.databindings.api;

public interface DoubleProperty extends Property<Double>, ObservableDoubleValue {
    static DoubleProperty create() {
        return DataBindings.getPropertyFactory().createDoubleProperty();
    }

    static DoubleProperty create(double value) {
        return DataBindings.getPropertyFactory().createDoubleProperty(value);
    }

    default void setValue(double value) {
        set(value);
    }

    default void incr() {
        incr(1);
    }

    default void incr(double value) {
        setValue(getValue() + value);
    }

    default void decr() {
        decr(1);
    }

    default void decr(double value) {
        setValue(getValue() - value);
    }
}

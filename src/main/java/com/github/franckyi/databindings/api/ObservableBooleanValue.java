package com.github.franckyi.databindings.api;

import java.util.function.BooleanSupplier;

public interface ObservableBooleanValue extends ObservableValue<Boolean> {
    ObservableBooleanValue TRUE = unmodifiable(true);
    ObservableBooleanValue FALSE = unmodifiable(false);

    static ObservableBooleanValue unmodifiable(boolean value) {
        return new ObservableBooleanValue.Unmodifiable() {
            @Override
            public Boolean get() {
                return value;
            }
        };
    }

    abstract class Unmodifiable extends ObservableValue.Unmodifiable<Boolean> implements ObservableBooleanValue {
    }

    static ObservableBooleanValue readOnly(BooleanProperty property) {
        return DataBindings.getPropertyFactory().createReadOnlyProperty(property);
    }

    static ObservableBooleanValue observe(BooleanSupplier supplier, ObservableValue<?>... triggers) {
        return DataBindings.getMappingFactory().createBooleanMapping(supplier, triggers);
    }

    default boolean getValue() {
        return get() != null && get();
    }

    default ObservableBooleanValue not() {
        return mapToBoolean(b -> !b);
    }

    default ObservableBooleanValue or(ObservableValue<Boolean> other) {
        return observe(() -> get() || other.get(), this, other);
    }

    default ObservableBooleanValue and(ObservableValue<Boolean> other) {
        return observe(() -> get() && other.get(), this, other);
    }
}

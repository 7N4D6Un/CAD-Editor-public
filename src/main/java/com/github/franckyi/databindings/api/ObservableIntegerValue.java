package com.github.franckyi.databindings.api;

import java.util.function.IntSupplier;

public interface ObservableIntegerValue extends ObservableValue<Integer> {
    static ObservableIntegerValue unmodifiable(int value) {
        return new Unmodifiable() {
            @Override
            public Integer get() {
                return value;
            }
        };
    }

    abstract class Unmodifiable extends ObservableValue.Unmodifiable<Integer> implements ObservableIntegerValue {
    }

    static ObservableIntegerValue readOnly(IntegerProperty property) {
        return DataBindings.getPropertyFactory().createReadOnlyProperty(property);
    }

    static ObservableIntegerValue observe(IntSupplier supplier, ObservableValue<?>... triggers) {
        return DataBindings.getMappingFactory().createIntegerMapping(supplier, triggers);
    }

    default int getValue() {
        return get() == null ? 0 : get();
    }

    default ObservableIntegerValue add(int other) {
        return mapToInt(i -> i + other);
    }

    default ObservableIntegerValue substract(int other) {
        return mapToInt(i -> i - other);
    }

    default ObservableIntegerValue multiply(int other) {
        return mapToInt(i -> i * other);
    }

    default ObservableIntegerValue divide(int other) {
        return mapToInt(i -> i / other);
    }

    default ObservableIntegerValue add(ObservableValue<Integer> other) {
        return observe(() -> get() + other.get(), this, other);
    }

    default ObservableIntegerValue substract(ObservableValue<Integer> other) {
        return observe(() -> get() - other.get(), this, other);
    }

    default ObservableIntegerValue multiply(ObservableValue<Integer> other) {
        return observe(() -> get() * other.get(), this, other);
    }

    default ObservableIntegerValue divide(ObservableValue<Integer> other) {
        return observe(() -> get() / other.get(), this, other);
    }

    default ObservableBooleanValue eq(int value) {
        return mapToBoolean(i -> i == value);
    }

    default ObservableBooleanValue neq(int value) {
        return mapToBoolean(i -> i != value);
    }

    default ObservableBooleanValue gt(int value) {
        return mapToBoolean(i -> i > value);
    }

    default ObservableBooleanValue lt(int value) {
        return mapToBoolean(i -> i < value);
    }

    default ObservableBooleanValue gte(int value) {
        return mapToBoolean(i -> i >= value);
    }

    default ObservableBooleanValue lte(int value) {
        return mapToBoolean(i -> i <= value);
    }

    default ObservableBooleanValue eq(ObservableValue<Integer> value) {
        return ObservableBooleanValue.observe(() -> getValue() == value.get(), this, value);
    }

    default ObservableBooleanValue neq(ObservableValue<Integer> value) {
        return ObservableBooleanValue.observe(() -> getValue() != value.get(), this, value);
    }

    default ObservableBooleanValue gt(ObservableValue<Integer> value) {
        return ObservableBooleanValue.observe(() -> getValue() > value.get(), this, value);
    }

    default ObservableBooleanValue lt(ObservableValue<Integer> value) {
        return ObservableBooleanValue.observe(() -> getValue() < value.get(), this, value);
    }

    default ObservableBooleanValue gte(ObservableValue<Integer> value) {
        return ObservableBooleanValue.observe(() -> getValue() >= value.get(), this, value);
    }

    default ObservableBooleanValue lte(ObservableValue<Integer> value) {
        return ObservableBooleanValue.observe(() -> getValue() <= value.get(), this, value);
    }
}

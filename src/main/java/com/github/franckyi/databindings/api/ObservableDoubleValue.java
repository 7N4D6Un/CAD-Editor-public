package com.github.franckyi.databindings.api;

import java.util.function.DoubleSupplier;

public interface ObservableDoubleValue extends ObservableValue<Double> {
    static ObservableDoubleValue unmodifiable(double value) {
        return new Unmodifiable() {
            @Override
            public Double get() {
                return value;
            }
        };
    }

    abstract class Unmodifiable extends ObservableValue.Unmodifiable<Double> implements ObservableDoubleValue {
    }

    static ObservableDoubleValue readOnly(DoubleProperty property) {
        return DataBindings.getPropertyFactory().createReadOnlyProperty(property);
    }

    static ObservableDoubleValue observe(DoubleSupplier supplier, ObservableValue<?>... triggers) {
        return DataBindings.getMappingFactory().createDoubleMapping(supplier, triggers);
    }

    default double getValue() {
        return get() == null ? 0 : get();
    }

    default ObservableDoubleValue add(double other) {
        return mapToDouble(i -> i + other);
    }

    default ObservableDoubleValue substract(double other) {
        return mapToDouble(i -> i - other);
    }

    default ObservableDoubleValue multiply(double other) {
        return mapToDouble(i -> i * other);
    }

    default ObservableDoubleValue divide(double other) {
        return mapToDouble(i -> i / other);
    }

    default ObservableDoubleValue add(ObservableValue<Double> other) {
        return observe(() -> get() + other.get(), this, other);
    }

    default ObservableDoubleValue substract(ObservableValue<Double> other) {
        return observe(() -> get() - other.get(), this, other);
    }

    default ObservableDoubleValue multiply(ObservableValue<Double> other) {
        return observe(() -> get() * other.get(), this, other);
    }

    default ObservableDoubleValue divide(ObservableValue<Double> other) {
        return observe(() -> get() / other.get(), this, other);
    }
}

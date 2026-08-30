package com.github.franckyi.databindings.api;

import java.util.function.Supplier;

public interface ObservableStringValue extends ObservableObjectValue<String> {
    static ObservableStringValue unmodifiable(String value) {
        return new Unmodifiable() {
            @Override
            public String get() {
                return value;
            }
        };
    }

    abstract class Unmodifiable extends ObservableValue.Unmodifiable<String> implements ObservableStringValue {
    }

    static ObservableStringValue readOnly(StringProperty property) {
        return DataBindings.getPropertyFactory().createReadOnlyProperty(property);
    }

    static ObservableStringValue observe(Supplier<String> supplier, ObservableValue<?>... triggers) {
        return DataBindings.getMappingFactory().createStringMapping(supplier, triggers);
    }

    default ObservableStringValue append(String other) {
        return mapToString(s -> s + other, other);
    }

    default ObservableStringValue prepend(String other) {
        return mapToString(s -> other + s, other);
    }

    default ObservableStringValue append(ObservableValue<String> other) {
        return observe(() -> get() + other.get(), this, other);
    }

    default ObservableStringValue prepend(ObservableValue<String> other) {
        return observe(() -> other.get() + get(), this, other);
    }
}

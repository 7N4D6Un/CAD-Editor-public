package com.github.franckyi.databindings.api;

public interface StringProperty extends ObjectProperty<String>, ObservableStringValue {
    static StringProperty create() {
        return DataBindings.getPropertyFactory().createStringProperty();
    }

    static StringProperty create(String value) {
        return DataBindings.getPropertyFactory().createStringProperty(value);
    }
}

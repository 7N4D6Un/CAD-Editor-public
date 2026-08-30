package com.github.franckyi.databindings.api;

public interface BooleanProperty extends Property<Boolean>, ObservableBooleanValue {
    static BooleanProperty create() {
        return DataBindings.getPropertyFactory().createBooleanProperty();
    }

    static BooleanProperty create(boolean value) {
        return DataBindings.getPropertyFactory().createBooleanProperty(value);
    }

    default void setValue(boolean value) {
        set(value);
    }

    default void toggle() {
        setValue(!getValue());
    }
}

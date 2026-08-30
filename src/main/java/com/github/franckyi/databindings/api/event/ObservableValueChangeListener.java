package com.github.franckyi.databindings.api.event;

import com.github.franckyi.databindings.api.ObservableValue;

@FunctionalInterface
public interface ObservableValueChangeListener<T> {
    void onValueChange(T oldVal, T newVal);
}

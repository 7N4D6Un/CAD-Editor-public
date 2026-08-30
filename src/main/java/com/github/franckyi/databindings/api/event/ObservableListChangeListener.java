package com.github.franckyi.databindings.api.event;

import com.github.franckyi.databindings.api.ObservableList;

@FunctionalInterface
public interface ObservableListChangeListener<E> {
    void onListChange(ObservableListChangeEvent<? extends E> event);
}

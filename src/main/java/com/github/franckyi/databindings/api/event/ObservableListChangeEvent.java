package com.github.franckyi.databindings.api.event;

import com.github.franckyi.databindings.api.ObservableList;

import java.util.List;

public interface ObservableListChangeEvent<E> {
    List<ChangeEntry<E>> getAllChanged();

    List<SimpleChangeEntry<E>> getAdded(boolean andReplaced);

    List<SimpleChangeEntry<E>> getRemoved(boolean andReplaced);

    List<ChangeEntry<E>> getReplaced();

    interface ChangeEntry<E> {
        int getIndex();

        E getOldValue();

        E getNewValue();

        default boolean wasAdded() {
            return getOldValue() == null && getNewValue() != null;
        }

        default boolean wasAddedOrReplaced() {
            return wasAdded() || wasReplaced();
        }

        default boolean wasRemoved() {
            return getOldValue() != null && getNewValue() == null;
        }

        default boolean wasRemovedOrReplaced() {
            return wasRemoved() || wasReplaced();
        }

        default boolean wasReplaced() {
            return getOldValue() != null && getNewValue() != null;
        }
    }

    interface SimpleChangeEntry<E> {
        int getIndex();

        E getValue();
    }
}

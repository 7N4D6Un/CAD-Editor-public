package com.github.franckyi.guapi.api.node;

import com.github.franckyi.databindings.api.DoubleProperty;
import com.github.franckyi.databindings.api.ObjectProperty;
import net.minecraft.world.entity.Entity;

public interface EntityView extends Control {
    default Entity getEntity() {
        return entityProperty().getValue();
    }

    ObjectProperty<Entity> entityProperty();

    default void setEntity(Entity value) {
        entityProperty().setValue(value);
    }

    default double getSize() {
        return sizeProperty().getValue();
    }

    DoubleProperty sizeProperty();

    default void setSize(double value) {
        sizeProperty().setValue(value);
    }

    default double getOrbitYaw() {
        return orbitYawProperty().getValue();
    }

    DoubleProperty orbitYawProperty();

    default void setOrbitYaw(double value) {
        orbitYawProperty().setValue(value);
    }

    default double getOrbitPitch() {
        return orbitPitchProperty().getValue();
    }

    DoubleProperty orbitPitchProperty();

    default void setOrbitPitch(double value) {
        orbitPitchProperty().setValue(value);
    }
}

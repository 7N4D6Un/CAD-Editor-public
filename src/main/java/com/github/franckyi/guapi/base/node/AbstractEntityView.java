package com.github.franckyi.guapi.base.node;

import com.github.franckyi.databindings.api.DoubleProperty;
import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.franckyi.guapi.api.node.EntityView;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("this-escape")
public abstract class AbstractEntityView extends AbstractControl implements EntityView {
    private final ObjectProperty<Entity> entityProperty = ObjectProperty.create();
    private final DoubleProperty sizeProperty = DoubleProperty.create(50);
    private final DoubleProperty orbitYawProperty = DoubleProperty.create(-45.0);
    private final DoubleProperty orbitPitchProperty = DoubleProperty.create(17.0);

    protected AbstractEntityView() {
    }

    protected AbstractEntityView(Entity entity) {
        setEntity(entity);
    }

    @Override
    public ObjectProperty<Entity> entityProperty() {
        return entityProperty;
    }

    @Override
    public DoubleProperty sizeProperty() {
        return sizeProperty;
    }

    @Override
    public DoubleProperty orbitYawProperty() {
        return orbitYawProperty;
    }

    @Override
    public DoubleProperty orbitPitchProperty() {
        return orbitPitchProperty;
    }
}

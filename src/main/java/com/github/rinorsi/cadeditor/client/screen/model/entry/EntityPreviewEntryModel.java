package com.github.rinorsi.cadeditor.client.screen.model.entry;

import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import net.minecraft.world.entity.Entity;

public class EntityPreviewEntryModel extends EntryModel {
    private final ObjectProperty<Entity> entityProperty = ObjectProperty.create();

    public EntityPreviewEntryModel(CategoryModel category) {
        super(category);
        setReorderable(false);
    }

    public Entity getEntity() {
        return entityProperty.getValue();
    }

    public ObjectProperty<Entity> entityProperty() {
        return entityProperty;
    }

    public void setEntity(Entity value) {
        entityProperty.setValue(value);
    }

    @Override
    public void apply() {
    }

    @Override
    public boolean isDeletable() {
        return false;
    }

    @Override
    public boolean isResetable() {
        return false;
    }

    @Override
    public Type getType() {
        return Type.ENTITY_PREVIEW;
    }
}

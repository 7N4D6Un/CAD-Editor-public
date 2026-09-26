package com.github.franckyi.guapi.base.node;

import com.github.franckyi.guapi.api.node.EntityView;
import com.github.franckyi.guapi.api.node.builder.EntityViewBuilder;
import net.minecraft.world.entity.Entity;

public class EntityViewImpl extends AbstractEntityView implements EntityViewBuilder {
    public EntityViewImpl() {
    }

    public EntityViewImpl(Entity entity) {
        super(entity);
    }

    @Override
    protected Class<?> getType() {
        return EntityView.class;
    }
}

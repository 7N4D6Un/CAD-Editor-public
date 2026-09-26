package com.github.franckyi.guapi.api.node.builder.generic;

import com.github.franckyi.guapi.api.node.EntityView;
import net.minecraft.world.entity.Entity;

public interface GenericEntityViewBuilder<N extends EntityView> extends EntityView, GenericControlBuilder<N> {
    default N entity(Entity value) {
        return with(n -> n.setEntity(value));
    }

    default N size(double value) {
        return with(n -> n.setSize(value));
    }
}

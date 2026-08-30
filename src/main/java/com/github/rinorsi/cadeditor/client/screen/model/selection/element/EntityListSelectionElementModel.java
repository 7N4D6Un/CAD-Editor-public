package com.github.rinorsi.cadeditor.client.screen.model.selection.element;

import net.minecraft.network.chat.Component;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Locale;

public class EntityListSelectionElementModel extends ItemListSelectionElementModel {
    private final Component displayName;
    private final String displayNameLowercase;

    public EntityListSelectionElementModel(EntityType<?> entityType, Identifier id) {
        super(entityType.getDescriptionId(), id, () -> buildIcon(entityType));
        this.displayName = entityType.getDescription().copy();
        this.displayNameLowercase = displayName.getString().toLowerCase(Locale.ROOT);
    }

    @Override
    public Component getDisplayName() {
        return displayName;
    }

    @Override
    public boolean matches(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        String lower = s.toLowerCase(Locale.ROOT);
        if (displayNameLowercase.contains(lower)) {
            return true;
        }
        return super.matches(s);
    }

    private static ItemStack buildIcon(EntityType<?> type) {
        return SpawnEggItem.byId(type).map(Holder::value).map(ItemStack::new).orElse(ItemStack.EMPTY);
    }
}

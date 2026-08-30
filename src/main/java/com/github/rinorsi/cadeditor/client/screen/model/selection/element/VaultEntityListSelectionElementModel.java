package com.github.rinorsi.cadeditor.client.screen.model.selection.element;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;


public class VaultEntityListSelectionElementModel extends ItemListSelectionElementModel {
    private final Component displayName;
    private final String searchLabel;

    public VaultEntityListSelectionElementModel(Identifier id, CompoundTag tag) {
        super(buildName(tag), id, (Supplier<ItemStack>) () -> {
            return buildIcon(tag);
        });
        this.searchLabel = buildName(tag);
        this.displayName = Component.literal(this.searchLabel);
    }

    private static String buildName(CompoundTag tag) {
        String id = tag.getStringOr("id", "");
        if (id.isEmpty()) {
            id = "minecraft:unknown";
        }
        String prettyName = (String) BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(id)).map(type -> type.getDescription().getString()).orElse(id);
        return String.format(Locale.ROOT, "%s (%s)", prettyName, id);
    }

    @Override 
    
    public Component getDisplayName() {
        return this.displayName;
    }

    @Override 
    public boolean matches(String s) {
        return s == null || s.isEmpty() || this.searchLabel.toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT)) || super.matches(s);
    }

    
    public static ItemStack buildIcon(CompoundTag tag) {
        String id = tag.getStringOr("id", "");
        if (!id.isEmpty()) {
            return (ItemStack) BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(id)).flatMap(SpawnEggItem::byId).map(ItemStack::new).orElse(new ItemStack(Items.SPAWNER));
        }
        return new ItemStack(Items.SPAWNER);
    }
}

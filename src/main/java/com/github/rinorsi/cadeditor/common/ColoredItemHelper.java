package com.github.rinorsi.cadeditor.common;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.FireworkExplosion;


public final class ColoredItemHelper {
    private ColoredItemHelper() {
    }

    public static ItemStack createColoredPotionItem(Identifier potionId, int color) {
        PotionContents contents;
        ItemStack stack = new ItemStack(Items.POTION);
        Optional<? extends HolderLookup.RegistryLookup<Potion>> lookupOpt = registryAccess().lookup(Registries.POTION);
        if (lookupOpt.isPresent()) {
            HolderLookup.RegistryLookup<Potion> lookup = lookupOpt.get();
            Identifier rl = potionId == null ? Identifier.parse("minecraft:empty") : potionId;
            ResourceKey<Potion> key = ResourceKey.create(Registries.POTION, rl);
            Holder<Potion> holder = (Holder) lookup.get(key).orElse(null);
            List<MobEffectInstance> effects = List.of();
            if (holder != null) {
                contents = new PotionContents(Optional.of(holder), color != Integer.MIN_VALUE ? Optional.of(color) : Optional.empty(), effects, Optional.empty());
            } else {
                contents = new PotionContents(Optional.empty(), color != Integer.MIN_VALUE ? Optional.of(color) : Optional.empty(), effects, Optional.empty());
            }
            stack.set(DataComponents.POTION_CONTENTS, contents);
            return stack;
        }
        CompoundTag data = new CompoundTag();
        CompoundTag tag = new CompoundTag();
        tag.putString("Potion", potionId == null ? "minecraft:empty" : potionId.toString());
        if (color != Integer.MIN_VALUE) {
            tag.putInt("CustomPotionColor", color);
        }
        data.putString("id", "minecraft:potion");
        data.putInt("count", 1);
        data.put("tag", tag);
        return ClientUtil.parseItemStack(data);
    }

    public static ItemStack createColoredArmorItem(ItemStack armorItem, int color) {
        ItemStack copy = armorItem.copy();
        if (color == Integer.MIN_VALUE) {
            copy.remove(DataComponents.DYED_COLOR);
        } else {
            copy.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
        }
        return copy;
    }

    public static ItemStack createFireworkStarItem(int color) {
        ItemStack stack = new ItemStack(Items.FIREWORK_STAR);
        if (color == Integer.MIN_VALUE) {
            stack.remove(DataComponents.FIREWORK_EXPLOSION);
            return stack;
        }
        IntArrayList colors = new IntArrayList();
        colors.add(color);
        FireworkExplosion explosion = new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, colors, new IntArrayList(), false, false);
        stack.set(DataComponents.FIREWORK_EXPLOSION, explosion);
        return stack;
    }

    private static HolderLookup.Provider registryAccess() {
        return ClientUtil.registryAccess();
    }
}

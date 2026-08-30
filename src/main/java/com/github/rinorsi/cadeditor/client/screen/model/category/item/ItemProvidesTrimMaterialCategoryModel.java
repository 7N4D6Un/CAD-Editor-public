package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.TrimMaterialSelectionEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class ItemProvidesTrimMaterialCategoryModel extends ItemEditorCategoryModel {
    public ItemProvidesTrimMaterialCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("provides_trim_material"), editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        getEntries().add(new TrimMaterialSelectionEntryModel(this, getProvidesTrimMaterialId(stack), this::setProvidesTrimMaterialId));
    }

    private String getProvidesTrimMaterialId(ItemStack stack) {
        Holder<TrimMaterial> material = stack.get(DataComponents.PROVIDES_TRIM_MATERIAL);
        return material != null && material.unwrapKey().isPresent() ? material.unwrapKey().get().identifier().toString() : "";
    }

    private void setProvidesTrimMaterialId(String value) {
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        String normalized = normalizeResourceId(value);
        if (normalized.isEmpty()) {
            stack.remove(DataComponents.PROVIDES_TRIM_MATERIAL);
            return;
        }
        Holder.Reference<TrimMaterial> holder = resolveHolder(normalized);
        if (holder != null) {
            stack.set(DataComponents.PROVIDES_TRIM_MATERIAL, holder);
        } else {
            stack.remove(DataComponents.PROVIDES_TRIM_MATERIAL);
        }
    }

    private String normalizeResourceId(String raw) {
        Identifier location;
        String trimmed = raw == null ? "" : raw.trim();
        return (trimmed.isEmpty() || (location = ClientUtil.parseResourceLocation(trimmed)) == null) ? "" : location.toString();
    }

    private Holder.Reference<TrimMaterial> resolveHolder(String id) {
        Identifier rl = Identifier.tryParse(id);
        if (rl == null) {
            return null;
        }
        Optional<? extends HolderLookup.RegistryLookup<TrimMaterial>> lookup = ClientUtil.registryAccess().lookup(Registries.TRIM_MATERIAL);
        return lookup.flatMap(l -> l.get(ResourceKey.create(Registries.TRIM_MATERIAL, rl))).orElse(null);
    }
}
package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.BannerPatternSelectionEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;

public class ItemProvidesBannerPatternCategoryModel extends ItemEditorCategoryModel {
    public ItemProvidesBannerPatternCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("provides_banner_patterns"), editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        getEntries().add(new BannerPatternSelectionEntryModel(this, getProvidesBannerPatternId(stack), this::setProvidesBannerPatternId));
    }

    private String getProvidesBannerPatternId(ItemStack stack) {
        HolderSet<BannerPattern> patterns = stack.get(DataComponents.PROVIDES_BANNER_PATTERNS);
        if (patterns == null) {
            return "";
        }
        return patterns.stream().findFirst().flatMap(h -> h.unwrapKey()).map(k -> k.identifier().toString()).orElse("");
    }

    private void setProvidesBannerPatternId(String value) {
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        String normalized = normalizeResourceId(value);
        if (normalized.isEmpty()) {
            stack.remove(DataComponents.PROVIDES_BANNER_PATTERNS);
            return;
        }
        Holder.Reference<BannerPattern> holder = resolveHolder(normalized);
        if (holder != null) {
            stack.set(DataComponents.PROVIDES_BANNER_PATTERNS, HolderSet.direct(holder));
        } else {
            stack.remove(DataComponents.PROVIDES_BANNER_PATTERNS);
        }
    }

    private String normalizeResourceId(String raw) {
        Identifier location;
        String trimmed = raw == null ? "" : raw.trim();
        return (trimmed.isEmpty() || (location = ClientUtil.parseResourceLocation(trimmed)) == null) ? "" : location.toString();
    }

    private Holder.Reference<BannerPattern> resolveHolder(String id) {
        Identifier rl = Identifier.tryParse(id);
        if (rl == null) {
            return null;
        }
        Optional<? extends HolderLookup.RegistryLookup<BannerPattern>> lookup = ClientUtil.registryAccess().lookup(Registries.BANNER_PATTERN);
        return lookup.flatMap(l -> l.get(ResourceKey.create(Registries.BANNER_PATTERN, rl))).orElse(null);
    }
}
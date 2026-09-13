
package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.BannerLayerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.DyeColorSelectionEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ItemBannerPatternCategoryModel extends ItemEditorCategoryModel {
    private String baseColorName = "";
    private boolean hasBaseColorEntry;
    private final List<BannerLayerEntryModel> layerEntries = new ArrayList<>();

    public ItemBannerPatternCategoryModel(ItemEditorModel editor) {
        super(ModTexts.BANNER, editor);
    }

    @Override
    protected void setupEntries() {
        layerEntries.clear();
        ItemStack stack = getParent().getContext().getItemStack();
        this.hasBaseColorEntry = stack.getItem() instanceof ShieldItem;
        if (this.hasBaseColorEntry) {
            DyeColor base = stack.get(DataComponents.BASE_COLOR);
            baseColorName = base != null ? base.getName() : "";
            getEntries().add(new DyeColorSelectionEntryModel(this, ModTexts.BANNER_BASE_COLOR, baseColorName,
                    value -> baseColorName = value == null ? "" : value.trim()));
        }
        BannerPatternLayers layers = stack.get(DataComponents.BANNER_PATTERNS);
        if (layers != null) {
            layers.layers().forEach(layer -> getEntries().add(createLayerEntry(formatLayer(layer))));
        }
    }

    @Override
    public int getEntryListStart() {
        return this.hasBaseColorEntry ? 1 : 0;
    }

    @Override
    public EntryModel createNewListEntry() {
        return createLayerEntry("");
    }

    private EntryModel createLayerEntry(String spec) {
        BannerLayerEntryModel entry = new BannerLayerEntryModel(this, ModTexts.BANNER_LAYER, spec);
        entry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_banner_pattern"), () -> openPatternSelection(entry)));
        entry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.COLOR_CUSTOM, ModTexts.gui("select_banner_color"), () -> openLayerColorSelection(entry)));
        layerEntries.add(entry);
        return entry;
    }

    private void openPatternSelection(StringWithActionsEntryModel entry) {
        String current = entry.getValue() == null ? "" : entry.getValue().trim();
        String patternPart = current.contains("|") ? current.substring(0, current.indexOf('|')).trim() : current;
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_banner_pattern"), patternPart,
                ClientCache.getBannerPatternSelectionItems(), chosen -> entry.setValue(mergeLayer(entry.getValue(), chosen, null)));
    }

    private void openLayerColorSelection(StringWithActionsEntryModel entry) {
        String current = entry.getValue() == null ? "" : entry.getValue().trim();
        String colorPart = current.contains("|") ? current.substring(current.indexOf('|') + 1).trim() : "";
        if (!colorPart.isEmpty() && !colorPart.contains(":")) {
            colorPart = "minecraft:" + colorPart;
        }
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_banner_color"), colorPart,
                DyeColorSelectionEntryModel.getDyeColorSelectionItems(), chosen -> entry.setValue(mergeLayer(entry.getValue(), null, chosen)));
    }

    private static String mergeLayer(String current, String pattern, String color) {
        String existing = current == null ? "" : current.trim();
        String patternPart = pattern != null ? pattern.trim()
                : (existing.contains("|") ? existing.substring(0, existing.indexOf('|')).trim() : existing);
        String colorPart = color != null ? stripNamespace(color)
                : (existing.contains("|") ? existing.substring(existing.indexOf('|') + 1).trim() : "");
        if (colorPart.contains("|")) {
            colorPart = colorPart.substring(0, colorPart.indexOf('|')).trim();
        }
        return patternPart + "|" + (colorPart.isEmpty() ? "white" : colorPart);
    }

    private static String stripNamespace(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.contains(":") ? trimmed.substring(trimmed.lastIndexOf(':') + 1).trim() : trimmed;
    }

    @Override
    public void apply() {
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        if (this.hasBaseColorEntry) {
            DyeColor base = DyeColor.byName(baseColorName, null);
            if (base != null) {
                stack.set(DataComponents.BASE_COLOR, base);
            } else {
                stack.remove(DataComponents.BASE_COLOR);
            }
        }

        HolderLookup.RegistryLookup<BannerPattern> patternLookup = ClientUtil.registryAccess()
                .lookup(Registries.BANNER_PATTERN)
                .orElse(null);
        List<BannerPatternLayers.Layer> parsedLayers = new ArrayList<>();
        boolean hasInvalid = false;
        for (BannerLayerEntryModel entry : layerEntries) {
            String spec = Optional.ofNullable(entry.getValue()).orElse("").trim();
            if (spec.isBlank()) {
                entry.setValid(true);
                continue;
            }
            Optional<BannerPatternLayers.Layer> parsed = parseLayer(spec, patternLookup);
            if (parsed.isPresent()) {
                parsedLayers.add(parsed.get());
                entry.setValid(true);
            } else {
                entry.setValid(false);
                hasInvalid = true;
            }
        }
        if (hasInvalid) {
            return;
        }
        if (parsedLayers.isEmpty()) {
            stack.remove(DataComponents.BANNER_PATTERNS);
        } else {
            stack.set(DataComponents.BANNER_PATTERNS, new BannerPatternLayers(parsedLayers));
        }
        CompoundTag data = getData();
        if (data != null && data.contains("components")) {
            CompoundTag components = data.getCompound("components").orElse(null);
            if (components != null) {
                components.remove("minecraft:banner_patterns");
                components.remove("minecraft:base_color");
                if (components.isEmpty()) {
                    data.remove("components");
                }
            }
        }
    }

    private String formatLayer(BannerPatternLayers.Layer layer) {
        String patternId = layer.pattern().unwrapKey()
                .map(ResourceKey::identifier)
                .map(Identifier::toString)
                .orElse("");
        return patternId + "|" + layer.color().getName();
    }

    public static boolean isValidLayerSpec(String spec) {
        HolderLookup.RegistryLookup<BannerPattern> lookup = ClientUtil.registryAccess()
                .lookup(Registries.BANNER_PATTERN)
                .orElse(null);
        return parseLayer(spec, lookup).isPresent();
    }

    private static Optional<BannerPatternLayers.Layer> parseLayer(String spec, HolderLookup.RegistryLookup<BannerPattern> lookup) {
        String[] parts = spec.split("\\|", -1);
        if (parts.length < 2) {
            return Optional.empty();
        }
        Identifier patternId = Identifier.tryParse(parts[0].trim());
        if (patternId == null || lookup == null) {
            return Optional.empty();
        }
        Optional<Holder.Reference<BannerPattern>> pattern = lookup.get(ResourceKey.create(Registries.BANNER_PATTERN, patternId));
        if (pattern.isEmpty()) {
            return Optional.empty();
        }
        DyeColor color = DyeColor.byName(parts[1].trim().toLowerCase(Locale.ROOT), null);
        if (color == null) {
            return Optional.empty();
        }
        return Optional.of(new BannerPatternLayers.Layer(pattern.get(), color));
    }
}

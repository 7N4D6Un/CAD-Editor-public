package com.github.rinorsi.cadeditor.client;

import com.github.franckyi.guapi.api.Color;
import com.github.rinorsi.cadeditor.client.screen.model.selection.ListSelectionFilter;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.EnchantmentListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.EntityListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.EquipmentAssetListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ItemListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SelectableItemListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SelectableSpriteListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SelectableTagListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SortedEnchantmentListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SoundEventListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.TagListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.TrimMaterialSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.BannerPatternSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.TrimPatternSelectionElementModel;
import com.github.rinorsi.cadeditor.common.ColoredItemHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.github.rinorsi.cadeditor.common.loot.LootTableIndex;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public final class ClientCache {
    private static List<String> itemSuggestions;
    private static List<ItemListSelectionElementModel> itemSelectionItems;
    private static List<String> blockSuggestions;
    private static List<ItemListSelectionElementModel> blockSelectionItems;
    private static List<TagListSelectionElementModel> blockTagSelectionItems;
    private static List<TagListSelectionElementModel> itemTagSelectionItems;
    private static List<TagListSelectionElementModel> damageTypeTagSelectionItems;
    private static List<ListSelectionElementModel> damageTypeSelectionItems;
    private static List<TagListSelectionElementModel> effectTagSelectionItems;
    private static List<TagListSelectionElementModel> entityTypeTagSelectionItems;
    private static List<String> damageTypeSuggestions;
    private static List<String> enchantmentSuggestions;
    private static List<EnchantmentListSelectionElementModel> enchantmentSelectionItems;
    private static List<String> attributeSuggestions;
    private static List<ListSelectionElementModel> attributeSelectionItems;
    private static List<String> potionSuggestions;
    private static List<ItemListSelectionElementModel> potionSelectionItems;
    private static List<String> effectSuggestions;
    private static List<SelectableSpriteListSelectionElementModel> effectSelectionItems;
    private static List<String> entitySuggestions;
    private static List<EntityListSelectionElementModel> entitySelectionItems;
    private static List<String> villagerProfessionSuggestions;
    private static List<ListSelectionElementModel> villagerProfessionSelectionItems;
    private static List<String> villagerTypeSuggestions;
    private static List<ListSelectionElementModel> villagerTypeSelectionItems;
    private static List<String> trimPatternSuggestions;
    private static List<TrimPatternSelectionElementModel> trimPatternSelectionItems;
    private static List<String> trimMaterialSuggestions;
    private static List<TrimMaterialSelectionElementModel> trimMaterialSelectionItems;
    private static List<String> bannerPatternSuggestions;
    private static List<BannerPatternSelectionElementModel> bannerPatternSelectionItems;
    private static List<String> instrumentSuggestions;
    private static List<ListSelectionElementModel> instrumentSelectionItems;
    private static List<String> jukeboxSongSuggestions;
    private static List<ListSelectionElementModel> jukeboxSongSelectionItems;
    private static List<String> soundEventSuggestions;
    private static List<SoundEventListSelectionElementModel> soundEventSelectionItems;
    private static List<ListSelectionFilter> soundEventFilters;
    private static List<String> equipmentAssetSuggestions;
    private static List<ListSelectionElementModel> equipmentAssetSelectionItems;
    private static List<String> blockEntityTypeSuggestions;
    private static List<String> lootTableSuggestions;
    private static List<String> componentTypeIds;
    private static final Identifier GUI_ATLAS_LOCATION = Identifier.fromNamespaceAndPath("minecraft", "textures/atlas/gui.png");
    private static final List<Identifier> BUILTIN_EQUIPMENT_ASSETS = buildBuiltinEquipmentAssets();

    public static void invalidate() {
        itemSuggestions = null;
        itemSelectionItems = null;
        blockSuggestions = null;
        blockSelectionItems = null;
        blockTagSelectionItems = null;
        itemTagSelectionItems = null;
        damageTypeTagSelectionItems = null;
        damageTypeSelectionItems = null;
        damageTypeSuggestions = null;
        enchantmentSuggestions = null;
        enchantmentSelectionItems = null;
        attributeSuggestions = null;
        attributeSelectionItems = null;
        potionSuggestions = null;
        potionSelectionItems = null;
        effectSuggestions = null;
        effectSelectionItems = null;
        effectTagSelectionItems = null;
        entityTypeTagSelectionItems = null;
        entitySuggestions = null;
        entitySelectionItems = null;
        villagerProfessionSuggestions = null;
        villagerProfessionSelectionItems = null;
        villagerTypeSuggestions = null;
        villagerTypeSelectionItems = null;
        trimPatternSuggestions = null;
        trimPatternSelectionItems = null;
        trimMaterialSuggestions = null;
        trimMaterialSelectionItems = null;
        bannerPatternSuggestions = null;
        bannerPatternSelectionItems = null;
        instrumentSuggestions = null;
        instrumentSelectionItems = null;
        jukeboxSongSuggestions = null;
        jukeboxSongSelectionItems = null;
        soundEventSuggestions = null;
        soundEventSelectionItems = null;
        soundEventFilters = null;
        equipmentAssetSuggestions = null;
        equipmentAssetSelectionItems = null;
        blockEntityTypeSuggestions = null;
        lootTableSuggestions = null;
        componentTypeIds = null;
    }

    public static List<String> getItemSuggestions() {
        if (itemSuggestions != null) {
            return itemSuggestions;
        }
        List<String> listBuildSuggestions = buildSuggestions((Registry<?>) BuiltInRegistries.ITEM);
        itemSuggestions = listBuildSuggestions;
        return listBuildSuggestions;
    }

    public static List<ItemListSelectionElementModel> getItemSelectionItems() {
        if (itemSelectionItems != null) {
            return itemSelectionItems;
        }
        List<ItemListSelectionElementModel> listBuildItemSelectionItems = buildItemSelectionItems();
        itemSelectionItems = listBuildItemSelectionItems;
        return listBuildItemSelectionItems;
    }

    public static List<String> getBlockSuggestions() {
        if (blockSuggestions != null) {
            return blockSuggestions;
        }
        List<String> listBuildSuggestions = buildSuggestions((Registry<?>) BuiltInRegistries.BLOCK);
        blockSuggestions = listBuildSuggestions;
        return listBuildSuggestions;
    }

    public static List<ItemListSelectionElementModel> getBlockSelectionItems() {
        if (blockSelectionItems != null) {
            return blockSelectionItems;
        }
        List<ItemListSelectionElementModel> listBuildBlockSelectionItems = buildBlockSelectionItems();
        blockSelectionItems = listBuildBlockSelectionItems;
        return listBuildBlockSelectionItems;
    }

    public static List<TagListSelectionElementModel> getBlockTagSelectionItems() {
        if (blockTagSelectionItems != null) {
            return blockTagSelectionItems;
        }
        List<TagListSelectionElementModel> listBuildBlockTagSelectionItems = buildBlockTagSelectionItems();
        blockTagSelectionItems = listBuildBlockTagSelectionItems;
        return listBuildBlockTagSelectionItems;
    }

    public static List<TagListSelectionElementModel> getItemTagSelectionItems() {
        if (itemTagSelectionItems != null) {
            return itemTagSelectionItems;
        }
        List<TagListSelectionElementModel> listBuildItemTagSelectionItems = buildItemTagSelectionItems();
        itemTagSelectionItems = listBuildItemTagSelectionItems;
        return listBuildItemTagSelectionItems;
    }

    public static List<TagListSelectionElementModel> getDamageTypeTagSelectionItems() {
        if (damageTypeTagSelectionItems != null) {
            return damageTypeTagSelectionItems;
        }
        List<TagListSelectionElementModel> listBuildDamageTypeTagSelectionItems = buildDamageTypeTagSelectionItems();
        damageTypeTagSelectionItems = listBuildDamageTypeTagSelectionItems;
        return listBuildDamageTypeTagSelectionItems;
    }

    public static List<TagListSelectionElementModel> getEffectTagSelectionItems() {
        if (effectTagSelectionItems != null) {
            return effectTagSelectionItems;
        }
        List<TagListSelectionElementModel> listBuildEffectTagSelectionItems = buildEffectTagSelectionItems();
        effectTagSelectionItems = listBuildEffectTagSelectionItems;
        return listBuildEffectTagSelectionItems;
    }

    public static List<TagListSelectionElementModel> getEntityTypeTagSelectionItems() {
        if (entityTypeTagSelectionItems != null) {
            return entityTypeTagSelectionItems;
        }
        List<TagListSelectionElementModel> listBuildEntityTypeTagSelectionItems = buildEntityTypeTagSelectionItems();
        entityTypeTagSelectionItems = listBuildEntityTypeTagSelectionItems;
        return listBuildEntityTypeTagSelectionItems;
    }

    public static List<ListSelectionElementModel> getDamageTypeSelectionItems() {
        if (damageTypeSelectionItems != null) {
            return damageTypeSelectionItems;
        }
        List<ListSelectionElementModel> listBuildDamageTypeSelectionItems = buildDamageTypeSelectionItems();
        damageTypeSelectionItems = listBuildDamageTypeSelectionItems;
        return listBuildDamageTypeSelectionItems;
    }

    public static List<String> getDamageTypeSuggestions() {
        if (damageTypeSuggestions == null) {
            damageTypeSuggestions = getDamageTypeSelectionItems().stream().map(element -> element.getId().toString()).toList();
        }
        return damageTypeSuggestions;
    }

    public static List<String> getBlockEntityTypeSuggestions() {
        if (blockEntityTypeSuggestions == null) {
            List<String> listBuildSuggestions = buildSuggestions((Registry<?>) BuiltInRegistries.BLOCK_ENTITY_TYPE);
            blockEntityTypeSuggestions = listBuildSuggestions;
            return listBuildSuggestions;
        }
        return blockEntityTypeSuggestions;
    }

    public static List<String> getEnchantmentSuggestions() {
        if (enchantmentSuggestions == null) {
            enchantmentSuggestions = (List) registryAccess().lookup(Registries.ENCHANTMENT).map(ClientCache::buildSuggestions).orElseGet(List::of);
        }
        return enchantmentSuggestions;
    }

    public static List<SortedEnchantmentListSelectionElementModel> getSortedEnchantmentSelectionItems(ItemStack target) {
        if (enchantmentSelectionItems == null) {
            enchantmentSelectionItems = buildEnchantmentSelectionItems();
        }
        return enchantmentSelectionItems.stream().map(item -> {
            boolean curse = item.getEnchantment().is(EnchantmentTags.CURSE);
            boolean canApply = enchantmentCanApply(item.getEnchantment(), target);
            return new SortedEnchantmentListSelectionElementModel(item, curse, canApply);
        }).sorted(Comparator.comparing((SortedEnchantmentListSelectionElementModel m) -> m.canApply() ? 0 : 1).thenComparing(m -> m.isCurse() ? 1 : 0).thenComparing(m -> m.getName().toLowerCase(Locale.ROOT))).toList();
    }

    public static Optional<EnchantmentListSelectionElementModel> findEnchantmentSelectionItem(Identifier id) {
        if (id == null) {
            return Optional.empty();
        }
        if (enchantmentSelectionItems == null) {
            enchantmentSelectionItems = buildEnchantmentSelectionItems();
        }
        return enchantmentSelectionItems.stream().filter(item -> item.getId().equals(id)).findFirst();
    }

    private static boolean enchantmentCanApply(Holder<Enchantment> enchantment, ItemStack target) {
        if (target == null || target.isEmpty()) {
            return false;
        }
        if (target.is(Items.ENCHANTED_BOOK) || target.is(Items.BOOK)) {
            return true;
        }
        Enchantment value = (Enchantment) enchantment.value();
        if (value.canEnchant(target)) {
            return true;
        }
        Item item = target.getItem();
        Enchantment.EnchantmentDefinition definition = value.definition();
        Optional<? extends HolderLookup.RegistryLookup<Item>> registry = registryAccess().lookup(Registries.ITEM);
        if (definition.primaryItems().map(set -> holderSetContainsItem(set, item, registry)).orElse(false)) {
            return true;
        }
        return holderSetContainsItem(definition.supportedItems(), item, registry);
    }

    public static List<String> getComponentTypeIds() {
        if (componentTypeIds == null) {
            List<String> ids = new ArrayList<>(BuiltInRegistries.DATA_COMPONENT_TYPE.keySet().size());
            for (Identifier id : BuiltInRegistries.DATA_COMPONENT_TYPE.keySet()) {
                ids.add(id.toString());
            }
            ids.sort(String::compareTo);
            componentTypeIds = List.copyOf(ids);
        }
        return componentTypeIds;
    }

    public static boolean isComponentIdKnown(String value) {
        Identifier id;
        if (value == null || value.isBlank() || (id = Identifier.tryParse(value)) == null) {
            return false;
        }
        return BuiltInRegistries.DATA_COMPONENT_TYPE.containsKey(id);
    }

    private static boolean holderSetContainsItem(HolderSet<Item> holders, Item item, Optional<? extends HolderLookup.RegistryLookup<Item>> registry) {
        if (holders == null) {
            return false;
        }
        try {
            if (holders.contains(BuiltInRegistries.ITEM.wrapAsHolder(item))) {
                return true;
            }
        } catch (IllegalStateException e) {
        }
        ResourceKey<Item> key = (ResourceKey) BuiltInRegistries.ITEM.getResourceKey(item).orElse(null);
        if (key == null && registry.isPresent()) {
            key = (ResourceKey) registry.get().listElements().filter(reference -> {
                try {
                    return reference.value() == item;
                } catch (IllegalStateException e) {
                    return false;
                }
            }).map(value -> value.key()).findFirst().orElse(null);
        }
        for (Holder<Item> holder : holders) {
            if (key != null && holder.is(key)) {
                return true;
            }
            try {
                if (holder.value() == item) {
                    return true;
                }
            } catch (IllegalStateException ignored) {
            }
        }
        return false;
    }

    public static List<String> getAttributeSuggestions() {
        if (attributeSuggestions != null) {
            return attributeSuggestions;
        }
        List<String> listBuildSuggestions = buildSuggestions((Registry<?>) BuiltInRegistries.ATTRIBUTE);
        attributeSuggestions = listBuildSuggestions;
        return listBuildSuggestions;
    }

    public static List<ListSelectionElementModel> getAttributeSelectionItems() {
        if (attributeSelectionItems != null) {
            return attributeSelectionItems;
        }
        List<ListSelectionElementModel> listBuildAttributeSelectionItems = buildAttributeSelectionItems();
        attributeSelectionItems = listBuildAttributeSelectionItems;
        return listBuildAttributeSelectionItems;
    }

    public static Optional<ListSelectionElementModel> findAttributeSelectionItem(Identifier id) {
        if (id == null) {
            return Optional.empty();
        }
        if (attributeSelectionItems == null) {
            attributeSelectionItems = buildAttributeSelectionItems();
        }
        return attributeSelectionItems.stream().filter(item -> item.getId().equals(id)).findFirst();
    }

    public static List<String> getPotionSuggestions() {
        if (potionSuggestions != null) {
            return potionSuggestions;
        }
        List<String> listBuildSuggestions = buildSuggestions((Registry<?>) BuiltInRegistries.POTION);
        potionSuggestions = listBuildSuggestions;
        return listBuildSuggestions;
    }

    public static List<ItemListSelectionElementModel> getPotionSelectionItems() {
        if (potionSelectionItems != null) {
            return potionSelectionItems;
        }
        List<ItemListSelectionElementModel> listBuildPotionSelectionItems = buildPotionSelectionItems();
        potionSelectionItems = listBuildPotionSelectionItems;
        return listBuildPotionSelectionItems;
    }

    public static List<String> getEffectSuggestions() {
        if (effectSuggestions != null) {
            return effectSuggestions;
        }
        List<String> listBuildSuggestions = buildSuggestions((Registry<?>) BuiltInRegistries.MOB_EFFECT);
        effectSuggestions = listBuildSuggestions;
        return listBuildSuggestions;
    }

    public static List<SelectableSpriteListSelectionElementModel> getEffectSelectionItems() {
        if (effectSelectionItems != null) {
            return effectSelectionItems;
        }
        List<SelectableSpriteListSelectionElementModel> listBuildEffectSelectionItems = buildEffectSelectionItems();
        effectSelectionItems = listBuildEffectSelectionItems;
        return listBuildEffectSelectionItems;
    }

    public static List<String> getEntitySuggestions() {
        if (entitySuggestions != null) {
            return entitySuggestions;
        }
        List<String> listBuildSuggestions = buildSuggestions((Registry<?>) BuiltInRegistries.ENTITY_TYPE);
        entitySuggestions = listBuildSuggestions;
        return listBuildSuggestions;
    }

    public static List<String> getEquipmentAssetSuggestions() {
        if (equipmentAssetSuggestions != null) {
            return equipmentAssetSuggestions;
        }
        List<String> listBuildEquipmentAssetSuggestions = buildEquipmentAssetSuggestions();
        equipmentAssetSuggestions = listBuildEquipmentAssetSuggestions;
        return listBuildEquipmentAssetSuggestions;
    }

    public static List<ListSelectionElementModel> getEquipmentAssetSelectionItems() {
        if (equipmentAssetSelectionItems != null) {
            return equipmentAssetSelectionItems;
        }
        List<ListSelectionElementModel> listBuildEquipmentAssetSelectionItems = buildEquipmentAssetSelectionItems();
        equipmentAssetSelectionItems = listBuildEquipmentAssetSelectionItems;
        return listBuildEquipmentAssetSelectionItems;
    }

    public static List<EntityListSelectionElementModel> getEntitySelectionItems() {
        if (entitySelectionItems != null) {
            return entitySelectionItems;
        }
        List<EntityListSelectionElementModel> listBuildEntitySelectionItems = buildEntitySelectionItems();
        entitySelectionItems = listBuildEntitySelectionItems;
        return listBuildEntitySelectionItems;
    }

    public static List<String> getVillagerProfessionSuggestions() {
        if (villagerProfessionSuggestions != null) {
            return villagerProfessionSuggestions;
        }
        List<String> listBuildSuggestions = buildSuggestions((Registry<?>) BuiltInRegistries.VILLAGER_PROFESSION);
        villagerProfessionSuggestions = listBuildSuggestions;
        return listBuildSuggestions;
    }

    public static List<ListSelectionElementModel> getVillagerProfessionSelectionItems() {
        if (villagerProfessionSelectionItems != null) {
            return villagerProfessionSelectionItems;
        }
        List<ListSelectionElementModel> listBuildVillagerProfessionSelectionItems = buildVillagerProfessionSelectionItems();
        villagerProfessionSelectionItems = listBuildVillagerProfessionSelectionItems;
        return listBuildVillagerProfessionSelectionItems;
    }

    public static List<String> getVillagerTypeSuggestions() {
        if (villagerTypeSuggestions != null) {
            return villagerTypeSuggestions;
        }
        List<String> listBuildSuggestions = buildSuggestions((Registry<?>) BuiltInRegistries.VILLAGER_TYPE);
        villagerTypeSuggestions = listBuildSuggestions;
        return listBuildSuggestions;
    }

    public static List<ListSelectionElementModel> getVillagerTypeSelectionItems() {
        if (villagerTypeSelectionItems != null) {
            return villagerTypeSelectionItems;
        }
        List<ListSelectionElementModel> listBuildVillagerTypeSelectionItems = buildVillagerTypeSelectionItems();
        villagerTypeSelectionItems = listBuildVillagerTypeSelectionItems;
        return listBuildVillagerTypeSelectionItems;
    }

    public static List<String> getTrimPatternSuggestions() {
        if (trimPatternSuggestions == null) {
            trimPatternSuggestions = getTrimPatternSelectionItems().stream().map(element -> element.getId().toString()).toList();
        }
        return trimPatternSuggestions;
    }

    public static List<TrimPatternSelectionElementModel> getTrimPatternSelectionItems() {
        if (trimPatternSelectionItems == null) {
            trimPatternSelectionItems = (List) registryAccess().lookup(Registries.TRIM_PATTERN).map(ClientCache::buildTrimPatternSelectionItems).orElseGet(List::of);
        }
        return trimPatternSelectionItems;
    }

    public static List<String> getTrimMaterialSuggestions() {
        if (trimMaterialSuggestions == null) {
            trimMaterialSuggestions = getTrimMaterialSelectionItems().stream().map(element -> element.getId().toString()).toList();
        }
        return trimMaterialSuggestions;
    }

    public static List<TrimMaterialSelectionElementModel> getTrimMaterialSelectionItems() {
        if (trimMaterialSelectionItems == null) {
            trimMaterialSelectionItems = (List) registryAccess().lookup(Registries.TRIM_MATERIAL).map(ClientCache::buildTrimMaterialSelectionItems).orElseGet(List::of);
        }
        return trimMaterialSelectionItems;
    }

    public static List<String> getBannerPatternSuggestions() {
        if (bannerPatternSuggestions == null) {
            bannerPatternSuggestions = getBannerPatternSelectionItems().stream().map(element -> element.getId().toString()).toList();
        }
        return bannerPatternSuggestions;
    }

    public static List<BannerPatternSelectionElementModel> getBannerPatternSelectionItems() {
        if (bannerPatternSelectionItems == null) {
            bannerPatternSelectionItems = (List) registryAccess().lookup(Registries.BANNER_PATTERN).map(ClientCache::buildBannerPatternSelectionItems).orElseGet(List::of);
        }
        return bannerPatternSelectionItems;
    }

    public static List<String> getInstrumentSuggestions() {
        if (instrumentSuggestions == null) {
            instrumentSuggestions = getInstrumentSelectionItems().stream().map(element -> element.getId().toString()).toList();
        }
        return instrumentSuggestions;
    }

    public static List<ListSelectionElementModel> getJukeboxSongSelectionItems() {
        if (jukeboxSongSelectionItems == null) {
            jukeboxSongSelectionItems = (List) registryAccess().lookup(Registries.JUKEBOX_SONG).map(ClientCache::buildJukeboxSongSelectionItems).orElseGet(List::of);
        }
        return jukeboxSongSelectionItems;
    }

    public static List<String> getJukeboxSongSuggestions() {
        if (jukeboxSongSuggestions == null) {
            jukeboxSongSuggestions = getJukeboxSongSelectionItems().stream().map(element -> element.getId().toString()).toList();
        }
        return jukeboxSongSuggestions;
    }

    public static List<ListSelectionElementModel> getInstrumentSelectionItems() {
        if (instrumentSelectionItems == null) {
            instrumentSelectionItems = (List) registryAccess().lookup(Registries.INSTRUMENT).map(ClientCache::buildInstrumentSelectionItems).orElseGet(List::of);
        }
        return instrumentSelectionItems;
    }

    public static List<String> getSoundEventSuggestions() {
        if (soundEventSuggestions == null) {
            List<String> listBuildSuggestions = buildSuggestions((Registry<?>) BuiltInRegistries.SOUND_EVENT);
            soundEventSuggestions = listBuildSuggestions;
            return listBuildSuggestions;
        }
        return soundEventSuggestions;
    }

    public static List<SoundEventListSelectionElementModel> getSoundEventSelectionItems() {
        if (soundEventSelectionItems == null) {
            soundEventSelectionItems = buildSoundEventSelectionItems();
        }
        return soundEventSelectionItems;
    }

    public static List<ListSelectionFilter> getSoundEventFilters() {
        if (soundEventFilters == null) {
            soundEventFilters = buildSoundEventFilters();
        }
        return soundEventFilters;
    }

    public static List<String> getLootTableSuggestions() {
        if (lootTableSuggestions == null) {
            lootTableSuggestions = buildLootTableSuggestions();
        }
        return lootTableSuggestions;
    }

    public static Optional<SelectableSpriteListSelectionElementModel> findEffectSelectionItem(Identifier id) {
        if (id == null) {
            return Optional.empty();
        }
        if (effectSelectionItems == null) {
            effectSelectionItems = buildEffectSelectionItems();
        }
        return effectSelectionItems.stream().filter(item -> item.getId().equals(id)).findFirst();
    }

    private static List<String> buildSuggestions(Registry<?> registry) {
        List<String> suggestions = new ArrayList<>();
        registry.entrySet().stream().map(e -> ((ResourceKey) e.getKey()).identifier().toString()).forEach(id -> {
            suggestions.add(id);
            if (id.startsWith("minecraft:")) {
                suggestions.add(id.substring(10));
            }
        });
        return suggestions;
    }

    private static List<String> buildSuggestions(HolderLookup.RegistryLookup<?> lookup) {
        List<String> suggestions = new ArrayList<>();
        lookup.listElements().forEach(holder -> {
            String id = holder.key().identifier().toString();
            suggestions.add(id);
            if (id.startsWith("minecraft:")) {
                suggestions.add(id.substring(10));
            }
        });
        return suggestions;
    }

    private static List<String> buildLootTableSuggestions() {
        List<Identifier> ids = LootTableIndex.getAll();
        if (ids.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<String> values = new LinkedHashSet<>();
        for (Identifier id : ids) {
            String full = id.toString();
            values.add(full);
            if (full.startsWith("minecraft:")) {
                values.add(full.substring(10));
            }
        }
        return List.copyOf(values);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<ItemListSelectionElementModel> buildItemSelectionItems() {
        return (List) BuiltInRegistries.ITEM.entrySet().stream().map(e -> {
            return new SelectableItemListSelectionElementModel(((Item) e.getValue()).getDescriptionId(), ((ResourceKey) e.getKey()).identifier(), (Supplier<ItemStack>) () -> {
                return new ItemStack((ItemLike) e.getValue());
            });
        }).sorted().toList();
    }

    private static List<TagListSelectionElementModel> buildBlockTagSelectionItems() {
        return buildTagSelectionItems(Registries.BLOCK);
    }

    private static List<TagListSelectionElementModel> buildItemTagSelectionItems() {
        return buildTagSelectionItems(Registries.ITEM);
    }

    private static List<TagListSelectionElementModel> buildDamageTypeTagSelectionItems() {
        return buildTagSelectionItems(Registries.DAMAGE_TYPE);
    }

    private static List<TagListSelectionElementModel> buildEffectTagSelectionItems() {
        return buildTagSelectionItems(Registries.MOB_EFFECT);
    }

    private static List<TagListSelectionElementModel> buildEntityTypeTagSelectionItems() {
        return buildTagSelectionItems(Registries.ENTITY_TYPE);
    }

    private static List<ListSelectionElementModel> buildDamageTypeSelectionItems() {
        return (List) registryAccess().lookup(Registries.DAMAGE_TYPE).map(lookup -> {
            return lookup.listElements().map(element -> new ListSelectionElementModel(element.key().identifier().toString(), element.key().identifier())).sorted().toList();
        }).orElseGet(List::of);
    }

    private static <T> List<TagListSelectionElementModel> buildTagSelectionItems(ResourceKey<Registry<T>> registryKey) {
        return (List) registryAccess().lookup(registryKey).map(lookup -> {
            return lookup.listTags().map(named -> new SelectableTagListSelectionElementModel(named.key().location())).sorted().toList();
        }).orElseGet(List::of);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<ItemListSelectionElementModel> buildBlockSelectionItems() {
        return (List) BuiltInRegistries.BLOCK.entrySet().stream().map(e -> {
            return new SelectableItemListSelectionElementModel(((Block) e.getValue()).getDescriptionId(), ((ResourceKey) e.getKey()).identifier(), (Supplier<ItemStack>) () -> {
                return new ItemStack((ItemLike) e.getValue());
            });
        }).sorted().toList();
    }

    private static List<EntityListSelectionElementModel> buildEntitySelectionItems() {
        return BuiltInRegistries.ENTITY_TYPE.entrySet().stream().map(e -> new EntityListSelectionElementModel((EntityType) e.getValue(), ((ResourceKey) e.getKey()).identifier())).sorted().toList();
    }

    private static List<ListSelectionElementModel> buildVillagerProfessionSelectionItems() {
        return BuiltInRegistries.VILLAGER_PROFESSION.entrySet().stream().map(e -> new ListSelectionElementModel(villagerProfessionTranslation(((ResourceKey) e.getKey()).identifier()), ((ResourceKey) e.getKey()).identifier())).sorted().toList();
    }

    private static List<ListSelectionElementModel> buildVillagerTypeSelectionItems() {
        return BuiltInRegistries.VILLAGER_TYPE.entrySet().stream().map(e -> new ListSelectionElementModel(villagerTypeTranslation(((ResourceKey) e.getKey()).identifier()), ((ResourceKey) e.getKey()).identifier())).sorted().toList();
    }

    private static String villagerProfessionTranslation(Identifier id) {
        return "villager.profession." + id.getPath();
    }

    private static String villagerTypeTranslation(Identifier id) {
        return "entity.minecraft.villager." + id.getPath();
    }

    private static List<EnchantmentListSelectionElementModel> buildEnchantmentSelectionItems() {
        return (List) registryAccess().lookup(Registries.ENCHANTMENT).map(lookup -> {
            return lookup.listElements().map(ref -> {
                Item iconItem = getEnchantmentTypeItem(ref);
                ItemStack icon = new ItemStack(iconItem);
                Component categoryLabel = buildEnchantmentCategoryLabel(ref, icon);
                return new EnchantmentListSelectionElementModel(((Enchantment) ref.value()).description().getString(), ref.key().identifier(), (Holder<Enchantment>) ref, (Supplier<ItemStack>) () -> {
                    return new ItemStack(iconItem);
                }, categoryLabel);
            }).sorted().toList();
        }).orElseGet(List::of);
    }

    private static Component buildEnchantmentCategoryLabel(Holder<Enchantment> enchantment, ItemStack icon) {
        List<Component> labels = (List) ((Enchantment) enchantment.value()).definition().primaryItems().map(ClientCache::describeItemSet).orElseGet(List::of);
        if (labels.isEmpty()) {
            labels = describeItemSet(((Enchantment) enchantment.value()).definition().supportedItems());
        }
        if (!labels.isEmpty()) {
            return joinComponents(labels);
        }
        List<Component> slotLabels = describeSlotGroups(((Enchantment) enchantment.value()).definition().slots());
        if (!slotLabels.isEmpty()) {
            return joinComponents(slotLabels);
        }
        return icon.getHoverName().copy();
    }

    private static Item getEnchantmentTypeItem(Holder<Enchantment> enchantment) {
        return (Item) ((Enchantment) enchantment.value()).definition().primaryItems().flatMap(ClientCache::pickRepresentativeItem).or(() -> {
            return pickRepresentativeItem(((Enchantment) enchantment.value()).definition().supportedItems());
        }).or(() -> {
            return pickItemFromSlots(((Enchantment) enchantment.value()).definition().slots());
        }).orElse(Items.ENCHANTED_BOOK);
    }

    private static List<Component> describeItemSet(HolderSet<Item> holders) {
        List<Component> result = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        holders.stream().map(Holder::value).filter(item -> item != Items.AIR).forEach(item -> {
            if (seen.add(item.getDescriptionId())) {
                result.add(new ItemStack(item).getHoverName().copy());
            }
        });
        return limitComponentList(result);
    }

    private static List<Component> describeSlotGroups(List<EquipmentSlotGroup> slots) {
        Set<String> seen = new LinkedHashSet<>();
        List<Component> result = new ArrayList<>();
        for (EquipmentSlotGroup slot : slots) {
            String raw = slot.name().toLowerCase(Locale.ROOT).replace('_', ' ');
            if (seen.add(raw)) {
                result.add(Component.literal(capitalizeWords(raw)));
            }
        }
        return limitComponentList(result);
    }

    private static List<Component> limitComponentList(List<Component> components) {
        if (components.size() <= 3) {
            return components;
        }
        List<Component> limited = new ArrayList<>(components.subList(0, 3));
        limited.add(Component.literal("…"));
        return limited;
    }

    private static Component joinComponents(List<Component> components) {
        MutableComponent result = Component.empty();
        for (int i = 0; i < components.size(); i++) {
            if (i > 0) {
                result.append(Component.literal(", "));
            }
            result.append(components.get(i).copy());
        }
        return result;
    }

    private static String capitalizeWords(String input) {
        if (input.isEmpty()) {
            return input;
        }
        StringBuilder builder = new StringBuilder(input.length());
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (c == ' ') {
                capitalizeNext = true;
                builder.append(c);
            } else if (capitalizeNext) {
                builder.append(Character.toTitleCase(c));
                capitalizeNext = false;
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    
    public static Optional<Item> pickRepresentativeItem(HolderSet<Item> holders) {
        return holders.stream().map(Holder::value).filter(item -> item != Items.AIR).min(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()));
    }

    
    public static Optional<Item> pickItemFromSlots(List<EquipmentSlotGroup> slots) {
        if (slots.contains(EquipmentSlotGroup.ARMOR) || slots.contains(EquipmentSlotGroup.BODY)) {
            return Optional.of(Items.IRON_CHESTPLATE);
        }
        if (slots.contains(EquipmentSlotGroup.HEAD)) {
            return Optional.of(Items.DIAMOND_HELMET);
        }
        if (slots.contains(EquipmentSlotGroup.CHEST)) {
            return Optional.of(Items.DIAMOND_CHESTPLATE);
        }
        if (slots.contains(EquipmentSlotGroup.LEGS)) {
            return Optional.of(Items.DIAMOND_LEGGINGS);
        }
        if (slots.contains(EquipmentSlotGroup.FEET)) {
            return Optional.of(Items.DIAMOND_BOOTS);
        }
        if (slots.contains(EquipmentSlotGroup.MAINHAND) || slots.contains(EquipmentSlotGroup.HAND)) {
            return Optional.of(Items.DIAMOND_SWORD);
        }
        if (slots.contains(EquipmentSlotGroup.OFFHAND)) {
            return Optional.of(Items.SHIELD);
        }
        return Optional.empty();
    }

    private static List<ListSelectionElementModel> buildAttributeSelectionItems() {
        return BuiltInRegistries.ATTRIBUTE.entrySet().stream().map(e -> new ListSelectionElementModel(((Attribute) e.getValue()).getDescriptionId(), ((ResourceKey) e.getKey()).identifier())).sorted().toList();
    }

    private static List<ItemListSelectionElementModel> buildPotionSelectionItems() {
        return (List) registryAccess().lookup(Registries.POTION).map(lookup -> {
            return lookup.listElements().map(holder -> {
                PotionContents contents = new PotionContents(holder);
                String name = contents.getName(Items.POTION.getDescriptionId() + ".effect.").getString();
                return new ItemListSelectionElementModel(name, holder.key().identifier(), (Supplier<ItemStack>) () -> {
                    return ColoredItemHelper.createColoredPotionItem(holder.key().identifier(), Color.NONE);
                });
            }).sorted().toList();
        }).orElseGet(List::of);
    }

    private static List<SelectableSpriteListSelectionElementModel> buildEffectSelectionItems() {
        return (List) registryAccess().lookup(Registries.MOB_EFFECT).map(lookup -> {
            return lookup.listElements().map(holder -> new SelectableSpriteListSelectionElementModel(((MobEffect) holder.value()).getDescriptionId(), holder.key().identifier(), mobEffectSpriteSupplier(holder))).sorted().toList();
        }).orElseGet(List::of);
    }

    private static List<TrimPatternSelectionElementModel> buildTrimPatternSelectionItems(HolderLookup.RegistryLookup<TrimPattern> lookup) {
        return lookup.listElements().map(holder -> {
            Identifier patternId = holder.key().identifier();
            return new TrimPatternSelectionElementModel(((TrimPattern) holder.value()).description(), patternId, (Supplier<ItemStack>) () -> {
                return patternIconStack(patternId);
            });
        }).sorted().toList();
    }

    private static List<TrimMaterialSelectionElementModel> buildTrimMaterialSelectionItems(HolderLookup.RegistryLookup<TrimMaterial> lookup) {
        return lookup.listElements().map(holder -> {
            Identifier materialId = holder.key().identifier();
            return new TrimMaterialSelectionElementModel(((TrimMaterial) holder.value()).description(), materialId, (Supplier<ItemStack>) () -> {
                return materialIconStack(materialId);
            });
        }).sorted().toList();
    }

    private static List<BannerPatternSelectionElementModel> buildBannerPatternSelectionItems(HolderLookup.RegistryLookup<BannerPattern> lookup) {
        return lookup.listElements().map(holder -> {
            Identifier patternId = holder.key().identifier();
            String translationKey = "cadeditor.banner_pattern." + patternId.getPath();
            return new BannerPatternSelectionElementModel(translationKey, patternId, (Supplier<ItemStack>) () -> {
                return bannerPatternItemStack(patternId);
            });
        }).filter(element -> element.getItem().getItem() != Items.BANNER.pick(DyeColor.WHITE)).sorted().toList();
    }

    private static List<ListSelectionElementModel> buildJukeboxSongSelectionItems(HolderLookup.RegistryLookup<JukeboxSong> lookup) {
        return lookup.listElements()
                .map(holder -> new ListSelectionElementModel("jukebox_song." + holder.key().identifier(), holder.key().identifier()))
                .sorted()
                .toList();
    }

    private static List<ListSelectionElementModel> buildInstrumentSelectionItems(HolderLookup.RegistryLookup<Instrument> lookup) {
        return lookup.listElements().map(holder -> new ListSelectionElementModel(holder.key().identifier().toString(), holder.key().identifier())).sorted().toList();
    }

    private static List<SoundEventListSelectionElementModel> buildSoundEventSelectionItems() {
        return BuiltInRegistries.SOUND_EVENT.entrySet().stream().map(entry -> new SoundEventListSelectionElementModel(((ResourceKey) entry.getKey()).identifier(), (SoundEvent) entry.getValue())).sorted().toList();
    }

    private static List<String> buildEquipmentAssetSuggestions() {
        return (List) registryAccess().lookup(EquipmentAssets.ROOT_ID).map(lookup -> {
            return lookup.listElements().map(element -> element.key().identifier().toString()).sorted().toList();
        }).orElse((List) BUILTIN_EQUIPMENT_ASSETS.stream().map(value -> value.toString()).collect(Collectors.toUnmodifiableList()));
    }

    private static List<ListSelectionElementModel> buildEquipmentAssetSelectionItems() {
        return (List) registryAccess().lookup(EquipmentAssets.ROOT_ID).map(lookup -> {
            return lookup.listElements().map(element -> new EquipmentAssetListSelectionElementModel(element.key().identifier())).sorted().toList();
        }).orElse((List) BUILTIN_EQUIPMENT_ASSETS.stream().map(id -> new EquipmentAssetListSelectionElementModel(id)).collect(Collectors.toUnmodifiableList()));
    }

    private static List<Identifier> buildBuiltinEquipmentAssets() {
        List<Identifier> ids = new ArrayList<>();
        ids.add(Identifier.withDefaultNamespace("leather"));
        ids.add(Identifier.withDefaultNamespace("chainmail"));
        ids.add(Identifier.withDefaultNamespace("iron"));
        ids.add(Identifier.withDefaultNamespace("gold"));
        ids.add(Identifier.withDefaultNamespace("diamond"));
        ids.add(Identifier.withDefaultNamespace("turtle_scute"));
        ids.add(Identifier.withDefaultNamespace("netherite"));
        ids.add(Identifier.withDefaultNamespace("armadillo_scute"));
        ids.add(Identifier.withDefaultNamespace("elytra"));
        ids.add(Identifier.withDefaultNamespace("saddle"));
        ids.add(Identifier.withDefaultNamespace("trader_llama"));
        for (String color : List.of(new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"})) {
            ids.add(Identifier.withDefaultNamespace("carpet_" + color));
            ids.add(Identifier.withDefaultNamespace("harness_" + color));
        }
        return List.copyOf(ids);
    }

    private static List<ListSelectionFilter> buildSoundEventFilters() {
        List<SoundEventListSelectionElementModel> items = getSoundEventSelectionItems();
        if (items.isEmpty()) {
            return List.of(new ListSelectionFilter("category:all", ModTexts.soundFilterCategoryAll(), null), new ListSelectionFilter("namespace:all", ModTexts.soundFilterNamespaceAll(), null));
        }
        List<ListSelectionFilter> filters = new ArrayList<>();
        filters.add(new ListSelectionFilter("category:all", ModTexts.soundFilterCategoryAll(), null));
        for (SoundEventListSelectionElementModel.SoundCategory category : SoundEventListSelectionElementModel.SoundCategory.values()) {
            filters.add(new ListSelectionFilter("category:" + category.id(), ModTexts.soundFilterCategory(category.label()), element -> {
                if (element instanceof SoundEventListSelectionElementModel) {
                    SoundEventListSelectionElementModel sound = (SoundEventListSelectionElementModel) element;
                    if (sound.getPrimaryCategory() == category) {
                        return true;
                    }
                }
                return false;
            }));
        }
        filters.add(new ListSelectionFilter("namespace:all", ModTexts.soundFilterNamespaceAll(), null));
        items.stream().map(value -> value.getNamespace()).distinct().sorted().forEach(namespace -> {
            filters.add(new ListSelectionFilter("namespace:" + namespace, ModTexts.soundFilterNamespace(namespace), element -> {
                if (element instanceof SoundEventListSelectionElementModel) {
                    SoundEventListSelectionElementModel sound = (SoundEventListSelectionElementModel) element;
                    if (sound.getNamespace().equals(namespace)) {
                        return true;
                    }
                }
                return false;
            }));
        });
        return List.copyOf(filters);
    }

    private static Supplier<TextureAtlasSprite> mobEffectSpriteSupplier(Holder<MobEffect> holder) {
        return () -> {
            Identifier spriteId = mobEffectSprite(holder);
            if (Minecraft.getInstance().getTextureManager().getTexture(GUI_ATLAS_LOCATION) instanceof TextureAtlas atlas) {
                return atlas.getSprite(spriteId);
            }
            return null;
        };
    }

    private static Identifier mobEffectSprite(Holder<MobEffect> holder) {
        return (Identifier) holder.unwrapKey().map(value -> value.identifier()).map(loc -> Identifier.fromNamespaceAndPath(loc.getNamespace(), "mob_effect/" + loc.getPath())).orElseThrow();
    }

    
    public static ItemStack patternIconStack(Identifier patternId) {
        return new ItemStack(patternTemplateItem(patternId));
    }

    private static Item patternTemplateItem(Identifier patternId) {
        return switch (patternId.getPath()) {
            case "sentry" -> Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "dune" -> Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "coast" -> Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "wild" -> Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "ward" -> Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "eye" -> Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "vex" -> Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "tide" -> Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "snout" -> Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "rib" -> Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "spire" -> Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "wayfinder" -> Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "shaper" -> Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "silence" -> Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "raiser" -> Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "host" -> Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "flow" -> Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE;
            case "bolt" -> Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE;
            default -> Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE;
        };
    }

    
    public static ItemStack materialIconStack(Identifier materialId) {
        return new ItemStack(materialDisplayItem(materialId));
    }

    private static Item materialDisplayItem(Identifier materialId) {
        return switch (materialId.getPath()) {
            case "quartz" -> Items.QUARTZ;
            case "iron" -> Items.IRON_INGOT;
            case "netherite" -> Items.NETHERITE_INGOT;
            case "redstone" -> Items.REDSTONE;
            case "copper" -> Items.COPPER_INGOT;
            case "gold" -> Items.GOLD_INGOT;
            case "emerald" -> Items.EMERALD;
            case "diamond" -> Items.DIAMOND;
            case "lapis" -> Items.LAPIS_LAZULI;
            case "amethyst" -> Items.AMETHYST_SHARD;
            case "resin" -> Items.RESIN_BRICK;
            default -> Items.IRON_INGOT;
        };
    }

    private static ItemStack bannerPatternItemStack(Identifier patternId) {
        return new ItemStack(bannerPatternItem(patternId));
    }

    private static Item bannerPatternItem(Identifier patternId) {
        return switch (patternId.getPath()) {
            case "flower" -> Items.FLOWER_BANNER_PATTERN;
            case "creeper" -> Items.CREEPER_BANNER_PATTERN;
            case "skull" -> Items.SKULL_BANNER_PATTERN;
            case "mojang" -> Items.MOJANG_BANNER_PATTERN;
            case "globe" -> Items.GLOBE_BANNER_PATTERN;
            case "piglin" -> Items.PIGLIN_BANNER_PATTERN;
            case "flow" -> Items.FLOW_BANNER_PATTERN;
            case "guster" -> Items.GUSTER_BANNER_PATTERN;
            case "field_masoned" -> Items.FIELD_MASONED_BANNER_PATTERN;
            case "bordure_indented" -> Items.BORDURE_INDENTED_BANNER_PATTERN;
            default -> Items.BANNER.pick(DyeColor.WHITE);
        };
    }


    private static HolderLookup.Provider registryAccess() {
        return ClientUtil.registryAccess();
    }
}
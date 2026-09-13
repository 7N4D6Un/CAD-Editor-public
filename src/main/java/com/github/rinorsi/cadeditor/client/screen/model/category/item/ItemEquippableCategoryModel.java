package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.LabeledEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.EquipmentAssetSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.SoundEventSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.TrimMaterialSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.TrimPatternSelectionEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.util.Unit;


public class ItemEquippableCategoryModel extends ItemEditorCategoryModel {
    private EquipmentSlot slot;
    private String equipSoundId;
    private String assetId;
    private String cameraOverlayId;
    private boolean dispensable;
    private boolean swappable;
    private boolean damageOnHurt;
    private boolean equipOnInteract;
    private boolean canBeSheared;
    private String shearingSoundId;
    private StringWithActionsEntryModel allowedEntitiesEntry;
    private String allowedEntitiesRaw = "";
    private SoundEventSelectionEntryModel equipSoundEntry;
    private SoundEventSelectionEntryModel shearingSoundEntry;
    private EquipmentAssetSelectionEntryModel assetEntry;
    private StringEntryModel overlayEntry;
    private TrimPatternSelectionEntryModel patternEntry;
    private TrimMaterialSelectionEntryModel materialEntry;
    private String patternId = "";
    private String materialId = "";
    private BooleanEntryModel behaviorToggle;
    private boolean enableEquippable;

    public ItemEquippableCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("armor"), editor);
    }

    @Override
    protected void setupEntries() {
        loadStateFromStack();
        this.enableEquippable = getParent().getContext().getItemStack().has(DataComponents.EQUIPPABLE);
        this.behaviorToggle = new BooleanEntryModel(this, ModTexts.gui("armor_behavior_enabled"), this.enableEquippable, value -> {
            this.enableEquippable = value != null && value;
            syncEntriesEnabled();
        });
        getEntries().add(this.behaviorToggle);
        EnumEntryModel<EquipmentSlot> slotEntry = new EnumEntryModel<>(this, ModTexts.gui("equippable_slot"), Arrays.asList(EquipmentSlot.values()), this.slot, value -> this.slot = value == null ? EquipmentSlot.HEAD : value).withTextFactory(ModTexts::equipmentSlot);
        getEntries().add(slotEntry);
        this.equipSoundEntry = new SoundEventSelectionEntryModel(this, ModTexts.gui("equippable_equip_sound"), this.equipSoundId, this::setEquipSoundId, namespaceFilter(this.equipSoundId));
        getEntries().add(this.equipSoundEntry);
        this.assetEntry = new EquipmentAssetSelectionEntryModel(this, ModTexts.EQUIPMENT_ASSET, this.assetId, this::setAssetId);
        getEntries().add(this.assetEntry);
        this.overlayEntry = new StringEntryModel(this, ModTexts.gui("equippable_camera_overlay"), this.cameraOverlayId, this::setCameraOverlayId);
        this.overlayEntry.setPlaceholder("minecraft:spyglass_overlay");
        getEntries().add(this.overlayEntry);
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("equippable_dispensable"), this.dispensable, value -> this.dispensable = value));
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("equippable_swappable"), this.swappable, value -> this.swappable = value));
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("equippable_damage_on_hurt"), this.damageOnHurt, value -> this.damageOnHurt = value));
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("equippable_on_interact"), this.equipOnInteract, value -> this.equipOnInteract = value));
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("glider_enabled"), getParent().getContext().getItemStack().has(DataComponents.GLIDER), value -> setGliderEnabled(value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("equippable_can_be_sheared"), this.canBeSheared, value -> this.canBeSheared = value));
        this.shearingSoundEntry = new SoundEventSelectionEntryModel(this, ModTexts.gui("equippable_shearing_sound"), this.shearingSoundId, this::setShearingSoundId, namespaceFilter(this.shearingSoundId));
        getEntries().add(this.shearingSoundEntry);
        ItemStack trimStack = getParent().getContext().getItemStack();
        ArmorTrim trim = trimStack.get(DataComponents.TRIM);
        if (trim != null) {
            this.patternId = trim.pattern().unwrapKey().map(ResourceKey::identifier).map(Identifier::toString).orElse("");
            this.materialId = trim.material().unwrapKey().map(ResourceKey::identifier).map(Identifier::toString).orElse("");
        }
        this.patternEntry = new TrimPatternSelectionEntryModel(this, this.patternId, value -> this.patternId = value == null ? "" : value.trim());
        this.materialEntry = new TrimMaterialSelectionEntryModel(this, this.materialId, value -> this.materialId = value == null ? "" : value.trim());
        this.patternEntry.setPlaceholder("minecraft:coast");
        this.materialEntry.setPlaceholder("minecraft:diamond");
        getEntries().add(this.patternEntry);
        getEntries().add(this.materialEntry);
        this.allowedEntitiesEntry = withWikiTooltip(new StringWithActionsEntryModel(this, ModTexts.gui("equippable_allowed_entities"), this.allowedEntitiesRaw, this::setAllowedEntitiesRaw), "equippable_allowed_entities", 2);
        this.allowedEntitiesEntry.setPlaceholder("minecraft:zombie 或 #minecraft:undead");
        this.allowedEntitiesEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_entity"), this::openAllowedEntitySelection));
        this.allowedEntitiesEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_tag"), this::openAllowedEntityTagSelection));
        getEntries().add(this.allowedEntitiesEntry);
        syncEntriesEnabled();
    }

    @Override
    public void initalize() {
        super.initalize();
        syncEntriesEnabled();
    }

    private void syncEntriesEnabled() {
        for (EntryModel entry : getEntries()) {
            if (entry != this.behaviorToggle) {
                entry.setEnabled(this.enableEquippable);
            }
        }
    }

    private void setGliderEnabled(boolean value) {
        ItemStack stack = getParent().getContext().getItemStack();
        if (value) {
            stack.set(DataComponents.GLIDER, Unit.INSTANCE);
        } else {
            stack.remove(DataComponents.GLIDER);
            getParent().removeComponentFromDataTag("minecraft:glider");
        }
    }

    @Override
    public void apply() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.enableEquippable) {
            stack.remove(DataComponents.EQUIPPABLE);
            getParent().removeComponentFromDataTag("minecraft:equippable");
            return;
        }
        super.apply();
        HolderLookup.Provider provider = ClientUtil.registryAccess();
        HolderLookup.RegistryLookup<SoundEvent> soundLookup = (HolderLookup.RegistryLookup) provider.lookup(Registries.SOUND_EVENT).orElse(null);
        HolderLookup.RegistryLookup<EntityType<?>> entityLookup = (HolderLookup.RegistryLookup) provider.lookup(Registries.ENTITY_TYPE).orElse(null);
        Optional<Holder<SoundEvent>> equipSoundHolder = resolveSound(soundLookup, this.equipSoundId);
        Optional<Holder<SoundEvent>> shearingSoundHolder = resolveSound(soundLookup, this.shearingSoundId);
        if (soundLookup == null) {
            this.equipSoundEntry.setValid(false);
            this.shearingSoundEntry.setValid(false);
            return;
        }
        this.equipSoundEntry.setValid(equipSoundHolder.isPresent() || this.equipSoundId.isBlank());
        this.shearingSoundEntry.setValid(shearingSoundHolder.isPresent() || this.shearingSoundId.isBlank());
        Equippable.Builder builder = Equippable.builder(this.slot);
        equipSoundHolder.ifPresent(builder::setEquipSound);
        Optional<ResourceKey<EquipmentAsset>> equipmentAsset = parseEquipmentAsset();
        equipmentAsset.ifPresent(builder::setAsset);
        Optional<Identifier> cameraOverlay = parseCameraOverlay();
        cameraOverlay.ifPresent(builder::setCameraOverlay);
        if (!resolveAllowedEntities(entityLookup, builder)) {
            return;
        }
        builder.setDispensable(this.dispensable);
        builder.setSwappable(this.swappable);
        builder.setDamageOnHurt(this.damageOnHurt);
        builder.setEquipOnInteract(this.equipOnInteract);
        builder.setCanBeSheared(this.canBeSheared);
        shearingSoundHolder.ifPresent(builder::setShearingSound);
        stack.set(DataComponents.EQUIPPABLE, builder.build());
        applyTrimComponent(stack);
    }

    private boolean resolveAllowedEntities(HolderLookup.RegistryLookup<EntityType<?>> lookup, Equippable.Builder builder) {
        if (this.allowedEntitiesRaw.isBlank()) {
            this.allowedEntitiesEntry.setValid(true);
            return true;
        }
        List<String> entries = parseIdentifierList(this.allowedEntitiesRaw);
        if (entries.isEmpty()) {
            this.allowedEntitiesEntry.setValid(false);
            return false;
        }
        List<String> tagIds = new ArrayList<>();
        List<String> entityIds = new ArrayList<>();
        for (String entry : entries) {
            if (entry.startsWith("#")) {
                String tagId = entry.substring(1);
                if (!tagId.isBlank()) {
                    tagIds.add(tagId);
                }
            } else if (!entry.isBlank()) {
                entityIds.add(entry);
            }
        }
        if (entityIds.isEmpty() && tagIds.size() == 1) {
            if (lookup == null) {
                this.allowedEntitiesEntry.setValid(false);
                return false;
            }
            Optional<HolderSet<EntityType<?>>> named = resolveNamedEntityTypeTag(lookup, tagIds.get(0));
            if (named.isPresent()) {
                this.allowedEntitiesEntry.setValid(true);
                builder.setAllowedEntities(named.get());
                return true;
            }
            this.allowedEntitiesEntry.setValid(false);
            return false;
        }
        List<Holder<EntityType<?>>> holders = new ArrayList<>();
        if (lookup != null) {
            for (String tagId : tagIds) {
                resolveNamedEntityTypeTag(lookup, tagId).ifPresent(named -> named.stream().forEach(holders::add));
            }
            for (String entityId : entityIds) {
                Identifier rl = tryParse(entityId);
                if (rl != null) {
                    lookup.get(ResourceKey.create(Registries.ENTITY_TYPE, rl)).ifPresent(holders::add);
                }
            }
        }
        if (holders.isEmpty()) {
            this.allowedEntitiesEntry.setValid(false);
            return false;
        }
        this.allowedEntitiesEntry.setValid(true);
        builder.setAllowedEntities(HolderSet.direct(holders));
        return true;
    }

    private Optional<HolderSet<EntityType<?>>> resolveNamedEntityTypeTag(HolderLookup.RegistryLookup<EntityType<?>> lookup, String tagId) {
        Identifier rl = tryParse(tagId);
        if (rl == null) {
            return Optional.empty();
        }
        return lookup.get(TagKey.create(Registries.ENTITY_TYPE, rl)).map(named -> (HolderSet<EntityType<?>>) named);
    }

    private void setAllowedEntitiesRaw(String value) {
        this.allowedEntitiesRaw = value == null ? "" : value.trim();
    }

    private void openAllowedEntitySelection() {
        Set<Identifier> initiallySelected = extractEntityIds(this.allowedEntitiesRaw);
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_entity"), "equippable_allowed_entities", ClientCache.getEntitySelectionItems(), null, true, selected -> {
            List<String> entries = new ArrayList<>();
            for (String entry : parseIdentifierList(this.allowedEntitiesRaw)) {
                if (entry.startsWith("#")) {
                    entries.add(entry);
                }
            }
            for (Identifier rl : selected) {
                entries.add(rl.toString());
            }
            String joined = String.join(", ", entries).trim();
            this.allowedEntitiesRaw = joined;
            this.allowedEntitiesEntry.setValue(joined);
        }, initiallySelected);
    }

    private void openAllowedEntityTagSelection() {
        Set<Identifier> initiallySelected = extractTagIds(this.allowedEntitiesRaw);
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_tag"), "equippable_allowed_entity_tags", ClientCache.getEntityTypeTagSelectionItems(), null, true, selected -> {
            List<String> entries = new ArrayList<>();
            for (String entry : parseIdentifierList(this.allowedEntitiesRaw)) {
                if (!entry.startsWith("#")) {
                    entries.add(entry);
                }
            }
            for (Identifier rl : selected) {
                entries.add("#" + rl);
            }
            String joined = String.join(", ", entries).trim();
            this.allowedEntitiesRaw = joined;
            this.allowedEntitiesEntry.setValue(joined);
        }, initiallySelected);
    }

    private List<String> parseIdentifierList(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        String[] parts = raw.split("[,\\n]");
        List<String> out = new ArrayList<>();
        for (String part : parts) {
            String trimmed = stripQuotes(part.trim());
            if (!trimmed.isEmpty()) {
                out.add(trimmed);
            }
        }
        return out;
    }

    private Set<Identifier> extractEntityIds(String raw) {
        Set<Identifier> set = new LinkedHashSet<>();
        for (String entry : parseIdentifierList(raw)) {
            if (!entry.startsWith("#")) {
                Identifier rl = tryParse(entry);
                if (rl != null) {
                    set.add(rl);
                }
            }
        }
        return set;
    }

    private Set<Identifier> extractTagIds(String raw) {
        Set<Identifier> set = new LinkedHashSet<>();
        for (String entry : parseIdentifierList(raw)) {
            String tagId = entry.startsWith("#") ? entry.substring(1) : null;
            if (tagId != null && !tagId.isBlank()) {
                Identifier rl = tryParse(tagId);
                if (rl != null) {
                    set.add(rl);
                }
            }
        }
        return set;
    }

    private String serializeAllowedEntities(HolderSet<EntityType<?>> set) {
        if (set instanceof HolderSet.Named<EntityType<?>> named) {
            return "#" + named.key().location();
        }
        return set.stream().map(holder -> holder.unwrapKey().map(key -> key.identifier().toString()).orElse("")).filter(s -> !s.isBlank()).reduce((a, b) -> a + ", " + b).orElse("");
    }

    private void applyTrimComponent(ItemStack stack) {
        HolderLookup.RegistryLookup<TrimPattern> patternLookup = ClientUtil.registryAccess().lookup(Registries.TRIM_PATTERN).orElse(null);
        HolderLookup.RegistryLookup<TrimMaterial> materialLookup = ClientUtil.registryAccess().lookup(Registries.TRIM_MATERIAL).orElse(null);
        Optional<Holder.Reference<TrimPattern>> patternHolder = resolvePattern(patternLookup, this.patternId);
        Optional<Holder.Reference<TrimMaterial>> materialHolder = resolveMaterial(materialLookup, this.materialId);
        if (patternHolder.isEmpty() || materialHolder.isEmpty()) {
            this.patternEntry.setValid(patternHolder.isPresent());
            this.materialEntry.setValid(materialHolder.isPresent());
            return;
        }
        this.patternEntry.setValid(true);
        this.materialEntry.setValid(true);
        stack.set(DataComponents.TRIM, new ArmorTrim(materialHolder.get(), patternHolder.get()));
        CompoundTag data = getData();
        if (data != null) {
            CompoundTag components = data.getCompound("components").orElse(null);
            if (components != null) {
                components.remove("minecraft:trim");
                if (components.isEmpty()) {
                    data.remove("components");
                }
            }
        }
    }

    private Optional<Holder.Reference<TrimPattern>> resolvePattern(HolderLookup.RegistryLookup<TrimPattern> lookup, String id) {
        if (lookup == null) {
            return Optional.empty();
        }
        Identifier rl = id.isBlank() ? null : Identifier.tryParse(id);
        if (rl == null) {
            return Optional.empty();
        }
        return lookup.get(ResourceKey.create(Registries.TRIM_PATTERN, rl));
    }

    private Optional<Holder.Reference<TrimMaterial>> resolveMaterial(HolderLookup.RegistryLookup<TrimMaterial> lookup, String id) {
        if (lookup == null) {
            return Optional.empty();
        }
        Identifier rl = id.isBlank() ? null : Identifier.tryParse(id);
        if (rl == null) {
            return Optional.empty();
        }
        return lookup.get(ResourceKey.create(Registries.TRIM_MATERIAL, rl));
    }

    private void loadStateFromStack() {
        ItemStack stack = getParent().getContext().getItemStack();
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            this.slot = EquipmentSlot.HEAD;
            this.equipSoundId = "";
            this.shearingSoundId = "";
            this.assetId = "";
            this.cameraOverlayId = "";
            this.dispensable = true;
            this.swappable = true;
            this.damageOnHurt = true;
            this.equipOnInteract = false;
            this.canBeSheared = false;
            this.allowedEntitiesRaw = "";
            return;
        }
        this.slot = equippable.slot();
        this.equipSoundId = holderId(equippable.equipSound());
        this.shearingSoundId = holderId(equippable.shearingSound());
        this.assetId = (String) equippable.assetId().map(value -> value.identifier()).map(value -> value.toString()).orElse("");
        this.cameraOverlayId = (String) equippable.cameraOverlay().map(value -> value.toString()).orElse("");
        this.dispensable = equippable.dispensable();
        this.swappable = equippable.swappable();
        this.damageOnHurt = equippable.damageOnHurt();
        this.equipOnInteract = equippable.equipOnInteract();
        this.canBeSheared = equippable.canBeSheared();
        this.allowedEntitiesRaw = equippable.allowedEntities().map(this::serializeAllowedEntities).orElse("");
    }

    private void setEquipSoundId(String id) {
        this.equipSoundId = sanitizeId(id);
    }

    private void setShearingSoundId(String id) {
        this.shearingSoundId = sanitizeId(id);
    }

    private void setAssetId(String id) {
        this.assetId = sanitizeId(id);
    }

    private void setCameraOverlayId(String id) {
        this.cameraOverlayId = sanitizeId(id);
    }

    private Optional<ResourceKey<EquipmentAsset>> parseEquipmentAsset() {
        String value = sanitizeId(this.assetId);
        if (value.isEmpty()) {
            return Optional.empty();
        }
        Identifier location = ClientUtil.parseResourceLocation(value);
        if (location == null) {
            return Optional.empty();
        }
        return Optional.of(ResourceKey.create(EquipmentAssets.ROOT_ID, location));
    }

    private Optional<Identifier> parseCameraOverlay() {
        String value = sanitizeId(this.cameraOverlayId);
        if (value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ClientUtil.parseResourceLocation(value));
    }

    private static Optional<Holder<SoundEvent>> resolveSound(HolderLookup.RegistryLookup<SoundEvent> lookup, String id) {
        String value = sanitizeId(id);
        if (value.isEmpty()) {
            return Optional.empty();
        }
        if (lookup == null) {
            return Optional.empty();
        }
        Identifier location = ClientUtil.parseResourceLocation(value);
        if (location == null) {
            return Optional.empty();
        }
        ResourceKey<SoundEvent> key = ResourceKey.create(Registries.SOUND_EVENT, location);
        return lookup.get(key).map(holder -> holder);
    }

    private static String holderId(Holder<SoundEvent> holder) {
        return holder.unwrapKey().map(key -> key.identifier().toString()).orElse("");
    }

    private static String sanitizeId(String id) {
        return id == null ? "" : id.trim();
    }

    private String stripQuotes(String value) {
        if (value == null) {
            return "";
        }
        String result = value.trim();
        if (((result.startsWith("\"") && result.endsWith("\"")) || (result.startsWith("'") && result.endsWith("'"))) && result.length() >= 2) {
            result = result.substring(1, result.length() - 1);
        }
        return result.trim();
    }

    private Identifier tryParse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String sanitized = value.trim();
        try {
            return Identifier.parse(sanitized);
        } catch (Exception e) {
            if (!sanitized.contains(":")) {
                try {
                    return Identifier.parse("minecraft:" + sanitized);
                } catch (Exception ignored) {
                    return null;
                }
            }
            return null;
        }
    }

    private String namespaceFilter(String id) {
        String trimmed = sanitizeId(id);
        if (trimmed.isEmpty()) {
            return null;
        }
        String namespace = trimmed.contains(":") ? trimmed.substring(0, trimmed.indexOf(58)) : "minecraft";
        return "namespace:" + namespace.toLowerCase(Locale.ROOT);
    }

    private <T extends LabeledEntryModel> T withWikiTooltip(T entry, String key, int lines) {
        if (entry == null || lines <= 0) {
            return entry;
        }
        MutableComponent[] tooltipLines = ModTexts.wikiTooltip(key, lines);
        for (int i = 0; i < tooltipLines.length; i++) {
            tooltipLines[i] = tooltipLines[i].copy().withStyle(ChatFormatting.GRAY);
        }
        entry.setLabelTooltip(tooltipLines);
        return entry;
    }
}

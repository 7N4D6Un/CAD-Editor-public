package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.util.NbtHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.component.UseEffects;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.level.block.Block;

public class ItemUseBehaviorCategoryModel extends ItemEditorCategoryModel {
    private final BlockListSection canPlaceOn = new BlockListSection(DataComponents.CAN_PLACE_ON, "minecraft:can_place_on", "CanPlaceOn", ModTexts.CAN_PLACE_ON);
    private final BlockListSection canBreak = new BlockListSection(DataComponents.CAN_BREAK, "minecraft:can_break", "CanDestroy", ModTexts.CAN_DESTROY);
    private BooleanEntryModel useCooldownToggleEntry;
    private StringEntryModel useCooldownGroupEntry;
    private FloatEntryModel useCooldownSecondsEntry;
    private boolean useCooldownEnabled;
    private String useCooldownGroupId;
    private float useCooldownSeconds;
    private BooleanEntryModel useEffectsToggleEntry;
    private BooleanEntryModel useEffectsCanSprintEntry;
    private BooleanEntryModel useEffectsInteractVibrationsEntry;
    private FloatEntryModel useEffectsSpeedMultiplierEntry;
    private boolean useEffectsEnabled;
    private boolean useEffectsCanSprint;
    private boolean useEffectsInteractVibrations;
    private float useEffectsSpeedMultiplier;

    public ItemUseBehaviorCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("item_use_behavior"), editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        CompoundTag data = getData();
        UseCooldown cooldown = stack.get(DataComponents.USE_COOLDOWN);
        this.useCooldownEnabled = cooldown != null;
        this.useCooldownGroupId = cooldown != null && cooldown.cooldownGroup().isPresent() ? cooldown.cooldownGroup().get().toString() : "";
        this.useCooldownSeconds = cooldown != null ? cooldown.seconds() : 1.0f;
        this.useCooldownToggleEntry = new BooleanEntryModel(this, ModTexts.gui("use_cooldown_enabled"), this.useCooldownEnabled, value -> setUseCooldownEnabled(value));
        getEntries().add(this.useCooldownToggleEntry);
        this.useCooldownGroupEntry = new StringEntryModel(this, ModTexts.gui("use_cooldown_group"), this.useCooldownGroupId, this::setUseCooldownGroupId);
        this.useCooldownGroupEntry.setPlaceholder("namespace:id (可留空)");
        getEntries().add(this.useCooldownGroupEntry);
        this.useCooldownSecondsEntry = new FloatEntryModel(this, ModTexts.gui("use_cooldown_seconds"), this.useCooldownSeconds, this::setUseCooldownSeconds, value -> value != null && value > 0.0f);
        getEntries().add(this.useCooldownSecondsEntry);
        getEntries().add(new SpacerEntryModel(this));
        UseEffects useEffects = stack.get(DataComponents.USE_EFFECTS);
        this.useEffectsEnabled = useEffects != null;
        this.useEffectsCanSprint = useEffects != null ? useEffects.canSprint() : UseEffects.DEFAULT.canSprint();
        this.useEffectsInteractVibrations = useEffects != null ? useEffects.interactVibrations() : UseEffects.DEFAULT.interactVibrations();
        this.useEffectsSpeedMultiplier = useEffects != null ? useEffects.speedMultiplier() : UseEffects.DEFAULT.speedMultiplier();
        this.useEffectsToggleEntry = new BooleanEntryModel(this, ModTexts.gui("use_partial_behavior_enabled"), this.useEffectsEnabled, value -> setUseEffectsEnabled(value));
        getEntries().add(this.useEffectsToggleEntry);
        this.useEffectsInteractVibrationsEntry = new BooleanEntryModel(this, ModTexts.gui("use_effects_interact_vibrations"), this.useEffectsInteractVibrations, value -> setUseEffectsInteractVibrations(value));
        getEntries().add(this.useEffectsInteractVibrationsEntry);
        this.useEffectsCanSprintEntry = new BooleanEntryModel(this, ModTexts.gui("use_effects_can_sprint"), this.useEffectsCanSprint, value -> setUseEffectsCanSprint(value));
        getEntries().add(this.useEffectsCanSprintEntry);
        this.useEffectsSpeedMultiplierEntry = new FloatEntryModel(this, ModTexts.gui("use_effects_speed_multiplier"), this.useEffectsSpeedMultiplier, this::setUseEffectsSpeedMultiplier, value -> value != null && value > 0.0f);
        getEntries().add(this.useEffectsSpeedMultiplierEntry);
        getEntries().add(new SpacerEntryModel(this));
        this.canPlaceOn.setup(data);
        this.canBreak.setup(data);
        syncCooldownEntriesEnabled();
        syncUseEffectsEntriesEnabled();
    }

    private void syncCooldownEntriesEnabled() {
        this.useCooldownGroupEntry.setEnabled(this.useCooldownEnabled);
        this.useCooldownSecondsEntry.setEnabled(this.useCooldownEnabled);
    }

    private void syncUseEffectsEntriesEnabled() {
        this.useEffectsInteractVibrationsEntry.setEnabled(this.useEffectsEnabled);
        this.useEffectsCanSprintEntry.setEnabled(this.useEffectsEnabled);
        this.useEffectsSpeedMultiplierEntry.setEnabled(this.useEffectsEnabled);
    }

    private void setUseCooldownEnabled(boolean value) {
        this.useCooldownEnabled = value;
        syncCooldownEntriesEnabled();
        applyUseCooldownComponent();
    }

    private void setUseCooldownGroupId(String value) {
        this.useCooldownGroupId = value == null ? "" : value.trim();
        if (this.useCooldownEnabled) {
            applyUseCooldownComponent();
        }
    }

    private void setUseCooldownSeconds(Float value) {
        this.useCooldownSeconds = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.useCooldownEnabled) {
            applyUseCooldownComponent();
        }
    }

    private void setUseEffectsEnabled(boolean value) {
        this.useEffectsEnabled = value;
        syncUseEffectsEntriesEnabled();
        applyUseEffectsComponent();
    }

    private void setUseEffectsCanSprint(boolean value) {
        this.useEffectsCanSprint = value;
        if (this.useEffectsEnabled) {
            applyUseEffectsComponent();
        }
    }

    private void setUseEffectsInteractVibrations(boolean value) {
        this.useEffectsInteractVibrations = value;
        if (this.useEffectsEnabled) {
            applyUseEffectsComponent();
        }
    }

    private void setUseEffectsSpeedMultiplier(Float value) {
        this.useEffectsSpeedMultiplier = value == null ? UseEffects.DEFAULT.speedMultiplier() : value;
        if (this.useEffectsEnabled) {
            applyUseEffectsComponent();
        }
    }

    private void applyUseCooldownComponent() {
        Identifier parsed;
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.useCooldownEnabled || this.useCooldownSeconds <= 0.0f) {
            stack.remove(DataComponents.USE_COOLDOWN);
            getParent().removeComponentFromDataTag("minecraft:use_cooldown");
            return;
        }
        Optional<Identifier> group = Optional.empty();
        String sanitized = sanitizeId(this.useCooldownGroupId);
        if (!sanitized.isBlank() && (parsed = tryParse(sanitized)) != null) {
            group = Optional.of(parsed);
        }
        UseCooldown useCooldown;
        if (group.isPresent()) {
            useCooldown = new UseCooldown(this.useCooldownSeconds, group);
        } else {
            useCooldown = new UseCooldown(this.useCooldownSeconds);
        }
        stack.set(DataComponents.USE_COOLDOWN, useCooldown);
    }

    private void applyUseEffectsComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.useEffectsEnabled) {
            stack.remove(DataComponents.USE_EFFECTS);
            getParent().removeComponentFromDataTag("minecraft:use_effects");
            if (this.useEffectsSpeedMultiplierEntry != null) {
                this.useEffectsSpeedMultiplierEntry.setValid(true);
                return;
            }
            return;
        }
        if (this.useEffectsSpeedMultiplierEntry != null) {
            this.useEffectsSpeedMultiplierEntry.setValid(this.useEffectsSpeedMultiplier > 0.0f);
        }
        if (this.useEffectsSpeedMultiplier <= 0.0f) {
            return;
        }
        stack.set(DataComponents.USE_EFFECTS, new UseEffects(this.useEffectsCanSprint, this.useEffectsInteractVibrations, this.useEffectsSpeedMultiplier));
    }

    private String sanitizeId(String id) {
        return id == null ? "" : stripQuotes(id.trim());
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

    private static void collectComponentBlocks(Tag blocksTag, List<String> out) {
        if (blocksTag == null) {
            return;
        }
        if (blocksTag instanceof StringTag stringTag) {
            out.add(stringTag.value());
            return;
        }
        if (blocksTag instanceof ListTag listTag) {
            for (Tag element : listTag) {
                collectComponentBlocks(element, out);
            }
            return;
        }
        if (blocksTag instanceof CompoundTag compoundTag) {
            if (compoundTag.contains("id")) {
                out.add(NbtHelper.getString(compoundTag, "id", ""));
            }
            if (compoundTag.contains("tag")) {
                out.add("#" + NbtHelper.getString(compoundTag, "tag", ""));
            }
            if (compoundTag.contains("blocks")) {
                collectComponentBlocks(compoundTag.get("blocks"), out);
            }
        }
    }

    private final class BlockListSection {
        private final DataComponentType<AdventureModePredicate> componentType;
        private final String componentKey;
        private final String legacyTagName;
        private final MutableComponent label;
        private StringWithActionsEntryModel entry;

        private BlockListSection(DataComponentType<AdventureModePredicate> componentType, String componentKey,
                                 String legacyTagName, MutableComponent label) {
            this.componentType = componentType;
            this.componentKey = componentKey;
            this.legacyTagName = legacyTagName;
            this.label = label;
        }

        private void setup(CompoundTag data) {
            List<String> entries = new ArrayList<>();
            CompoundTag comp;
            ListTag preds;
            if (data != null) {
                CompoundTag components = data.getCompound("components").orElse(null);
                if (components != null && (comp = components.getCompound(this.componentKey).orElse(null)) != null && (preds = comp.getList("predicates").orElse(null)) != null) {
                    boolean wildcard = false;
                    List<String> collected = new ArrayList<>();
                    for (Tag predicateTag : preds) {
                        if (predicateTag instanceof CompoundTag predicate) {
                            if (predicate.contains("blocks")) {
                                collectComponentBlocks(predicate.get("blocks"), collected);
                            } else {
                                wildcard = true;
                            }
                        }
                    }
                    if (wildcard) {
                        entries.add("*");
                    } else {
                        entries.addAll(collected);
                    }
                }
            }
            if (entries.isEmpty()) {
                CompoundTag legacyTag = data == null ? null : data.getCompound("tag").orElse(null);
                ListTag nbtList = NbtHelper.getListOrEmpty(legacyTag, this.legacyTagName);
                for (Tag element : nbtList) {
                    if (element instanceof StringTag stringTag) {
                        entries.add(stringTag.value());
                    }
                }
            }
            String raw = String.join(", ", entries);
            this.entry = new StringWithActionsEntryModel(ItemUseBehaviorCategoryModel.this, this.label, raw, v -> {
            });
            this.entry.setPlaceholder("minecraft:stone, #minecraft:logs，留空=任意方块");
            this.entry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.choose(ModTexts.BLOCK), this::openBlockSelection));
            this.entry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.choose(ModTexts.gui("select_tag")), this::openTagSelection));
            getEntries().add(this.entry);
        }

        private void openBlockSelection() {
            Set<Identifier> initiallySelected = extractIds(this.entry.getValue(), false);
            ModScreenHandler.openListSelectionScreen(ModTexts.BLOCK, "block_list_blocks", ClientCache.getBlockSelectionItems(), null, true, selected -> {
                List<String> entries = new ArrayList<>();
                for (String entry : parseIdentifierList(this.entry.getValue())) {
                    if (entry.startsWith("#") || "*".equals(entry)) {
                        entries.add(entry);
                    }
                }
                for (Identifier rl : selected) {
                    entries.add(rl.toString());
                }
                String joined = String.join(", ", entries).trim();
                this.entry.setValue(joined);
            }, initiallySelected);
        }

        private void openTagSelection() {
            Set<Identifier> initiallySelected = extractIds(this.entry.getValue(), true);
            ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_tag"), "block_list_tags", ClientCache.getBlockTagSelectionItems(), null, true, selected -> {
                List<String> entries = new ArrayList<>();
                for (String entry : parseIdentifierList(this.entry.getValue())) {
                    if (!entry.startsWith("#") || "*".equals(entry)) {
                        entries.add(entry);
                    }
                }
                for (Identifier rl : selected) {
                    entries.add("#" + rl);
                }
                String joined = String.join(", ", entries).trim();
                this.entry.setValue(joined);
            }, initiallySelected);
        }

        private void apply(HolderLookup.RegistryLookup<Block> lookup) {
            if (this.entry == null) {
                return;
            }
            ItemStack stack = getParent().getContext().getItemStack();
            List<BlockPredicate> predicates = new ArrayList<>();
            boolean anyInvalid = false;
            boolean wildcard = false;
            List<String> tagIds = new ArrayList<>();
            List<String> blockIds = new ArrayList<>();
            for (String entry : parseIdentifierList(this.entry.getValue())) {
                if ("*".equals(entry)) {
                    wildcard = true;
                    continue;
                }
                if (entry.startsWith("#")) {
                    String tagId = entry.substring(1);
                    if (tagId.isBlank() || tryParse(tagId) == null) {
                        anyInvalid = true;
                    } else {
                        tagIds.add(tagId);
                    }
                } else if (tryParse(entry) == null) {
                    anyInvalid = true;
                } else {
                    blockIds.add(entry);
                }
            }
            for (String tagId : tagIds) {
                Identifier tagIdentifier = tryParse(tagId);
                if (tagIdentifier != null) {
                    TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, tagIdentifier);
                    lookup.get(tagKey).ifPresent(named -> predicates.add(new BlockPredicate(Optional.of(named), Optional.empty(), Optional.empty(), DataComponentMatchers.ANY)));
                }
            }
            List<Holder<Block>> holders = new ArrayList<>();
            for (String blockId : blockIds) {
                Identifier rl = tryParse(blockId);
                if (rl != null) {
                    lookup.get(ResourceKey.create(Registries.BLOCK, rl)).ifPresent(holders::add);
                }
            }
            if (!holders.isEmpty()) {
                predicates.add(new BlockPredicate(Optional.of(HolderSet.direct(holders)), Optional.empty(), Optional.empty(), DataComponentMatchers.ANY));
            }
            this.entry.setValid(!anyInvalid);
            if (anyInvalid) {
                return;
            }
            if (wildcard) {
                predicates.add(0, new BlockPredicate(Optional.empty(), Optional.empty(), Optional.empty(), DataComponentMatchers.ANY));
            }
            if (predicates.isEmpty()) {
                stack.remove(this.componentType);
            } else {
                stack.set(this.componentType, new AdventureModePredicate(predicates));
            }
            removeLegacyTag();
        }

        private void removeLegacyTag() {
            CompoundTag data = getData();
            CompoundTag legacyTag;
            if (data != null && (legacyTag = data.getCompound("tag").orElse(null)) != null && legacyTag.contains(this.legacyTagName)) {
                legacyTag.remove(this.legacyTagName);
                if (legacyTag.isEmpty()) {
                    data.remove("tag");
                }
            }
        }
    }

    private static List<String> parseIdentifierList(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        String[] parts = raw.split("[,\\n]");
        List<String> out = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                out.add(trimmed);
            }
        }
        return out;
    }

    private static Set<Identifier> extractIds(String raw, boolean tags) {
        Set<Identifier> set = new LinkedHashSet<>();
        for (String entry : parseIdentifierList(raw)) {
            boolean isTag = entry.startsWith("#");
            if (isTag != tags) {
                continue;
            }
            String id = isTag ? entry.substring(1) : entry;
            if (id.isBlank()) {
                continue;
            }
            try {
                set.add(Identifier.parse(id.contains(":") ? id : "minecraft:" + id));
            } catch (Exception ignored) {
            }
        }
        return set;
    }

    @Override
    public void apply() {
        super.apply();
        Optional<? extends HolderLookup.RegistryLookup<Block>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.BLOCK);
        this.canPlaceOn.apply(lookupOpt.orElse(null));
        this.canBreak.apply(lookupOpt.orElse(null));
    }
}

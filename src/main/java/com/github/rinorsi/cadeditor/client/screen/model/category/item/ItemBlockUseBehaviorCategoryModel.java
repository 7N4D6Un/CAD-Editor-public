package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.ContextFloatProviderSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.ContextIntProviderSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.JukeboxSongSelectionEntryModel;
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
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.component.BrewingFuel;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

public class ItemBlockUseBehaviorCategoryModel extends ItemEditorCategoryModel {
    private final BlockListSection canPlaceOn = new BlockListSection(DataComponents.CAN_PLACE_ON, "minecraft:can_place_on", "CanPlaceOn", ModTexts.CAN_PLACE_ON);
    private final BlockListSection canBreak = new BlockListSection(DataComponents.CAN_BREAK, "minecraft:can_break", "CanDestroy", ModTexts.CAN_DESTROY);
    private JukeboxSongSelectionEntryModel jukeboxSongEntry;
    private String jukeboxSongId;
    private BooleanEntryModel compostableToggle;
    private ContextIntProviderSelectionEntryModel compostableLayersEntry;
    private boolean compostableEnabled;
    private String compostableLayersText;
    private BooleanEntryModel cookingFuelToggle;
    private ContextIntProviderSelectionEntryModel cookingBurnTimeEntry;
    private ContextFloatProviderSelectionEntryModel cookingSpeedEntry;
    private boolean cookingFuelEnabled;
    private String cookingBurnTimeText;
    private String cookingSpeedText;
    private BooleanEntryModel brewingFuelToggle;
    private ContextIntProviderSelectionEntryModel brewingUsesEntry;
    private ContextFloatProviderSelectionEntryModel brewingSpeedEntry;
    private boolean brewingFuelEnabled;
    private String brewingUsesText;
    private String brewingSpeedText;

    public ItemBlockUseBehaviorCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("block_interaction_behavior"), editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        JukeboxPlayable jukeboxPlayable = stack.get(DataComponents.JUKEBOX_PLAYABLE);
        this.jukeboxSongId = jukeboxPlayable != null ? jukeboxPlayable.song().unwrapKey().map(key -> key.identifier().toString()).orElse("") : "";
        this.jukeboxSongEntry = new JukeboxSongSelectionEntryModel(this, this.jukeboxSongId, this::setJukeboxSongId);
        getEntries().add(this.jukeboxSongEntry);
        getEntries().add(new SpacerEntryModel(this));
        CompoundTag data = getData();
        this.canPlaceOn.setup(data);
        this.canBreak.setup(data);
        getEntries().add(new SpacerEntryModel(this));
        Compostable compostable = stack.get(DataComponents.COMPOSTABLE);
        this.compostableEnabled = compostable != null;
        this.compostableLayersText = serializeResolvableInt(compostable != null ? compostable.layers() : null, "");
        this.compostableToggle = new BooleanEntryModel(this, ModTexts.gui("compostable_enabled"), this.compostableEnabled, value -> {
            this.compostableEnabled = value != null && value;
            if (this.compostableEnabled && this.compostableLayersText.isBlank()) {
                this.compostableLayersText = "1";
                this.compostableLayersEntry.setValue("1");
            }
            syncCompostableEntriesEnabled();
            applyCompostableComponent();
        });
        getEntries().add(this.compostableToggle);
        this.compostableLayersEntry = new ContextIntProviderSelectionEntryModel(this, ModTexts.gui("compostable_layers"), this.compostableLayersText, this::setCompostableLayersText);
        this.compostableLayersEntry.setValidator(value -> !this.compostableEnabled || parseCompostableLayers(value).isPresent());
        getEntries().add(this.compostableLayersEntry);
        syncCompostableEntriesEnabled();
        getEntries().add(new SpacerEntryModel(this));
        CookingFuel cookingFuel = stack.get(DataComponents.COOKING_FUEL);
        this.cookingFuelEnabled = cookingFuel != null;
        this.cookingBurnTimeText = serializeResolvableInt(cookingFuel != null ? cookingFuel.burnTime() : null, "");
        this.cookingSpeedText = serializeResolvableFloat(cookingFuel != null ? cookingFuel.speedMultiplier() : null, "");
        this.cookingFuelToggle = new BooleanEntryModel(this, ModTexts.gui("cooking_fuel_enabled"), this.cookingFuelEnabled, value -> {
            this.cookingFuelEnabled = value != null && value;
            if (this.cookingFuelEnabled) {
                if (this.cookingBurnTimeText.isBlank()) {
                    this.cookingBurnTimeText = "1600";
                    this.cookingBurnTimeEntry.setValue("1600");
                }
                if (this.cookingSpeedText.isBlank()) {
                    this.cookingSpeedText = "1.0";
                    this.cookingSpeedEntry.setValue("1.0");
                }
            }
            syncCookingFuelEntriesEnabled();
            applyCookingFuelComponent();
        });
        getEntries().add(this.cookingFuelToggle);
        this.cookingBurnTimeEntry = new ContextIntProviderSelectionEntryModel(this, ModTexts.gui("burn_time"), this.cookingBurnTimeText, this::setCookingBurnTimeText);
        this.cookingBurnTimeEntry.setValidator(value -> !this.cookingFuelEnabled || parseResolvableInt(value).isPresent());
        getEntries().add(this.cookingBurnTimeEntry);
        this.cookingSpeedEntry = new ContextFloatProviderSelectionEntryModel(this, ModTexts.gui("speed_multiplier"), this.cookingSpeedText, this::setCookingSpeedText);
        this.cookingSpeedEntry.setValidator(value -> !this.cookingFuelEnabled || parseResolvableFloat(value).isPresent());
        getEntries().add(this.cookingSpeedEntry);
        syncCookingFuelEntriesEnabled();
        getEntries().add(new SpacerEntryModel(this));
        BrewingFuel brewingFuel = stack.get(DataComponents.BREWING_FUEL);
        this.brewingFuelEnabled = brewingFuel != null;
        this.brewingUsesText = serializeResolvableInt(brewingFuel != null ? brewingFuel.uses() : null, "");
        this.brewingSpeedText = serializeResolvableFloat(brewingFuel != null ? brewingFuel.speedMultiplier() : null, "");
        this.brewingFuelToggle = new BooleanEntryModel(this, ModTexts.gui("brewing_fuel_enabled"), this.brewingFuelEnabled, value -> {
            this.brewingFuelEnabled = value != null && value;
            if (this.brewingFuelEnabled) {
                if (this.brewingUsesText.isBlank()) {
                    this.brewingUsesText = "20";
                    this.brewingUsesEntry.setValue("20");
                }
                if (this.brewingSpeedText.isBlank()) {
                    this.brewingSpeedText = "1.0";
                    this.brewingSpeedEntry.setValue("1.0");
                }
            }
            syncBrewingFuelEntriesEnabled();
            applyBrewingFuelComponent();
        });
        getEntries().add(this.brewingFuelToggle);
        this.brewingUsesEntry = new ContextIntProviderSelectionEntryModel(this, ModTexts.gui("brewing_uses"), this.brewingUsesText, this::setBrewingUsesText);
        this.brewingUsesEntry.setValidator(value -> !this.brewingFuelEnabled || parseResolvableInt(value).isPresent());
        getEntries().add(this.brewingUsesEntry);
        this.brewingSpeedEntry = new ContextFloatProviderSelectionEntryModel(this, ModTexts.gui("speed_multiplier"), this.brewingSpeedText, this::setBrewingSpeedText);
        this.brewingSpeedEntry.setValidator(value -> !this.brewingFuelEnabled || parseResolvableFloat(value).isPresent());
        getEntries().add(this.brewingSpeedEntry);
        syncBrewingFuelEntriesEnabled();
    }

    private void setJukeboxSongId(String value) {
        this.jukeboxSongId = value == null ? "" : value.trim();
        applyJukeboxPlayableComponent();
    }

    private void applyJukeboxPlayableComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (this.jukeboxSongId.isBlank()) {
            stack.remove(DataComponents.JUKEBOX_PLAYABLE);
            getParent().removeComponentFromDataTag("minecraft:jukebox_playable");
            return;
        }
        Identifier parsed = tryParse(this.jukeboxSongId);
        if (parsed == null) {
            stack.remove(DataComponents.JUKEBOX_PLAYABLE);
            return;
        }
        HolderLookup.RegistryLookup<JukeboxSong> lookup = ClientUtil.registryAccess()
                .lookup(Registries.JUKEBOX_SONG)
                .orElse(null);
        if (lookup == null) {
            return;
        }
        ResourceKey<JukeboxSong> key = ResourceKey.create(Registries.JUKEBOX_SONG, parsed);
        Optional<Holder.Reference<JukeboxSong>> holder = lookup.get(key);
        if (holder.isPresent()) {
            stack.set(DataComponents.JUKEBOX_PLAYABLE, new JukeboxPlayable(holder.get()));
        } else {
            stack.remove(DataComponents.JUKEBOX_PLAYABLE);
        }
    }

    private static String serializeResolvableInt(ResolvableInt value, String fallback) {
        if (value instanceof ResolvableInt.Constant constant) {
            return Integer.toString(constant.value());
        }
        if (value instanceof ResolvableInt.Reference reference) {
            return reference.key().identifier().toString();
        }
        return fallback;
    }

    private static String serializeResolvableFloat(ResolvableFloat value, String fallback) {
        if (value instanceof ResolvableFloat.Constant constant) {
            return Float.toString(constant.value());
        }
        if (value instanceof ResolvableFloat.Reference reference) {
            return reference.key().identifier().toString();
        }
        return fallback;
    }

    private void setCompostableLayersText(String value) {
        this.compostableLayersText = value == null ? "" : value.trim();
        if (this.compostableEnabled) {
            applyCompostableComponent();
        }
    }

    private void syncCompostableEntriesEnabled() {
        this.compostableLayersEntry.setEnabled(this.compostableEnabled);
    }

    private void applyCompostableComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.compostableEnabled) {
            this.compostableLayersEntry.setValid(true);
            stack.remove(DataComponents.COMPOSTABLE);
            getParent().removeComponentFromDataTag("minecraft:compostable");
            return;
        }
        Optional<ResolvableInt> parsed = parseCompostableLayers(this.compostableLayersText);
        if (parsed.isEmpty()) {
            this.compostableLayersEntry.setValid(false);
            return;
        }
        this.compostableLayersEntry.setValid(true);
        stack.set(DataComponents.COMPOSTABLE, new Compostable(parsed.get()));
    }

    private Optional<ResolvableInt> parseCompostableLayers(String text) {
        Optional<ResolvableInt> parsed = parseResolvableInt(text);
        if (parsed.isPresent() && parsed.get() instanceof ResolvableInt.Constant constant && (constant.value() < 0 || constant.value() > 7)) {
            return Optional.empty();
        }
        return parsed;
    }

    private Optional<ResolvableInt> parseResolvableInt(String text) {
        String sanitized = text == null ? "" : text.trim();
        if (sanitized.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(new ResolvableInt.Constant(Integer.parseInt(sanitized)));
        } catch (NumberFormatException ignored) {
        }
        Identifier rl = tryParseStrict(sanitized);
        if (rl == null) {
            return Optional.empty();
        }
        return Optional.of(new ResolvableInt.Reference(ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, rl)));
    }

    private Optional<ResolvableFloat> parseResolvableFloat(String text) {
        String sanitized = text == null ? "" : text.trim();
        if (sanitized.isBlank()) {
            return Optional.empty();
        }
        try {
            float value = Float.parseFloat(sanitized);
            if (Float.isFinite(value)) {
                return Optional.of(new ResolvableFloat.Constant(value));
            }
            return Optional.empty();
        } catch (NumberFormatException ignored) {
        }
        Identifier rl = tryParseStrict(sanitized);
        if (rl == null) {
            return Optional.empty();
        }
        return Optional.of(new ResolvableFloat.Reference(ResourceKey.create(Registries.CONTEXT_FLOAT_PROVIDER, rl)));
    }

    private void setCookingBurnTimeText(String value) {
        this.cookingBurnTimeText = value == null ? "" : value.trim();
        if (this.cookingFuelEnabled) {
            applyCookingFuelComponent();
        }
    }

    private void setCookingSpeedText(String value) {
        this.cookingSpeedText = value == null ? "" : value.trim();
        if (this.cookingFuelEnabled) {
            applyCookingFuelComponent();
        }
    }

    private void syncCookingFuelEntriesEnabled() {
        this.cookingBurnTimeEntry.setEnabled(this.cookingFuelEnabled);
        this.cookingSpeedEntry.setEnabled(this.cookingFuelEnabled);
    }

    private void applyCookingFuelComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.cookingFuelEnabled) {
            this.cookingBurnTimeEntry.setValid(true);
            this.cookingSpeedEntry.setValid(true);
            stack.remove(DataComponents.COOKING_FUEL);
            getParent().removeComponentFromDataTag("minecraft:cooking_fuel");
            return;
        }
        Optional<ResolvableInt> burnTime = parseResolvableInt(this.cookingBurnTimeText);
        Optional<ResolvableFloat> speed = parseResolvableFloat(this.cookingSpeedText);
        this.cookingBurnTimeEntry.setValid(burnTime.isPresent());
        this.cookingSpeedEntry.setValid(speed.isPresent());
        if (burnTime.isEmpty() || speed.isEmpty()) {
            return;
        }
        stack.set(DataComponents.COOKING_FUEL, new CookingFuel(burnTime.get(), speed.get()));
    }

    private void setBrewingUsesText(String value) {
        this.brewingUsesText = value == null ? "" : value.trim();
        if (this.brewingFuelEnabled) {
            applyBrewingFuelComponent();
        }
    }

    private void setBrewingSpeedText(String value) {
        this.brewingSpeedText = value == null ? "" : value.trim();
        if (this.brewingFuelEnabled) {
            applyBrewingFuelComponent();
        }
    }

    private void syncBrewingFuelEntriesEnabled() {
        this.brewingUsesEntry.setEnabled(this.brewingFuelEnabled);
        this.brewingSpeedEntry.setEnabled(this.brewingFuelEnabled);
    }

    private void applyBrewingFuelComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.brewingFuelEnabled) {
            this.brewingUsesEntry.setValid(true);
            this.brewingSpeedEntry.setValid(true);
            stack.remove(DataComponents.BREWING_FUEL);
            getParent().removeComponentFromDataTag("minecraft:brewing_fuel");
            return;
        }
        Optional<ResolvableInt> uses = parseResolvableInt(this.brewingUsesText);
        Optional<ResolvableFloat> speed = parseResolvableFloat(this.brewingSpeedText);
        this.brewingUsesEntry.setValid(uses.isPresent());
        this.brewingSpeedEntry.setValid(speed.isPresent());
        if (uses.isEmpty() || speed.isEmpty()) {
            return;
        }
        stack.set(DataComponents.BREWING_FUEL, new BrewingFuel(uses.get(), speed.get()));
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
            this.entry = new StringWithActionsEntryModel(ItemBlockUseBehaviorCategoryModel.this, this.label, raw, v -> {
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

    private Identifier tryParseStrict(String value) {
        String sanitized = value == null ? "" : value.trim();
        if (sanitized.isBlank() || !sanitized.contains(":")) {
            return null;
        }
        return Identifier.tryParse(sanitized);
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
}

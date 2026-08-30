package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.debug.DebugLog;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.ItemSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.LabeledEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.RaritySelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.SoundEventSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.JukeboxSongSelectionEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.Collections;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.enchantment.Repairable;


public class ItemGeneralCategoryModel extends ItemEditorCategoryModel {
    private StringWithActionsEntryModel damageResistantTypesEntry;
    private String damageResistantTypeId;
    private BooleanEntryModel customModelDataToggle;
    private StringEntryModel cmdFloatsEntry;
    private StringEntryModel cmdFlagsEntry;
    private StringEntryModel cmdStringsEntry;
    private StringEntryModel cmdColorsEntry;
    private boolean enableCustomModelData;
    private StringWithActionsEntryModel repairableItemsEntry;
    private String repairableItemsRaw;
    private JukeboxSongSelectionEntryModel jukeboxSongEntry;
    private String jukeboxSongId;
    private SoundEventSelectionEntryModel breakSoundEntry;
    private String breakSoundId;

    public ItemGeneralCategoryModel(ItemEditorModel editor) {
        super(ModTexts.GENERAL, editor);
    }

    @Override 
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        String currentId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        int currentCount = stack.getCount();
        getEntries().add(new ItemSelectionEntryModel(this, ModTexts.ITEM_ID, currentId, this::setItemId));
        getEntries().add(new IntegerEntryModel(this, ModTexts.COUNT, currentCount, value -> setCount(value)));
        getEntries().add(new IntegerEntryModel(this, ModTexts.MAX_STACK_SIZE, getMaxStackSizeValue(stack), value -> setMaxStackSize(value), value -> value.intValue() >= 0 && value.intValue() <= 99));
        getEntries().add(new RaritySelectionEntryModel(this, ModTexts.gui("rarity"), getRarityString(stack), this::setRarity));
        ItemSelectionEntryModel itemModelEntry = new ItemSelectionEntryModel(this, ModTexts.gui("item_model"), getItemModelId(stack), this::setItemModelId);
        itemModelEntry.setPlaceholder("minecraft:diamond");
        getEntries().add(itemModelEntry);
        StringEntryModel tooltipStyleEntry = new StringEntryModel(this, ModTexts.gui("tooltip_style"), getTooltipStyleId(stack), this::setTooltipStyleId);
        tooltipStyleEntry.setPlaceholder("minecraft:classic");
        getEntries().add(tooltipStyleEntry);
        JukeboxPlayable jukeboxPlayable = stack.get(DataComponents.JUKEBOX_PLAYABLE);
        this.jukeboxSongId = jukeboxPlayable != null ? jukeboxPlayable.song().unwrapKey().map(key -> key.identifier().toString()).orElse("") : "";
        this.jukeboxSongEntry = new JukeboxSongSelectionEntryModel(this, this.jukeboxSongId, this::setJukeboxSongId);
        getEntries().add(this.jukeboxSongEntry);
        getEntries().add(new SpacerEntryModel(this));
        int currentDamage = stack.isDamageableItem() ? stack.getDamageValue() : 0;
        boolean isUnbreakable = stack.has(DataComponents.UNBREAKABLE);
        getEntries().add(new IntegerEntryModel(this, ModTexts.DAMAGE, currentDamage, value -> setDamage(value)));
        getEntries().add(new IntegerEntryModel(this, ModTexts.MAX_DAMAGE, getMaxDamageValue(stack), value -> setMaxDamage(value), value -> value >= 0));
        getEntries().add(new BooleanEntryModel(this, ModTexts.UNBREAKABLE, isUnbreakable, value -> setUnbreakable(value)));
        getEntries().add(new IntegerEntryModel(this, ModTexts.gui("repair_cost"), getRepairCost(stack), value -> setRepairCost(value)));
        Repairable repairable = stack.get(DataComponents.REPAIRABLE);
        Repairable baseRepairable = stack.getItem().components().get(DataComponents.REPAIRABLE);
        this.repairableItemsRaw = repairable != null ? serializeRepairableItems(repairable) : (baseRepairable != null ? serializeRepairableItems(baseRepairable) : "");
        this.repairableItemsEntry = new StringWithActionsEntryModel(this, ModTexts.gui("repairable_items"), this.repairableItemsRaw, this::setRepairableItems);
        this.repairableItemsEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_item"), this::openRepairableItemSelection));
        this.repairableItemsEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_tag"), this::openRepairableTagSelection));
        getEntries().add(this.repairableItemsEntry);
        Holder<SoundEvent> breakSoundHolder = stack.get(DataComponents.BREAK_SOUND);
        this.breakSoundId = breakSoundHolder != null && breakSoundHolder.unwrapKey().isPresent() ? breakSoundHolder.unwrapKey().get().identifier().toString() : "";
        this.breakSoundEntry = new SoundEventSelectionEntryModel(this, ModTexts.gui("break_sound_value"), this.breakSoundId, this::setBreakSoundId, namespaceFilter(this.breakSoundId));
        getEntries().add(this.breakSoundEntry);
        DamageResistant damageResistant = stack.get(DataComponents.DAMAGE_RESISTANT);
        this.damageResistantTypeId = serializeDamageResistantTypes(damageResistant);
        this.damageResistantTypesEntry = withWikiTooltip(new StringWithActionsEntryModel(this, ModTexts.gui("damage_resistant_enabled"), this.damageResistantTypeId, this::setDamageResistantTypeId), "damage_resistant_enabled", 2);
        this.damageResistantTypesEntry.setPlaceholder("minecraft:player_attack 或 #minecraft:is_fire");
        this.damageResistantTypesEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_damage_type"), this::openDamageResistantTypeSelection));
        this.damageResistantTypesEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_tag"), this::openDamageResistantTagSelection));
        getEntries().add(this.damageResistantTypesEntry);
        getEntries().add(new SpacerEntryModel(this));
        CustomModelData customModelData = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        this.enableCustomModelData = customModelData != null;
        this.customModelDataToggle = new BooleanEntryModel(this, ModTexts.gui("custom_model_data_behavior_enabled"), this.enableCustomModelData, value -> {
            this.enableCustomModelData = value != null && value;
            syncCustomModelDataEntries();
        });
        getEntries().add(this.customModelDataToggle);
        this.cmdFloatsEntry = createCustomModelDataEntry(ModTexts.gui("custom_model_data.floats"),
                joinNumeric(customModelData == null ? null : customModelData.floats()));
        this.cmdFloatsEntry.setPlaceholder(placeholder("custom_model_data.floats.placeholder"));
        getEntries().add(this.cmdFloatsEntry);
        this.cmdFlagsEntry = createCustomModelDataEntry(ModTexts.gui("custom_model_data.flags"),
                joinFlags(customModelData == null ? null : customModelData.flags()));
        this.cmdFlagsEntry.setPlaceholder(placeholder("custom_model_data.flags.placeholder"));
        getEntries().add(this.cmdFlagsEntry);
        this.cmdStringsEntry = createCustomModelDataEntry(ModTexts.gui("custom_model_data.strings"),
                joinStrings(customModelData == null ? null : customModelData.strings()));
        this.cmdStringsEntry.setPlaceholder(placeholder("custom_model_data.strings.placeholder"));
        getEntries().add(this.cmdStringsEntry);
        this.cmdColorsEntry = createCustomModelDataEntry(ModTexts.gui("custom_model_data.colors"),
                joinNumeric(customModelData == null ? null : customModelData.colors()));
        this.cmdColorsEntry.setPlaceholder(placeholder("custom_model_data.colors.placeholder"));
        getEntries().add(this.cmdColorsEntry);
        syncCustomModelDataEntries();
    }

    @Override
    public void apply() {
        super.apply();
        applyCustomModelData();
    }

    private ItemStack getStack() {
        return getParent().getContext().getItemStack();
    }

    private StringEntryModel createCustomModelDataEntry(MutableComponent label, String initialValue) {
        return new StringEntryModel(this, label, initialValue == null ? "" : initialValue, v -> {
        });
    }

    private String placeholder(String key) {
        return ModTexts.gui(key).getString();
    }

    private void syncCustomModelDataEntries() {
        if (this.cmdFloatsEntry != null) {
            this.cmdFloatsEntry.setEnabled(this.enableCustomModelData);
            this.cmdFlagsEntry.setEnabled(this.enableCustomModelData);
            this.cmdStringsEntry.setEnabled(this.enableCustomModelData);
            this.cmdColorsEntry.setEnabled(this.enableCustomModelData);
        }
    }

    private void applyCustomModelData() {
        ItemStack stack = getStack();
        if (!this.enableCustomModelData) {
            stack.remove(DataComponents.CUSTOM_MODEL_DATA);
            getParent().removeComponentFromDataTag("minecraft:custom_model_data");
            return;
        }
        ParseResult<Float> floats = parseFloatList(this.cmdFloatsEntry);
        ParseResult<Boolean> flags = parseBooleanList(this.cmdFlagsEntry);
        ParseResult<String> strings = parseStringList(this.cmdStringsEntry);
        ParseResult<Integer> colors = parseColorList(this.cmdColorsEntry);
        if (!floats.valid || !flags.valid || !strings.valid || !colors.valid) {
            return;
        }
        if (floats.values.isEmpty() && flags.values.isEmpty() && strings.values.isEmpty() && colors.values.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_MODEL_DATA);
            return;
        }
        stack.set(DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(
                        List.copyOf(floats.values),
                        List.copyOf(flags.values),
                        List.copyOf(strings.values),
                        List.copyOf(colors.values)
                ));
    }

    private static String joinNumeric(List<? extends Number> values) {
        if (values == null || values.isEmpty()) return "";
        return values.stream()
                .map(number -> number.toString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    private static String joinFlags(List<Boolean> values) {
        if (values == null || values.isEmpty()) return "";
        return values.stream()
                .map(flag -> flag ? "true" : "false")
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    private static String joinStrings(List<String> values) {
        if (values == null || values.isEmpty()) return "";
        return String.join("\n", values);
    }

    private ParseResult<Float> parseFloatList(StringEntryModel entry) {
        return parseList(entry, token -> {
            try {
                return Float.parseFloat(token);
            } catch (NumberFormatException ex) {
                return null;
            }
        });
    }

    private ParseResult<Boolean> parseBooleanList(StringEntryModel entry) {
        return parseList(entry, token -> {
            String lower = token.toLowerCase();
            if ("true".equals(lower)) return Boolean.TRUE;
            if ("false".equals(lower)) return Boolean.FALSE;
            return null;
        });
    }

    private ParseResult<String> parseStringList(StringEntryModel entry) {
        return parseList(entry, token -> token);
    }

    private ParseResult<Integer> parseColorList(StringEntryModel entry) {
        return parseList(entry, token -> {
            try {
                return Integer.decode(token);
            } catch (NumberFormatException ex) {
                return null;
            }
        });
    }

    private <T> ParseResult<T> parseList(StringEntryModel entry, Parser<T> parser) {
        String raw = entry.getValue();
        if (raw == null || raw.isBlank()) {
            entry.setValid(true);
            return ParseResult.valid(Collections.emptyList());
        }
        List<T> values = new ArrayList<>();
        boolean valid = true;
        for (String token : raw.split("[,\\n]")) {
            String trimmed = token.trim();
            if (trimmed.isEmpty()) continue;
            T parsed = parser.parse(trimmed);
            if (parsed == null) {
                valid = false;
                break;
            }
            values.add(parsed);
        }
        entry.setValid(valid);
        return valid ? ParseResult.valid(values) : ParseResult.invalid();
    }

    private interface Parser<T> {
        T parse(String token);
    }

    private static final class ParseResult<T> {
        final List<T> values;
        final boolean valid;

        private ParseResult(List<T> values, boolean valid) {
            this.values = values;
            this.valid = valid;
        }

        static <T> ParseResult<T> valid(List<T> values) {
            return new ParseResult<>(values, true);
        }

        static <T> ParseResult<T> invalid() {
            return new ParseResult<>(List.of(), false);
        }
    }

    private void setItemId(String id) {
        try {
            Identifier rl = Identifier.parse(id);
            BuiltInRegistries.ITEM.getOptional(rl).ifPresent(item -> {
                ItemStack old = getParent().getContext().getItemStack();
                int count = Math.max(1, old.getCount());
                ItemStack repl = new ItemStack(item, count);
                getParent().handleStackReplaced(repl);
            });
        } catch (Exception e) {
        }
    }

    private void setCount(int value) {
        ItemStack stack = getParent().getContext().getItemStack();
        int clamped = Math.max(1, Math.min(999, value));
        stack.setCount(clamped);
    }

    private int getMaxStackSizeValue(ItemStack stack) {
        Integer override = (Integer) stack.get(DataComponents.MAX_STACK_SIZE);
        return override != null ? override.intValue() : stack.getItem().getDefaultMaxStackSize();
    }

    private void setMaxStackSize(int value) {
        int clamped;
        ItemStack stack = getParent().getContext().getItemStack();
        int defaultMax = stack.getItem().getDefaultMaxStackSize();
        if (value <= 0 || (clamped = Math.max(1, Math.min(99, value))) == defaultMax) {
            stack.remove(DataComponents.MAX_STACK_SIZE);
        } else {
            stack.set(DataComponents.MAX_STACK_SIZE, clamped);
        }
        int actualMax = stack.getMaxStackSize();
        if (stack.getCount() > actualMax) {
            stack.setCount(actualMax);
        }
    }

    private String getRarityString(ItemStack stack) {
        Rarity r = stack.get(DataComponents.RARITY);
        return r != null ? r.getSerializedName() : "common";
    }

    private void setRarity(String name) {
        String lowerCase;
        ItemStack stack = getParent().getContext().getItemStack();
        if (name == null) {
            lowerCase = "";
        } else {
            try {
                lowerCase = name.toLowerCase();
            } catch (Exception e) {
                return;
            }
        }
        String n = lowerCase;
        int i = n.indexOf(58);
        if (i >= 0) {
            n = n.substring(i + 1);
        }
        Rarity rarity = switch (n) {
            case "uncommon" -> Rarity.UNCOMMON;
            case "rare" -> Rarity.RARE;
            case "epic" -> Rarity.EPIC;
            default -> Rarity.COMMON;
        };
        if (rarity == Rarity.COMMON) {
            stack.remove(DataComponents.RARITY);
        } else {
            stack.set(DataComponents.RARITY, rarity);
        }
    }

    private String getItemModelId(ItemStack stack) {
        Identifier id = stack.get(DataComponents.ITEM_MODEL);
        return id == null ? "" : id.toString();
    }

    private void setItemModelId(String value) {
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        String normalized = normalizeResourceId(value);
        if (normalized.isEmpty()) {
            stack.remove(DataComponents.ITEM_MODEL);
        } else {
            stack.set(DataComponents.ITEM_MODEL, Identifier.parse(normalized));
        }
    }

    private String normalizeResourceId(String raw) {
        Identifier location;
        String trimmed = raw == null ? "" : raw.trim();
        return (trimmed.isEmpty() || (location = ClientUtil.parseResourceLocation(trimmed)) == null) ? "" : location.toString();
    }

    private String getTooltipStyleId(ItemStack stack) {
        Identifier id = stack.get(DataComponents.TOOLTIP_STYLE);
        return id == null ? "" : id.toString();
    }

    private void setTooltipStyleId(String value) {
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        String normalized = normalizeResourceId(value);
        if (normalized.isEmpty()) {
            stack.remove(DataComponents.TOOLTIP_STYLE);
        } else {
            stack.set(DataComponents.TOOLTIP_STYLE, Identifier.parse(normalized));
        }
    }

    private void setDamage(int value) {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!stack.isDamageableItem()) {
            if (value > 0) {
                stack.set(DataComponents.DAMAGE, value);
            } else {
                stack.remove(DataComponents.DAMAGE);
            }
            return;
        }
        int max = stack.getMaxDamage();
        int clamped = Math.max(0, Math.min(value, Math.max(0, max - 1)));
        stack.setDamageValue(clamped);
        if (clamped == 0) {
            stack.remove(DataComponents.DAMAGE);
        }
    }

    private int getMaxDamageValue(ItemStack stack) {
        Integer override = (Integer) stack.get(DataComponents.MAX_DAMAGE);
        if (override != null) {
            return override;
        }
        if (stack.isDamageableItem()) {
            return stack.getMaxDamage();
        }
        return 0;
    }

    private void setMaxDamage(int value) {
        ItemStack stack = getParent().getContext().getItemStack();
        if (value > 0) {
            stack.set(DataComponents.MAX_DAMAGE, value);
        } else {
            stack.remove(DataComponents.MAX_DAMAGE);
        }
    }

    private void setUnbreakable(boolean value) {
        ItemStack stack = getParent().getContext().getItemStack();
        if (value) {
            stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        } else {
            stack.remove(DataComponents.UNBREAKABLE);
        }
    }

    private int getRepairCost(ItemStack stack) {
        Integer v = (Integer) stack.get(DataComponents.REPAIR_COST);
        return v != null ? v : 0;
    }

    private void setRepairCost(int value) {
        ItemStack stack = getParent().getContext().getItemStack();
        if (value > 0) {
            stack.set(DataComponents.REPAIR_COST, value);
        } else {
            stack.remove(DataComponents.REPAIR_COST);
        }
    }

    private void setRepairableItems(String value) {
        this.repairableItemsRaw = value == null ? "" : value.trim();
        applyRepairableComponent();
    }

    private String serializeRepairableItems(Repairable repairable) {
        HolderSet<Item> items = repairable.items();
        if (items instanceof HolderSet.Named<Item> named) {
            return "#" + named.key().location();
        }
        return items.stream().map(holder -> holder.unwrapKey().map(key -> key.identifier().toString()).orElse("")).filter(s -> !s.isBlank()).reduce((a, b) -> a + ", " + b).orElse("");
    }

    private void openRepairableItemSelection() {
        Set<Identifier> initiallySelected = extractItemIds(this.repairableItemsRaw);
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_item"), "repairable_items", ClientCache.getItemSelectionItems(), null, true, selected -> {
            List<String> entries = new ArrayList<>();
            for (String entry : parseIdentifierList(this.repairableItemsRaw)) {
                if (entry.startsWith("#")) {
                    entries.add(entry);
                }
            }
            for (Identifier rl : selected) {
                entries.add(rl.toString());
            }
            String joined = String.join(", ", entries).trim();
            this.repairableItemsRaw = joined;
            this.repairableItemsEntry.setValue(joined);
            applyRepairableComponent();
        }, initiallySelected);
    }

    private void openRepairableTagSelection() {
        Set<Identifier> initiallySelected = extractTagIds(this.repairableItemsRaw);
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_tag"), "repairable_tags", ClientCache.getItemTagSelectionItems(), null, true, selected -> {
            List<String> entries = new ArrayList<>();
            for (String entry : parseIdentifierList(this.repairableItemsRaw)) {
                if (!entry.startsWith("#")) {
                    entries.add(entry);
                }
            }
            for (Identifier rl : selected) {
                entries.add("#" + rl);
            }
            String joined = String.join(", ", entries).trim();
            this.repairableItemsRaw = joined;
            this.repairableItemsEntry.setValue(joined);
            applyRepairableComponent();
        }, initiallySelected);
    }

    private void applyRepairableComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (this.repairableItemsRaw.isBlank()) {
            this.repairableItemsEntry.setValid(true);
            stack.remove(DataComponents.REPAIRABLE);
            getParent().removeComponentFromDataTag("minecraft:repairable");
            DebugLog.info((java.util.function.Supplier<String>) () -> "[Repairable] Empty input, removing component");
            return;
        }
        List<String> entries = parseIdentifierList(this.repairableItemsRaw);
        if (entries.isEmpty()) {
            this.repairableItemsEntry.setValid(false);
            DebugLog.info((java.util.function.Supplier<String>) () -> "[Repairable] Parsed entries empty from raw: " + this.repairableItemsRaw);
            return;
        }
        List<String> tagIds = new ArrayList<>();
        List<String> itemIds = new ArrayList<>();
        for (String entry : entries) {
            if (entry.startsWith("#")) {
                String tagId = entry.substring(1);
                if (!tagId.isBlank()) {
                    tagIds.add(tagId);
                }
            } else if (!entry.isBlank()) {
                itemIds.add(entry);
            }
        }
        if (itemIds.isEmpty() && tagIds.size() == 1) {
            Optional<HolderSet<Item>> namedSet = resolveNamedItemTag(tagIds.get(0));
            if (namedSet.isPresent()) {
                this.repairableItemsEntry.setValid(true);
                stack.set(DataComponents.REPAIRABLE, new Repairable(namedSet.get()));
                return;
            }
        }
        List<Holder<Item>> holders = resolveItemHolders(entries);
        if (holders.isEmpty()) {
            this.repairableItemsEntry.setValid(false);
        } else {
            this.repairableItemsEntry.setValid(true);
            stack.set(DataComponents.REPAIRABLE, new Repairable(HolderSet.direct(holders)));
        }
    }

    private List<Holder<Item>> resolveItemHolders(List<String> ids) {
        Optional<? extends HolderLookup.RegistryLookup<Item>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.ITEM);
        if (lookupOpt.isEmpty()) {
            return List.of();
        }
        HolderLookup.RegistryLookup<Item> lookup = lookupOpt.get();
        List<Holder<Item>> holders = new ArrayList<>();
        for (String entry : ids) {
            if (!entry.isBlank()) {
                if (entry.startsWith("#")) {
                    Identifier rl = tryParse(entry.substring(1));
                    if (rl != null) {
                        TagKey<Item> tag = TagKey.create(Registries.ITEM, rl);
                        lookup.get(tag).ifPresent(named -> named.stream().forEach(holders::add));
                    }
                } else {
                    Identifier rl = tryParse(entry);
                    if (rl != null) {
                        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, rl);
                        lookup.get(key).ifPresent(holders::add);
                    }
                }
            }
        }
        return holders;
    }

    private Optional<HolderSet<Item>> resolveNamedItemTag(String tagId) {
        Optional<? extends HolderLookup.RegistryLookup<Item>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.ITEM);
        if (lookupOpt.isEmpty()) {
            return Optional.empty();
        }
        Identifier rl = tryParse(tagId);
        if (rl == null) {
            return Optional.empty();
        }
        TagKey<Item> tag = TagKey.create(Registries.ITEM, rl);
        return ((HolderLookup.RegistryLookup) lookupOpt.get()).get(tag).map(named -> named);
    }

    private void setBreakSoundId(String id) {
        this.breakSoundId = sanitizeId(id);
        if (this.breakSoundEntry != null) {
            this.breakSoundEntry.setValid(this.breakSoundId.isBlank() || resolveSoundHolder(this.breakSoundId).isPresent());
        }
        applyBreakSoundComponent();
    }

    private void applyBreakSoundComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (this.breakSoundId.isBlank()) {
            stack.remove(DataComponents.BREAK_SOUND);
            return;
        }
        resolveSoundHolder(this.breakSoundId).ifPresentOrElse(sound -> stack.set(DataComponents.BREAK_SOUND, sound), () -> stack.remove(DataComponents.BREAK_SOUND));
    }

    private void setDamageResistantTypeId(String value) {
        this.damageResistantTypeId = value == null ? "" : value.trim();
        applyDamageResistantComponent();
    }

    private String serializeDamageResistantTypes(DamageResistant dr) {
        if (dr == null) {
            return "";
        }
        HolderSet<DamageType> types = dr.types();
        if (types instanceof HolderSet.Named<DamageType> named) {
            return "#" + named.key().location();
        }
        return types.stream().map(holder -> holder.unwrapKey().map(key -> key.identifier().toString()).orElse("")).filter(s -> !s.isBlank()).reduce((a, b) -> a + ", " + b).orElse("");
    }

    private List<Holder<DamageType>> resolveDamageTypeHolders(List<String> typeIds) {
        Optional<? extends HolderLookup.RegistryLookup<DamageType>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.DAMAGE_TYPE);
        if (lookupOpt.isEmpty()) {
            return List.of();
        }
        HolderLookup.RegistryLookup<DamageType> lookup = lookupOpt.get();
        List<Holder<DamageType>> holders = new ArrayList<>();
        for (String entry : typeIds) {
            if (!entry.isBlank()) {
                Identifier rl = tryParse(entry);
                if (rl != null) {
                    ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, rl);
                    lookup.get(key).ifPresent(holders::add);
                }
            }
        }
        return holders;
    }

    private void openDamageResistantTypeSelection() {
        Set<Identifier> initiallySelected = extractItemIds(this.damageResistantTypeId);
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_damage_type"), "damage_resistant_types", ClientCache.getDamageTypeSelectionItems(), null, true, selected -> {
            List<String> entries = new ArrayList<>();
            for (String entry : parseIdentifierList(this.damageResistantTypeId)) {
                if (entry.startsWith("#")) {
                    entries.add(entry);
                }
            }
            for (Identifier rl : selected) {
                entries.add(rl.toString());
            }
            String joined = String.join(", ", entries).trim();
            this.damageResistantTypeId = joined;
            this.damageResistantTypesEntry.setValue(joined);
            applyDamageResistantComponent();
        }, initiallySelected);
    }

    private void openDamageResistantTagSelection() {
        Set<Identifier> initiallySelected = extractTagIds(this.damageResistantTypeId);
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_tag"), "damage_resistant_tags", ClientCache.getDamageTypeTagSelectionItems(), null, true, selected -> {
            List<String> entries = new ArrayList<>();
            for (String entry : parseIdentifierList(this.damageResistantTypeId)) {
                if (!entry.startsWith("#")) {
                    entries.add(entry);
                }
            }
            for (Identifier rl : selected) {
                entries.add("#" + rl);
            }
            String joined = String.join(", ", entries).trim();
            this.damageResistantTypeId = joined;
            this.damageResistantTypesEntry.setValue(joined);
            applyDamageResistantComponent();
        }, initiallySelected);
    }

    private void applyDamageResistantComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (this.damageResistantTypeId.isBlank()) {
            this.damageResistantTypesEntry.setValid(true);
            stack.remove(DataComponents.DAMAGE_RESISTANT);
            getParent().removeComponentFromDataTag("minecraft:damage_resistant");
            return;
        }
        List<String> entries = parseIdentifierList(this.damageResistantTypeId);
        if (entries.isEmpty()) {
            this.damageResistantTypesEntry.setValid(false);
            return;
        }
        List<String> tagIds = new ArrayList<>();
        List<String> typeIds = new ArrayList<>();
        for (String entry : entries) {
            if (entry.startsWith("#")) {
                String tagId = entry.substring(1);
                if (!tagId.isBlank()) {
                    tagIds.add(tagId);
                }
            } else if (!entry.isBlank()) {
                typeIds.add(entry);
            }
        }
        if (typeIds.isEmpty() && tagIds.size() == 1) {
            Optional<HolderSet<DamageType>> namedSet = parseDamageTypeHolderSet("#" + tagIds.get(0));
            if (namedSet.isPresent()) {
                this.damageResistantTypesEntry.setValid(true);
                stack.set(DataComponents.DAMAGE_RESISTANT, new DamageResistant(namedSet.get()));
                return;
            }
        }
        List<Holder<DamageType>> holders = resolveDamageTypeHolders(typeIds);
        if (holders.isEmpty()) {
            this.damageResistantTypesEntry.setValid(false);
        } else {
            this.damageResistantTypesEntry.setValid(true);
            stack.set(DataComponents.DAMAGE_RESISTANT, new DamageResistant(HolderSet.direct(holders)));
        }
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

    private Optional<HolderSet<DamageType>> parseDamageTypeHolderSet(String value) {
        String sanitized = sanitizeId(value);
        if (sanitized.isBlank()) {
            return Optional.empty();
        }
        if (!sanitized.startsWith("#")) {
            return Optional.empty();
        }
        Optional<? extends HolderLookup.RegistryLookup<DamageType>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.DAMAGE_TYPE);
        if (lookupOpt.isEmpty()) {
            return Optional.empty();
        }
        Identifier rl = tryParse(sanitized.substring(1));
        if (rl == null) {
            return Optional.empty();
        }
        TagKey<DamageType> tag = TagKey.create(Registries.DAMAGE_TYPE, rl);
        return ((HolderLookup.RegistryLookup) lookupOpt.get()).get(tag).map(named -> named);
    }

    private Optional<Holder<SoundEvent>> resolveSoundHolder(String id) {
        return resolveSoundHolder((HolderLookup.RegistryLookup) ClientUtil.registryAccess().lookup(Registries.SOUND_EVENT).orElse(null), id);
    }

    private Optional<Holder<SoundEvent>> resolveSoundHolder(HolderLookup.RegistryLookup<SoundEvent> lookup, String id) {
        if (lookup == null) {
            return Optional.empty();
        }
        String sanitized = sanitizeId(id);
        if (sanitized.isBlank()) {
            return Optional.empty();
        }
        Identifier rl = tryParse(sanitized);
        if (rl == null) {
            return Optional.empty();
        }
        ResourceKey<SoundEvent> key = ResourceKey.create(Registries.SOUND_EVENT, rl);
        return lookup.get(key).map(holder -> holder);
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

    private Set<Identifier> extractTagIds(String raw) {
        Identifier rl;
        Set<Identifier> set = new LinkedHashSet<>();
        for (String entry : parseIdentifierList(raw)) {
            String trimmed = entry.startsWith("#") ? entry.substring(1) : null;
            if (trimmed != null && !trimmed.isBlank() && (rl = tryParse(trimmed)) != null) {
                set.add(rl);
            }
        }
        return set;
    }

    private Set<Identifier> extractItemIds(String raw) {
        Identifier rl;
        Set<Identifier> set = new LinkedHashSet<>();
        for (String entry : parseIdentifierList(raw)) {
            if (!entry.startsWith("#") && (rl = tryParse(entry)) != null) {
                set.add(rl);
            }
        }
        return set;
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

    private String namespaceFilter(String id) {
        String sanitized = sanitizeId(id);
        if (sanitized.isBlank()) {
            return null;
        }
        String namespace = sanitized.contains(":") ? sanitized.substring(0, sanitized.indexOf(58)) : "minecraft";
        return "namespace:" + namespace.toLowerCase(Locale.ROOT);
    }
}

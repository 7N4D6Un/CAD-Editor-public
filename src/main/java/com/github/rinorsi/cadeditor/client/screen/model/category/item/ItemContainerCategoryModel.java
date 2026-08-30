package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.LootTableSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.util.NbtHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.LockCode;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.storage.loot.LootTable;


public class ItemContainerCategoryModel extends ItemEditorCategoryModel {
    private static final String LOCK_COMPONENT = "minecraft:lock";
    private static final String KEY_ITEMS = "items";
    private static final String KEY_COUNT = "count";
    private static final String KEY_MIN = "min";
    private static final String KEY_MAX = "max";
    private static final String KEY_COMPONENTS = "components";
    private static final String LOOT_COMPONENT = "minecraft:container_loot";
    private static final String CUSTOM_NAME = "minecraft:custom_name";

    private StringWithActionsEntryModel lockRequiredItemEntry;
    private StringEntryModel lockRequiredCountEntry;
    private StringEntryModel lockNamePasswordEntry;
    private LootTableSelectionEntryModel lootTableEntry;
    private StringEntryModel lootSeedEntry;
    private CompoundTag workingLockData;
    private String initialItems;
    private String initialCount;
    private String initialPassword;

    public ItemContainerCategoryModel(ItemEditorModel parent) {
        super(ModTexts.CONTAINER, parent);
    }

    @Override 
    protected void setupEntries() {
        this.workingLockData = readRawLockData();
        CompoundTag lock = this.workingLockData;
        this.initialItems = readLockRequiredItem(lock);
        this.initialCount = readLockRequiredCount(lock);
        this.initialPassword = readLockNamePassword(lock);
        this.lockRequiredItemEntry = new StringWithActionsEntryModel(this, ModTexts.LOCK_REQUIRED_ITEM, this.initialItems, this::setLockRequiredItem);
        this.lockRequiredItemEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_item"), this::openItemSelection));
        this.lockRequiredItemEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_tag"), this::openTagSelection));
        this.lockRequiredCountEntry = new StringEntryModel(this, ModTexts.LOCK_REQUIRED_COUNT, this.initialCount, this::setLockRequiredCount);
        this.lockNamePasswordEntry = new StringEntryModel(this, ModTexts.LOCK_PASSWORD, this.initialPassword, this::setLockNamePassword);
        getEntries().add(this.lockRequiredItemEntry);
        getEntries().add(this.lockRequiredCountEntry);
        getEntries().add(this.lockNamePasswordEntry);
        this.lootTableEntry = new LootTableSelectionEntryModel(this, readLootTableId(), v -> {
        });
        this.lootSeedEntry = new StringEntryModel(this, ModTexts.SEED, readLootSeed(), v -> {
        });
        getEntries().add(this.lootTableEntry);
        getEntries().add(this.lootSeedEntry);
    }

    private void openItemSelection() {
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_item"), "lock_required_item", ClientCache.getItemSelectionItems(), selected -> {
            if (selected == null || selected.isBlank() || Identifier.tryParse(selected) == null) {
                return;
            }
            lockRequiredItemEntry.setValue(selected);
            setLockRequiredItem(selected);
        });
    }

    private void openTagSelection() {
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_tag"), "lock_required_tag", ClientCache.getItemTagSelectionItems(), selected -> {
            if (selected == null || selected.isBlank() || Identifier.tryParse(selected) == null) {
                return;
            }
            String tag = "#" + selected;
            lockRequiredItemEntry.setValue(tag);
            setLockRequiredItem(tag);
        });
    }

    @Override
    public void apply() {
        super.apply();
        pushLockToStack();
        applyContainerLoot();
    }

    private void applyContainerLoot() {
        CompoundTag components;
        ItemStack stack = getStack();
        String idRaw = Optional.ofNullable(this.lootTableEntry.getValue()).orElse("").trim();
        String seedRaw = Optional.ofNullable(this.lootSeedEntry.getValue()).orElse("").trim();
        if (idRaw.isEmpty()) {
            CompoundTag data = getData();
            if (data != null && (components = data.getCompound(KEY_COMPONENTS).orElse(null)) != null) {
                components.remove(LOOT_COMPONENT);
                components.remove("!" + LOOT_COMPONENT);
                components.put("!minecraft:container", new CompoundTag());
                if (components.isEmpty()) {
                    data.remove(KEY_COMPONENTS);
                }
            }
            stack.remove(DataComponents.CONTAINER_LOOT);
            this.lootTableEntry.setValid(true);
            this.lootSeedEntry.setValid(true);
            return;
        }
        try {
            Identifier id = Identifier.parse(idRaw);
            CompoundTag loot = new CompoundTag();
            loot.putString("loot_table", id.toString());
            long seedValue = 0;
            boolean hasSeed = false;
            if (!seedRaw.isEmpty()) {
                seedValue = Long.parseLong(seedRaw);
                hasSeed = true;
            }
            CompoundTag data = getData();
            if (data != null) {
                CompoundTag lootComponents = NbtHelper.getOrCreateCompound(data, KEY_COMPONENTS);
                if (hasSeed) {
                    loot.putLong("seed", seedValue);
                }
                lootComponents.put(LOOT_COMPONENT, loot);
                lootComponents.remove("minecraft:container");
                lootComponents.put("!minecraft:container", new CompoundTag());
                if (lootComponents.isEmpty()) {
                    data.remove(KEY_COMPONENTS);
                }
            }
            ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, id);
            SeededContainerLoot seeded = new SeededContainerLoot(key, hasSeed ? seedValue : 0L);
            stack.set(DataComponents.CONTAINER_LOOT, seeded);
            stack.remove(DataComponents.CONTAINER);
            this.lootTableEntry.setValid(true);
            this.lootSeedEntry.setValid(true);
        } catch (Exception e) {
            this.lootTableEntry.setValid(false);
            this.lootSeedEntry.setValid(false);
        }
    }

    private String readLootTableId() {
        CompoundTag data = getData();
        if (data != null) {
            CompoundTag components = data.getCompound(KEY_COMPONENTS).orElse(null);
            if (components != null) {
                CompoundTag loot = components.getCompound(LOOT_COMPONENT).orElse(null);
                if (loot != null) {
                    return loot.getString("loot_table").orElse("");
                }
            }
        }
        return "";
    }

    private String readLootSeed() {
        CompoundTag data = getData();
        if (data != null) {
            CompoundTag components = data.getCompound(KEY_COMPONENTS).orElse(null);
            if (components != null) {
                CompoundTag loot = components.getCompound(LOOT_COMPONENT).orElse(null);
                if (loot != null && loot.contains("seed")) {
                    return Long.toString(loot.getLongOr("seed", 0L));
                }
            }
        }
        return "";
    }

    private ItemStack getStack() {
        return getParent().getContext().getItemStack();
    }

    private CompoundTag readRawLockData() {
        CompoundTag data = getData();
        if (data != null) {
            CompoundTag components = data.getCompound("components").orElse(null);
            if (components != null) {
                return components.getCompound(LOCK_COMPONENT).map(CompoundTag::copy).orElseGet(CompoundTag::new);
            }
        }
        return new CompoundTag();
    }

    private void pushLockToStack() {
        ItemStack stack = getStack();
        if (stack == null) {
            return;
        }
        if (this.workingLockData == null || this.workingLockData.isEmpty()) {
            stack.remove(DataComponents.LOCK);
            clearDisplayLockData();
            return;
        }
        try {
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, ClientUtil.registryAccess());
            LockCode code = LockCode.CODEC.parse(ops, this.workingLockData).result().orElse(null);
            if (code != null) {
                stack.set(DataComponents.LOCK, code);
                publishDisplayLockData();
            } else {
                stack.remove(DataComponents.LOCK);
                clearDisplayLockData();
            }
        } catch (Exception e) {
            stack.remove(DataComponents.LOCK);
        }
    }

    private void publishDisplayLockData() {
        CompoundTag data = getData();
        if (data == null) {
            return;
        }
        CompoundTag components = data.getCompound("components").map(CompoundTag::copy).orElseGet(CompoundTag::new);
        components.put(LOCK_COMPONENT, this.workingLockData.copy());
        data.put("components", components);
    }

    private void clearDisplayLockData() {
        CompoundTag data = getData();
        if (data == null) {
            return;
        }
        CompoundTag components = data.getCompound("components").orElse(null);
        if (components != null) {
            components.remove(LOCK_COMPONENT);
        }
    }

    private String readLockRequiredItem(CompoundTag lock) {
        if (lock == null) {
            return "";
        }
        Tag item = lock.get(KEY_ITEMS);
        if (item instanceof StringTag s) {
            return s.asString().orElse("");
        }
        return "";
    }

    private String readLockRequiredCount(CompoundTag lock) {
        if (lock == null || !lock.contains(KEY_COUNT)) {
            return "";
        }
        Tag encoded = lock.get(KEY_COUNT);
        if (encoded instanceof CompoundTag range) {
            boolean hasMin = range.contains(KEY_MIN);
            boolean hasMax = range.contains(KEY_MAX);
            if (hasMin && hasMax) {
                int min = range.getIntOr(KEY_MIN, 0);
                int max = range.getIntOr(KEY_MAX, 0);
                return min == max ? String.valueOf(min) : min + ".." + max;
            }
            if (hasMin) {
                return range.getIntOr(KEY_MIN, 0) + "..";
            }
            if (hasMax) {
                return ".." + range.getIntOr(KEY_MAX, 0);
            }
            return "";
        }
        if (encoded instanceof NumericTag number) {
            return String.valueOf(number.intValue());
        }
        return "";
    }

    private String readLockNamePassword(CompoundTag lock) {
        if (lock == null) {
            return "";
        }
        CompoundTag components = lock.getCompound(KEY_COMPONENTS).orElse(null);
        if (components == null) {
            return "";
        }
        Tag encoded = components.get(CUSTOM_NAME);
        if (encoded instanceof StringTag s) {
            return s.asString().orElse("");
        }
        if (encoded instanceof CompoundTag c) {
            Tag text = c.get("text");
            if (text instanceof StringTag t) {
                return t.asString().orElse("");
            }
        }
        return "";
    }

    private void setLockRequiredItem(String value) {
        String text = value == null ? "" : value.trim();
        if (Objects.equals(text, this.initialItems)) {
            return;
        }
        if (text.isEmpty()) {
            this.workingLockData.remove(KEY_ITEMS);
        } else {
            this.workingLockData.putString(KEY_ITEMS, text);
        }
    }

    private void setLockRequiredCount(String value) {
        String text = value == null ? "" : value.trim();
        if (Objects.equals(text, this.initialCount)) {
            return;
        }
        if (text.isEmpty()) {
            this.workingLockData.remove(KEY_COUNT);
            return;
        }
        Tag parsed = parseCount(text);
        if (parsed != null) {
            this.workingLockData.put(KEY_COUNT, parsed);
        }
    }

    private void setLockNamePassword(String value) {
        String text = value == null ? "" : value.trim();
        if (Objects.equals(text, this.initialPassword)) {
            return;
        }
        if (text.isEmpty()) {
            removeCustomNamePassword();
            return;
        }
        CompoundTag components = this.workingLockData.getCompound(KEY_COMPONENTS).orElse(null);
        if (components == null) {
            components = new CompoundTag();
            this.workingLockData.put(KEY_COMPONENTS, components);
        }
        components.putString(CUSTOM_NAME, text);
    }

    private void removeCustomNamePassword() {
        CompoundTag components = this.workingLockData.getCompound(KEY_COMPONENTS).orElse(null);
        if (components != null) {
            components.remove(CUSTOM_NAME);
            if (components.isEmpty()) {
                this.workingLockData.remove(KEY_COMPONENTS);
            }
        }
    }

    private Tag parseCount(String text) {
        try {
            int separator = text.indexOf("..");
            if (separator >= 0) {
                CompoundTag range = new CompoundTag();
                String low = text.substring(0, separator).trim();
                String high = text.substring(separator + 2).trim();
                if (!low.isEmpty()) {
                    range.putInt(KEY_MIN, Integer.parseInt(low));
                }
                if (!high.isEmpty()) {
                    range.putInt(KEY_MAX, Integer.parseInt(high));
                }
                return range.isEmpty() ? null : range;
            }
            return IntTag.valueOf(Integer.parseInt(text));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
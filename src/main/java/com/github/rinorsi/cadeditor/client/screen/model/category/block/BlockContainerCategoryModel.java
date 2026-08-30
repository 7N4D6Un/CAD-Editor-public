package com.github.rinorsi.cadeditor.client.screen.model.category.block;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.BlockEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.TextEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.LootTableSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.util.ComponentJsonHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class BlockContainerCategoryModel extends BlockEditorCategoryModel {
    private static final String LOCK_TAG = "lock";
    private static final String LOCK_ITEMS_TAG = "items";
    private static final String LOCK_COUNT_TAG = "count";
    private static final String LOCK_COUNT_MIN_TAG = "min";
    private static final String LOCK_COUNT_MAX_TAG = "max";
    private static final String LOCK_COMPONENTS_TAG = "components";
    private static final String LOCK_CUSTOM_NAME_COMPONENT = "minecraft:custom_name";
    private static final String CUSTOM_NAME_TAG = "CustomName";

    private StringWithActionsEntryModel lockRequiredItemEntry;
    private LootTableSelectionEntryModel lootTableEntry;
    private StringEntryModel lootSeedEntry;

    public BlockContainerCategoryModel(BlockEditorModel editor) {
        super(ModTexts.CONTAINER, editor);
    }

    @Override
    protected void setupEntries() {
        CompoundTag lock = getLockTag();
        this.lockRequiredItemEntry = new StringWithActionsEntryModel(this, ModTexts.LOCK_REQUIRED_ITEM, readLockRequiredItem(lock), this::setLockRequiredItem);
        this.lockRequiredItemEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_item"), this::openItemSelection));
        this.lockRequiredItemEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_tag"), this::openTagSelection));
        this.lootTableEntry = new LootTableSelectionEntryModel(this, readLootTable(), v -> {
        });
        this.lootSeedEntry = new StringEntryModel(this, ModTexts.SEED, readLootSeed(), v -> {
        });
        getEntries().addAll(
                new TextEntryModel(this, ModTexts.CUSTOM_NAME, getCustomName(), this::setCustomName),
                this.lockRequiredItemEntry,
                new StringEntryModel(this, ModTexts.LOCK_REQUIRED_COUNT, readLockRequiredCount(lock), this::setLockRequiredCount),
                new StringEntryModel(this, ModTexts.LOCK_PASSWORD, readLockNamePassword(lock), this::setLockNamePassword),
                this.lootTableEntry,
                this.lootSeedEntry
        );
    }

    private String readLootTable() {
        CompoundTag tag = getData();
        if (tag != null && tag.contains("LootTable")) {
            return tag.getString("LootTable").orElse("");
        }
        return "";
    }

    private String readLootSeed() {
        CompoundTag tag = getData();
        if (tag != null && tag.contains("LootTableSeed")) {
            return tag.getLong("LootTableSeed").map(value -> Long.toString(value)).orElse("");
        }
        return "";
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
        CompoundTag data = getData();
        CompoundTag lock = data.getCompound(LOCK_TAG).orElse(null);
        if (lock != null && lock.isEmpty()) {
            data.remove(LOCK_TAG);
        }
        applyContainerLoot();
    }

    private void applyContainerLoot() {
        CompoundTag tag = getData();
        if (tag == null) {
            return;
        }
        String table = Optional.ofNullable(this.lootTableEntry.getValue()).orElse("").trim();
        String seedRaw = Optional.ofNullable(this.lootSeedEntry.getValue()).orElse("").trim();
        if (table.isEmpty()) {
            tag.remove("LootTable");
            tag.remove("LootTableSeed");
            this.lootTableEntry.setValid(true);
            this.lootSeedEntry.setValid(true);
            return;
        }
        tag.putString("LootTable", table);
        if (seedRaw.isEmpty()) {
            tag.remove("LootTableSeed");
            this.lootSeedEntry.setValid(true);
        } else {
            try {
                long s = Long.parseLong(seedRaw);
                tag.putLong("LootTableSeed", s);
                this.lootSeedEntry.setValid(true);
            } catch (Exception ex) {
                this.lootSeedEntry.setValid(false);
                return;
            }
        }
        this.lootTableEntry.setValid(true);
        if (tag.contains("Items")) {
            tag.remove("Items");
        }
    }

    private CompoundTag getLockTag() {
        return getData().getCompound(LOCK_TAG).orElse(null);
    }

    private String readLockRequiredItem(CompoundTag lock) {
        if (lock == null) {
            return "";
        }
        return lock.getString(LOCK_ITEMS_TAG).orElse("");
    }

    private String readLockRequiredCount(CompoundTag lock) {
        if (lock == null || !lock.contains(LOCK_COUNT_TAG)) {
            return "";
        }
        Tag encoded = lock.get(LOCK_COUNT_TAG);
        if (encoded instanceof CompoundTag range) {
            boolean hasMin = range.contains(LOCK_COUNT_MIN_TAG);
            boolean hasMax = range.contains(LOCK_COUNT_MAX_TAG);
            if (hasMin && hasMax) {
                int min = range.getIntOr(LOCK_COUNT_MIN_TAG, 0);
                int max = range.getIntOr(LOCK_COUNT_MAX_TAG, 0);
                return min == max ? String.valueOf(min) : min + ".." + max;
            }
            if (hasMin) {
                return range.getIntOr(LOCK_COUNT_MIN_TAG, 0) + "..";
            }
            if (hasMax) {
                return ".." + range.getIntOr(LOCK_COUNT_MAX_TAG, 0);
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
        CompoundTag components = lock.getCompound(LOCK_COMPONENTS_TAG).orElse(null);
        if (components == null) {
            return "";
        }
        Tag encoded = components.get(LOCK_CUSTOM_NAME_COMPONENT);
        if (encoded == null) {
            return "";
        }
        MutableComponent decoded = ComponentJsonHelper.decode(encoded, ClientUtil.registryAccess());
        return decoded == null ? "" : decoded.getString();
    }

    private MutableComponent getCustomName() {
        Tag encoded = getData().get(CUSTOM_NAME_TAG);
        return ComponentJsonHelper.decode(encoded, ClientUtil.registryAccess());
    }

    private void setCustomName(MutableComponent value) {
        if (value != null && !value.getString().isEmpty()) {
            Tag encoded = ComponentJsonHelper.encodeToTag(value, ClientUtil.registryAccess());
            if (encoded != null) {
                getData().put(CUSTOM_NAME_TAG, encoded);
            }
        } else {
            getData().remove(CUSTOM_NAME_TAG);
        }
    }

    private void setLockRequiredItem(String value) {
        updateLockTag(lock -> {
            String text = value == null ? "" : value.trim();
            if (text.isEmpty()) {
                lock.remove(LOCK_ITEMS_TAG);
            } else {
                lock.putString(LOCK_ITEMS_TAG, text);
            }
        });
    }

    private void setLockRequiredCount(String value) {
        updateLockTag(lock -> {
            String text = value == null ? "" : value.trim();
            if (text.isEmpty()) {
                lock.remove(LOCK_COUNT_TAG);
                return;
            }
            Tag parsed = parseLockCount(text);
            if (parsed != null) {
                lock.put(LOCK_COUNT_TAG, parsed);
            }
        });
    }

    private void setLockNamePassword(String value) {
        updateLockTag(lock -> {
            String text = value == null ? "" : value.trim();
            if (text.isEmpty()) {
                removeLockComponent(lock, LOCK_CUSTOM_NAME_COMPONENT);
                return;
            }
            Tag encoded = ComponentJsonHelper.encodeToTag(Component.literal(text), ClientUtil.registryAccess());
            if (encoded == null) {
                return;
            }
            CompoundTag components = lock.getCompound(LOCK_COMPONENTS_TAG).orElse(null);
            if (components == null) {
                components = new CompoundTag();
                lock.put(LOCK_COMPONENTS_TAG, components);
            }
            components.put(LOCK_CUSTOM_NAME_COMPONENT, encoded);
        });
    }

    private void updateLockTag(Consumer<CompoundTag> action) {
        CompoundTag data = getData();
        CompoundTag lock = data.getCompound(LOCK_TAG).orElse(null);
        if (lock == null) {
            lock = new CompoundTag();
            action.accept(lock);
            if (!lock.isEmpty()) {
                data.put(LOCK_TAG, lock);
            }
        } else {
            action.accept(lock);
        }
    }

    private void removeLockComponent(CompoundTag lock, String componentId) {
        CompoundTag components = lock.getCompound(LOCK_COMPONENTS_TAG).orElse(null);
        if (components != null) {
            components.remove(componentId);
            if (components.isEmpty()) {
                lock.remove(LOCK_COMPONENTS_TAG);
            }
        }
    }

    private Tag parseLockCount(String text) {
        try {
            int separator = text.indexOf("..");
            if (separator >= 0) {
                CompoundTag range = new CompoundTag();
                String low = text.substring(0, separator).trim();
                String high = text.substring(separator + 2).trim();
                if (!low.isEmpty()) {
                    range.putInt(LOCK_COUNT_MIN_TAG, Integer.parseInt(low));
                }
                if (!high.isEmpty()) {
                    range.putInt(LOCK_COUNT_MAX_TAG, Integer.parseInt(high));
                }
                return range.isEmpty() ? null : range;
            }
            int exact = Integer.parseInt(text);
            CompoundTag exactRange = new CompoundTag();
            exactRange.putInt(LOCK_COUNT_MIN_TAG, exact);
            exactRange.putInt(LOCK_COUNT_MAX_TAG, exact);
            return exactRange;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

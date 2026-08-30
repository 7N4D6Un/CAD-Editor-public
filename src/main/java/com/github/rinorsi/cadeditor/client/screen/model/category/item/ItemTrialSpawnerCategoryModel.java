package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.franckyi.databindings.api.BooleanProperty;
import com.github.franckyi.databindings.api.ObservableBooleanValue;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.TrialSpawnerNbtHelper;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntityEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.LootTableSelectionEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

public class ItemTrialSpawnerCategoryModel extends ItemEditorCategoryModel {
    private static final BooleanProperty ALWAYS_VALID = BooleanProperty.create(true);
    private boolean ominous;
    private CompoundTag root;
    private BooleanEntryModel ominousToggle;
    private EntityEntryModel entityEntry;
    private IntegerEntryModel spawnRangeEntry;
    private FloatEntryModel totalMobsEntry;
    private FloatEntryModel simultaneousMobsEntry;
    private FloatEntryModel totalAddedEntry;
    private FloatEntryModel simultaneousAddedEntry;
    private IntegerEntryModel ticksBetweenEntry;
    private LootTableSelectionEntryModel lootEjectEntry;
    private LootTableSelectionEntryModel itemsDropEntry;
    private IntegerEntryModel targetCooldownEntry;
    private IntegerEntryModel requiredPlayerRangeEntry;

    public ItemTrialSpawnerCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("trial_spawner"), editor);
        ItemStack stack = getParent().getContext().getItemStack();
        TypedEntityData<net.minecraft.world.level.block.entity.BlockEntityType<?>> data = stack == null ? null : stack.get(DataComponents.BLOCK_ENTITY_DATA);
        this.root = data == null ? new CompoundTag() : data.getUnsafe().copy();
        this.ominous = false;
    }

    @Override
    protected void setupEntries() {
        ominousToggle = new BooleanEntryModel(this, ModTexts.gui("trial_ominous"), ominous, this::onOminousToggled);
        getEntries().add(ominousToggle);
        targetCooldownEntry = new IntegerEntryModel(this, ModTexts.gui("trial_target_cooldown"),
                Math.max(0, TrialSpawnerNbtHelper.readInt(root, TrialSpawnerNbtHelper.KEY_TARGET_COOLDOWN_LENGTH, 36000)), value -> {
        }, value -> value >= 0);
        requiredPlayerRangeEntry = new IntegerEntryModel(this, ModTexts.gui("trial_required_player_range"),
                Math.max(1, TrialSpawnerNbtHelper.readInt(root, TrialSpawnerNbtHelper.KEY_REQUIRED_PLAYER_RANGE, 14)), value -> {
        }, value -> value >= 1 && value <= 128);
        getEntries().add(targetCooldownEntry);
        getEntries().add(requiredPlayerRangeEntry);
        buildConfigEntries(TrialSpawnerNbtHelper.loadConfig(root, configKey()));
        insertConfigEntries();
    }

    @Override
    public ObservableBooleanValue validProperty() {
        return ALWAYS_VALID;
    }

    private void buildConfigEntries(CompoundTag config) {
        CompoundTag entityTag = TrialSpawnerNbtHelper.readSpawnEntity(config);
        entityEntry = new EntityEntryModel(this, resolveEntityType(entityTag), entityTag, value -> {
        });
        entityEntry.setLabel(ModTexts.gui("trial_spawn_entity"));
        spawnRangeEntry = new IntegerEntryModel(this, ModTexts.gui("trial_spawn_range"),
                Math.max(1, TrialSpawnerNbtHelper.readInt(config, TrialSpawnerNbtHelper.KEY_SPAWN_RANGE, 4)), value -> {
        }, value -> value >= 1 && value <= 128);
        totalMobsEntry = new FloatEntryModel(this, ModTexts.gui("trial_total_mobs"),
                TrialSpawnerNbtHelper.readFloat(config, TrialSpawnerNbtHelper.KEY_TOTAL_MOBS, 6.0f), value -> {
        }, value -> value >= 0.0f);
        simultaneousMobsEntry = new FloatEntryModel(this, ModTexts.gui("trial_simultaneous_mobs"),
                TrialSpawnerNbtHelper.readFloat(config, TrialSpawnerNbtHelper.KEY_SIMULTANEOUS_MOBS, 2.0f), value -> {
        }, value -> value >= 0.0f);
        totalAddedEntry = new FloatEntryModel(this, ModTexts.gui("trial_total_added_per_player"),
                TrialSpawnerNbtHelper.readFloat(config, TrialSpawnerNbtHelper.KEY_TOTAL_MOBS_ADDED_PER_PLAYER, 2.0f), value -> {
        }, value -> value >= 0.0f);
        simultaneousAddedEntry = new FloatEntryModel(this, ModTexts.gui("trial_simultaneous_added_per_player"),
                TrialSpawnerNbtHelper.readFloat(config, TrialSpawnerNbtHelper.KEY_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER, 1.0f), value -> {
        }, value -> value >= 0.0f);
        ticksBetweenEntry = new IntegerEntryModel(this, ModTexts.gui("trial_ticks_between_spawn"),
                Math.max(0, TrialSpawnerNbtHelper.readInt(config, TrialSpawnerNbtHelper.KEY_TICKS_BETWEEN_SPAWN, 40)), value -> {
        }, value -> value >= 0);
        lootEjectEntry = new LootTableSelectionEntryModel(this,
                TrialSpawnerNbtHelper.readSingleLootKey(config, TrialSpawnerNbtHelper.KEY_LOOT_TABLES_TO_EJECT), value -> {
        });
        lootEjectEntry.setLabel(ModTexts.gui("trial_loot_tables_eject"));
        if (ominous) {
            itemsDropEntry = new LootTableSelectionEntryModel(this,
                    config.getStringOr(TrialSpawnerNbtHelper.KEY_ITEMS_TO_DROP_WHEN_OMINOUS, ""), value -> {
            });
            itemsDropEntry.setLabel(ModTexts.gui("trial_items_drop_when_ominous"));
        } else {
            itemsDropEntry = null;
        }
    }

    private void insertConfigEntries() {
        insertAfter(requiredPlayerRangeEntry, entityEntry, spawnRangeEntry, totalMobsEntry, simultaneousMobsEntry,
                totalAddedEntry, simultaneousAddedEntry, ticksBetweenEntry, lootEjectEntry);
        if (itemsDropEntry != null) {
            insertAfter(lootEjectEntry, itemsDropEntry);
        }
    }

    private void removeConfigEntries() {
        removeEntries(entityEntry, spawnRangeEntry, totalMobsEntry, simultaneousMobsEntry, totalAddedEntry,
                simultaneousAddedEntry, ticksBetweenEntry, lootEjectEntry, itemsDropEntry);
    }

    private void onOminousToggled(boolean value) {
        if (value == ominous) {
            return;
        }
        commitToRoot();
        removeConfigEntries();
        ominous = value;
        buildConfigEntries(TrialSpawnerNbtHelper.loadConfig(root, configKey()));
        insertConfigEntries();
    }

    @Override
    public void apply() {
        super.apply();
        commitToRoot();
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        CompoundTag payload = root.copy();
        payload.remove("id");
        stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(BlockEntityTypes.TRIAL_SPAWNER, payload));
    }

    private void commitToRoot() {
        CompoundTag config = new CompoundTag();
        TrialSpawnerNbtHelper.putInt(config, TrialSpawnerNbtHelper.KEY_SPAWN_RANGE, bound(spawnRangeEntry.getValue(), 1, 128));
        TrialSpawnerNbtHelper.putFloat(config, TrialSpawnerNbtHelper.KEY_TOTAL_MOBS, max0(totalMobsEntry.getValue()));
        TrialSpawnerNbtHelper.putFloat(config, TrialSpawnerNbtHelper.KEY_SIMULTANEOUS_MOBS, max0(simultaneousMobsEntry.getValue()));
        TrialSpawnerNbtHelper.putFloat(config, TrialSpawnerNbtHelper.KEY_TOTAL_MOBS_ADDED_PER_PLAYER, max0(totalAddedEntry.getValue()));
        TrialSpawnerNbtHelper.putFloat(config, TrialSpawnerNbtHelper.KEY_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER, max0(simultaneousAddedEntry.getValue()));
        TrialSpawnerNbtHelper.putInt(config, TrialSpawnerNbtHelper.KEY_TICKS_BETWEEN_SPAWN, Math.max(0, ticksBetweenEntry.getValue()));
        CompoundTag entity = entityEntry == null ? new CompoundTag() : entityEntry.copyValue();
        if (entity.getStringOr("id", "").isBlank()) {
            config.remove(TrialSpawnerNbtHelper.KEY_SPAWN_POTENTIALS);
        } else {
            TrialSpawnerNbtHelper.writeSpawnEntity(config, entity);
        }
        TrialSpawnerNbtHelper.writeSingleLootKey(config, TrialSpawnerNbtHelper.KEY_LOOT_TABLES_TO_EJECT, lootEjectEntry.getValue());
        if (itemsDropEntry != null) {
            String itemsDrop = itemsDropEntry.getValue() == null ? "" : itemsDropEntry.getValue().trim();
            if (itemsDrop.isEmpty()) {
                config.remove(TrialSpawnerNbtHelper.KEY_ITEMS_TO_DROP_WHEN_OMINOUS);
            } else {
                config.putString(TrialSpawnerNbtHelper.KEY_ITEMS_TO_DROP_WHEN_OMINOUS, itemsDrop);
            }
        }
        TrialSpawnerNbtHelper.putConfig(root, configKey(), config);
        TrialSpawnerNbtHelper.putInt(root, TrialSpawnerNbtHelper.KEY_TARGET_COOLDOWN_LENGTH, Math.max(0, targetCooldownEntry.getValue()));
        TrialSpawnerNbtHelper.putInt(root, TrialSpawnerNbtHelper.KEY_REQUIRED_PLAYER_RANGE, bound(requiredPlayerRangeEntry.getValue(), 1, 128));
        TrialSpawnerNbtHelper.syncSpawnData(root);
    }

    private String configKey() {
        return ominous ? TrialSpawnerNbtHelper.KEY_OMINOUS_CONFIG : TrialSpawnerNbtHelper.KEY_NORMAL_CONFIG;
    }

    private void insertAfter(EntryModel anchor, EntryModel... entries) {
        if (anchor == null || entries == null || entries.length == 0) {
            return;
        }
        int index = getEntries().indexOf(anchor);
        int insertIndex = index < 0 ? getEntries().size() : index + 1;
        for (EntryModel entry : entries) {
            if (entry != null && !getEntries().contains(entry)) {
                getEntries().add(insertIndex, entry);
                insertIndex++;
            }
        }
    }

    private void removeEntries(EntryModel... entries) {
        if (entries == null) {
            return;
        }
        for (EntryModel entry : entries) {
            if (entry != null) {
                getEntries().remove(entry);
            }
        }
    }

    private static int bound(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float max0(float value) {
        return Math.max(0.0f, value);
    }

    private static EntityType<?> resolveEntityType(CompoundTag entityTag) {
        String id = entityTag.getStringOr("id", "");
        if (id.isBlank()) {
            return null;
        }
        return (EntityType) net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getOptional(net.minecraft.resources.Identifier.tryParse(id)).orElse(null);
    }
}
package com.github.rinorsi.cadeditor.client.screen.model.category.block;

import com.github.rinorsi.cadeditor.client.debug.DebugLog;
import com.github.rinorsi.cadeditor.client.screen.model.BlockEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntityEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.LootTableSelectionEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

import static com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper.extractEntityData;
import static com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper.extractSpawnDataExtras;
import static com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper.normalizeEntityId;
import static com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper.readCompound;
import static com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper.readInt;
import static com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper.resolveEntityType;
import static com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper.sanitizeEntityData;
import static com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper.writeCompound;
import static com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper.writeInt;

public class BlockSpawnerCategoryModel extends BlockEditorCategoryModel {

    private EntityEntryModel entityEntry;
    private IntegerEntryModel delayEntry;
    private IntegerEntryModel minSpawnDelayEntry;
    private IntegerEntryModel maxSpawnDelayEntry;
    private IntegerEntryModel spawnCountEntry;
    private IntegerEntryModel maxNearbyEntry;
    private IntegerEntryModel requiredPlayerRangeEntry;
    private IntegerEntryModel spawnRangeEntry;
    private CompoundTag spawnDataExtras = new CompoundTag();
    private boolean preferSnakeCase = true;
    private String initialEntityId = "";
    private BooleanEntryModel customRulesEnabled;
    private IntegerEntryModel blockLightMinEntry;
    private IntegerEntryModel blockLightMaxEntry;
    private IntegerEntryModel skyLightMinEntry;
    private IntegerEntryModel skyLightMaxEntry;
    private BooleanEntryModel equipmentEnabled;
    private LootTableSelectionEntryModel equipmentLootTableEntry;
    private FloatEntryModel mainhandDropChanceEntry;
    private FloatEntryModel offhandDropChanceEntry;
    private FloatEntryModel headDropChanceEntry;
    private FloatEntryModel chestDropChanceEntry;
    private FloatEntryModel legsDropChanceEntry;
    private FloatEntryModel feetDropChanceEntry;

    public BlockSpawnerCategoryModel(BlockEditorModel editor) {
        super(ModTexts.gui("spawner"), editor);
    }

    @Override
    protected void setupEntries() {
        CompoundTag tag = ensureTag();
        preferSnakeCase = SpawnerNbtHelper.detectSnakeCase(tag);
        CompoundTag rawSpawnData = readCompound(tag, SpawnerNbtHelper.KEY_SPAWN_DATA_SNAKE, SpawnerNbtHelper.KEY_SPAWN_DATA_LEGACY);
        CompoundTag entityData = extractEntityData(rawSpawnData);
        spawnDataExtras = extractSpawnDataExtras(rawSpawnData);
        initialEntityId = normalizeEntityId(entityData.getStringOr("id", ""));
        DebugLog.info(() -> "[SpawnerModel] setup preferSnake=" + preferSnakeCase
                + " initialEntity=" + initialEntityId
                + " hasSpawnData=" + !rawSpawnData.isEmpty()
                + " hasPotentials=" + (tag.contains(SpawnerNbtHelper.KEY_SPAWN_POTENTIALS_SNAKE) || tag.contains(SpawnerNbtHelper.KEY_SPAWN_POTENTIALS_LEGACY)));

        EntityType<?> type = resolveEntityType(entityData);
        entityEntry = new EntityEntryModel(this, type, entityData, value -> {
        }).withWeight(0);
        entityEntry.setLabel(ModTexts.gui("spawner_entity"));
        entityEntry.entityIdProperty().addListener(value -> refreshEntityValidity());
        entityEntry.validProperty().addListener(value -> refreshEntityValidity());

        int delay = readInt(tag, 20, SpawnerNbtHelper.KEY_DELAY_SNAKE, SpawnerNbtHelper.KEY_DELAY_LEGACY);
        int minDelay = readInt(tag, 200, SpawnerNbtHelper.KEY_MIN_DELAY_SNAKE, SpawnerNbtHelper.KEY_MIN_DELAY_LEGACY);
        int maxDelay = readInt(tag, 800, SpawnerNbtHelper.KEY_MAX_DELAY_SNAKE, SpawnerNbtHelper.KEY_MAX_DELAY_LEGACY);
        int spawnCount = readInt(tag, 4, SpawnerNbtHelper.KEY_SPAWN_COUNT_SNAKE, SpawnerNbtHelper.KEY_SPAWN_COUNT_LEGACY);
        int maxNearby = readInt(tag, 6, SpawnerNbtHelper.KEY_MAX_NEARBY_SNAKE, SpawnerNbtHelper.KEY_MAX_NEARBY_LEGACY);
        int requiredPlayerRange = readInt(tag, 16, SpawnerNbtHelper.KEY_REQUIRED_PLAYER_RANGE_SNAKE, SpawnerNbtHelper.KEY_REQUIRED_PLAYER_RANGE_LEGACY);
        int spawnRange = readInt(tag, 4, SpawnerNbtHelper.KEY_SPAWN_RANGE_SNAKE, SpawnerNbtHelper.KEY_SPAWN_RANGE_LEGACY);

        delayEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_delay"), Math.max(0, delay), value -> {
        }, value -> value >= 0);
        minSpawnDelayEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_min_delay"), Math.max(0, minDelay), value -> {
        }, value -> value >= 0);
        maxSpawnDelayEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_max_delay"), Math.max(1, maxDelay), value -> {
        }, value -> value >= 1);
        spawnCountEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_count"), Math.max(1, spawnCount), value -> {
        }, value -> value >= 1);
        maxNearbyEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_max_nearby"), Math.max(0, maxNearby), value -> {
        }, value -> value >= 0);
        requiredPlayerRangeEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_required_player_range"),
                Math.max(1, requiredPlayerRange), value -> {
        }, value -> value >= 1);
        spawnRangeEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_spawn_range"), Math.max(0, spawnRange), value -> {
        }, value -> value >= 0);

        getEntries().add(entityEntry);
        getEntries().add(delayEntry);
        getEntries().add(minSpawnDelayEntry);
        getEntries().add(maxSpawnDelayEntry);
        getEntries().add(spawnCountEntry);
        getEntries().add(maxNearbyEntry);
        getEntries().add(requiredPlayerRangeEntry);
        getEntries().add(spawnRangeEntry);

        CompoundTag rules = readCompound(rawSpawnData, SpawnerNbtHelper.KEY_CUSTOM_RULES_SNAKE, SpawnerNbtHelper.KEY_CUSTOM_RULES_LEGACY);
        int[] blockLight = SpawnerNbtHelper.readRange(rules, SpawnerNbtHelper.KEY_BLOCK_LIGHT);
        int[] skyLight = SpawnerNbtHelper.readRange(rules, SpawnerNbtHelper.KEY_SKY_LIGHT);
        customRulesEnabled = new BooleanEntryModel(this, ModTexts.gui("spawner_custom_rules"), !rules.isEmpty(), this::updateCustomRulesEntries);
        blockLightMinEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_block_light_min"), clampLight(blockLight[0]), value -> {
        }, SpawnerNbtHelper.INT_LIGHT);
        blockLightMaxEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_block_light_max"), clampLight(blockLight[1]), value -> {
        }, SpawnerNbtHelper.INT_LIGHT);
        skyLightMinEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_sky_light_min"), clampLight(skyLight[0]), value -> {
        }, SpawnerNbtHelper.INT_LIGHT);
        skyLightMaxEntry = new IntegerEntryModel(this, ModTexts.gui("spawner_sky_light_max"), clampLight(skyLight[1]), value -> {
        }, SpawnerNbtHelper.INT_LIGHT);
        getEntries().add(customRulesEnabled);
        if (customRulesEnabled.getValue()) {
            insertCustomRuleEntries();
        }

        CompoundTag equipTag = readCompound(rawSpawnData, SpawnerNbtHelper.KEY_EQUIPMENT, SpawnerNbtHelper.KEY_EQUIPMENT);
        CompoundTag dropChances = equipTag.isEmpty() ? null : equipTag.getCompound(SpawnerNbtHelper.KEY_SLOT_DROP_CHANCES).orElse(null);
        equipmentEnabled = new BooleanEntryModel(this, ModTexts.gui("spawner_equipment"), !equipTag.isEmpty(), this::updateEquipmentEntries);
        equipmentLootTableEntry = new LootTableSelectionEntryModel(this,
                equipTag.isEmpty() ? "" : equipTag.getStringOr(SpawnerNbtHelper.KEY_LOOT_TABLE, ""), value -> {
        });
        mainhandDropChanceEntry = new FloatEntryModel(this, ModTexts.gui("spawner_equipment_slot_mainhand"), readDropChance(dropChances, "mainhand"), value -> {
        }, SpawnerNbtHelper.FLOAT_DROP);
        offhandDropChanceEntry = new FloatEntryModel(this, ModTexts.gui("spawner_equipment_slot_offhand"), readDropChance(dropChances, "offhand"), value -> {
        }, SpawnerNbtHelper.FLOAT_DROP);
        headDropChanceEntry = new FloatEntryModel(this, ModTexts.gui("spawner_equipment_slot_head"), readDropChance(dropChances, "head"), value -> {
        }, SpawnerNbtHelper.FLOAT_DROP);
        chestDropChanceEntry = new FloatEntryModel(this, ModTexts.gui("spawner_equipment_slot_chest"), readDropChance(dropChances, "chest"), value -> {
        }, SpawnerNbtHelper.FLOAT_DROP);
        legsDropChanceEntry = new FloatEntryModel(this, ModTexts.gui("spawner_equipment_slot_legs"), readDropChance(dropChances, "legs"), value -> {
        }, SpawnerNbtHelper.FLOAT_DROP);
        feetDropChanceEntry = new FloatEntryModel(this, ModTexts.gui("spawner_equipment_slot_feet"), readDropChance(dropChances, "feet"), value -> {
        }, SpawnerNbtHelper.FLOAT_DROP);
        getEntries().add(equipmentEnabled);
        if (equipmentEnabled.getValue()) {
            insertEquipmentEntries();
        }
        refreshEntityValidity();
    }

    private void updateCustomRulesEntries(boolean enabled) {
        if (enabled) {
            insertCustomRuleEntries();
        } else {
            removeCustomRuleEntries();
        }
    }

    private void insertCustomRuleEntries() {
        if (getEntries().contains(blockLightMinEntry)) {
            return;
        }
        int index = getEntries().indexOf(customRulesEnabled);
        if (index < 0) {
            index = getEntries().size();
        }
        getEntries().add(index + 1, blockLightMinEntry);
        getEntries().add(index + 2, blockLightMaxEntry);
        getEntries().add(index + 3, skyLightMinEntry);
        getEntries().add(index + 4, skyLightMaxEntry);
    }

    private void removeCustomRuleEntries() {
        getEntries().remove(blockLightMinEntry);
        getEntries().remove(blockLightMaxEntry);
        getEntries().remove(skyLightMinEntry);
        getEntries().remove(skyLightMaxEntry);
    }

    private void updateEquipmentEntries(boolean enabled) {
        if (enabled) {
            insertEquipmentEntries();
        } else {
            removeEquipmentEntries();
        }
    }

    private void insertEquipmentEntries() {
        if (getEntries().contains(equipmentLootTableEntry)) {
            return;
        }
        int index = getEntries().indexOf(equipmentEnabled);
        if (index < 0) {
            index = getEntries().size();
        }
        getEntries().add(index + 1, equipmentLootTableEntry);
        getEntries().add(index + 2, mainhandDropChanceEntry);
        getEntries().add(index + 3, offhandDropChanceEntry);
        getEntries().add(index + 4, headDropChanceEntry);
        getEntries().add(index + 5, chestDropChanceEntry);
        getEntries().add(index + 6, legsDropChanceEntry);
        getEntries().add(index + 7, feetDropChanceEntry);
    }

    private void removeEquipmentEntries() {
        getEntries().remove(equipmentLootTableEntry);
        getEntries().remove(mainhandDropChanceEntry);
        getEntries().remove(offhandDropChanceEntry);
        getEntries().remove(headDropChanceEntry);
        getEntries().remove(chestDropChanceEntry);
        getEntries().remove(legsDropChanceEntry);
        getEntries().remove(feetDropChanceEntry);
    }

    private static int clampLight(int value) {
        return Math.max(0, Math.min(15, value));
    }

    private static float readDropChance(CompoundTag dropChances, String slot) {
        return dropChances == null ? 0.085f : dropChances.getFloatOr(slot, 0.085f);
    }

    private void writeCustomRules(CompoundTag spawnData) {
        if (customRulesEnabled.getValue()) {
            CompoundTag rules = new CompoundTag();
            SpawnerNbtHelper.writeRange(rules, SpawnerNbtHelper.KEY_BLOCK_LIGHT, blockLightMinEntry.getValue(), blockLightMaxEntry.getValue());
            SpawnerNbtHelper.writeRange(rules, SpawnerNbtHelper.KEY_SKY_LIGHT, skyLightMinEntry.getValue(), skyLightMaxEntry.getValue());
            spawnData.put(SpawnerNbtHelper.KEY_CUSTOM_RULES_SNAKE, rules);
            spawnData.remove(SpawnerNbtHelper.KEY_CUSTOM_RULES_LEGACY);
        } else {
            spawnData.remove(SpawnerNbtHelper.KEY_CUSTOM_RULES_SNAKE);
            spawnData.remove(SpawnerNbtHelper.KEY_CUSTOM_RULES_LEGACY);
        }
    }

    private void writeEquipment(CompoundTag spawnData) {
        String lootTable = equipmentLootTableEntry.getValue() == null ? "" : equipmentLootTableEntry.getValue().trim();
        if (equipmentEnabled.getValue() && !lootTable.isEmpty()) {
            CompoundTag equip = new CompoundTag();
            equip.putString(SpawnerNbtHelper.KEY_LOOT_TABLE, lootTable);
            CompoundTag drops = new CompoundTag();
            drops.putFloat("mainhand", mainhandDropChanceEntry.getValue());
            drops.putFloat("offhand", offhandDropChanceEntry.getValue());
            drops.putFloat("head", headDropChanceEntry.getValue());
            drops.putFloat("chest", chestDropChanceEntry.getValue());
            drops.putFloat("legs", legsDropChanceEntry.getValue());
            drops.putFloat("feet", feetDropChanceEntry.getValue());
            equip.put(SpawnerNbtHelper.KEY_SLOT_DROP_CHANCES, drops);
            spawnData.put(SpawnerNbtHelper.KEY_EQUIPMENT, equip);
        } else {
            spawnData.remove(SpawnerNbtHelper.KEY_EQUIPMENT);
        }
    }

    @Override
    public void apply() {
        super.apply();
        CompoundTag tag = ensureTag();

        int delay = Math.max(0, delayEntry.getValue());
        int minDelay = Math.max(0, minSpawnDelayEntry.getValue());
        int maxDelay = Math.max(1, maxSpawnDelayEntry.getValue());
        if (maxDelay < minDelay) {
            maxDelay = minDelay;
        }
        int spawnCount = Math.max(1, spawnCountEntry.getValue());
        int maxNearby = Math.max(0, maxNearbyEntry.getValue());
        int requiredPlayerRange = Math.max(1, requiredPlayerRangeEntry.getValue());
        int spawnRange = Math.max(0, spawnRangeEntry.getValue());

        writeInt(tag, SpawnerNbtHelper.KEY_DELAY_SNAKE, SpawnerNbtHelper.KEY_DELAY_LEGACY, delay, preferSnakeCase);
        writeInt(tag, SpawnerNbtHelper.KEY_MIN_DELAY_SNAKE, SpawnerNbtHelper.KEY_MIN_DELAY_LEGACY, minDelay, preferSnakeCase);
        writeInt(tag, SpawnerNbtHelper.KEY_MAX_DELAY_SNAKE, SpawnerNbtHelper.KEY_MAX_DELAY_LEGACY, maxDelay, preferSnakeCase);
        writeInt(tag, SpawnerNbtHelper.KEY_SPAWN_COUNT_SNAKE, SpawnerNbtHelper.KEY_SPAWN_COUNT_LEGACY, spawnCount, preferSnakeCase);
        writeInt(tag, SpawnerNbtHelper.KEY_MAX_NEARBY_SNAKE, SpawnerNbtHelper.KEY_MAX_NEARBY_LEGACY, maxNearby, preferSnakeCase);
        writeInt(tag, SpawnerNbtHelper.KEY_REQUIRED_PLAYER_RANGE_SNAKE, SpawnerNbtHelper.KEY_REQUIRED_PLAYER_RANGE_LEGACY, requiredPlayerRange, preferSnakeCase);
        writeInt(tag, SpawnerNbtHelper.KEY_SPAWN_RANGE_SNAKE, SpawnerNbtHelper.KEY_SPAWN_RANGE_LEGACY, spawnRange, preferSnakeCase);

        CompoundTag entity = sanitizeEntityData(entityEntry.copyValue());
        String selectedEntityId = normalizeEntityId(entity.getStringOr("id", ""));
        boolean entityChanged = !selectedEntityId.equals(initialEntityId);
        if (entity.isEmpty() || !entity.contains("id")) {
            tag.remove(SpawnerNbtHelper.KEY_SPAWN_DATA_SNAKE);
            tag.remove(SpawnerNbtHelper.KEY_SPAWN_DATA_LEGACY);
        } else {
            CompoundTag spawnData = spawnDataExtras == null ? new CompoundTag() : spawnDataExtras.copy();
            spawnData.put("entity", entity);
            spawnData.remove("Entity");
            writeCustomRules(spawnData);
            writeEquipment(spawnData);
            writeCompound(tag, SpawnerNbtHelper.KEY_SPAWN_DATA_SNAKE, SpawnerNbtHelper.KEY_SPAWN_DATA_LEGACY, spawnData, preferSnakeCase);
        }

        if (entityChanged) {
            tag.remove(SpawnerNbtHelper.KEY_SPAWN_POTENTIALS_SNAKE);
            tag.remove(SpawnerNbtHelper.KEY_SPAWN_POTENTIALS_LEGACY);
            tag.remove(SpawnerNbtHelper.KEY_NEXT_SPAWN_DATA_SNAKE);
            tag.remove(SpawnerNbtHelper.KEY_NEXT_SPAWN_DATA_LEGACY);
        }
        DebugLog.info(() -> "[SpawnerModel] apply selectedEntity=" + selectedEntityId
                + " changed=" + entityChanged
                + " preferSnake=" + preferSnakeCase
                + " hasSpawnDataSnake=" + tag.contains(SpawnerNbtHelper.KEY_SPAWN_DATA_SNAKE)
                + " hasSpawnDataLegacy=" + tag.contains(SpawnerNbtHelper.KEY_SPAWN_DATA_LEGACY)
                + " hasPotentials=" + (tag.contains(SpawnerNbtHelper.KEY_SPAWN_POTENTIALS_SNAKE) || tag.contains(SpawnerNbtHelper.KEY_SPAWN_POTENTIALS_LEGACY)));

        initialEntityId = selectedEntityId;
        getContext().setTag(tag);
    }

    private CompoundTag ensureTag() {
        CompoundTag tag = getData();
        if (tag == null) {
            tag = new CompoundTag();
            getContext().setTag(tag);
        }
        return tag;
    }

    private void refreshEntityValidity() {
        String id = entityEntry.getEntityId();
        if (id == null || id.isBlank()) {
            entityEntry.setValid(true);
        }
    }
}
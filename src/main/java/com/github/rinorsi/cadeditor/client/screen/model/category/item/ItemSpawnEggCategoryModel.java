package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntityEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;


public class ItemSpawnEggCategoryModel extends ItemEditorCategoryModel {
    private static final Set<String> TRANSIENT_ENTITY_TAG_KEYS = Set.of("Pos", "Motion", "Rotation", "UUID", "UUIDMost", "UUIDLeast");
    private static final String EQUIPMENT_TAG = "equipment";
    private static final String DROP_CHANCES_TAG = "drop_chances";
    private static final String LEGACY_HAND_ITEMS_TAG = "HandItems";
    private static final String LEGACY_HAND_DROPS_TAG = "HandDropChances";
    private static final String LEGACY_ARMOR_ITEMS_TAG = "ArmorItems";
    private static final String LEGACY_ARMOR_DROPS_TAG = "ArmorDropChances";
    private static final String VILLAGER_DATA_TAG = "VillagerData";
    private static final String OFFERS_TAG = "Offers";
    private static final String RECIPES_TAG = "Recipes";
    private static final String ASSIGN_PROFESSION_WHEN_SPAWNED_TAG = "AssignProfessionWhenSpawned";
    private static final String XP_TAG = "Xp";
    private static final String NONE_PROFESSION = "minecraft:none";
    private static final String DEFAULT_TRADING_PROFESSION = "minecraft:farmer";
    private static final String DEFAULT_VILLAGER_TYPE = "minecraft:plains";

    private static final float DEFAULT_DROP_CHANCE = 0.085f;
    private static final float DROP_EPSILON = 1.0E-4f;
    private final SpawnEggItem item;
    private CompoundTag spawnData;
    private CompoundTag initialSerializedData;
    private CompoundTag initialEditorData;
    private EntityEntryModel entityEntry;

    public ItemSpawnEggCategoryModel(ItemEditorModel editor, SpawnEggItem item) {
        super(ModTexts.SPAWN_EGG, editor);
        this.initialSerializedData = new CompoundTag();
        this.initialEditorData = new CompoundTag();
        this.item = item;
        this.spawnData = readSpawnData(editor.getContext().getItemStack(), editor.getContext().getTag());
    }

    @Override 
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        HolderLookup.Provider registries = ClientUtil.registryAccess();
        CompoundTag editorData = prepareEditorData(this.spawnData, stack);
        ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING, registries, editorData);
        Optional<EntityType<?>> entityType = EntityType.by(valueInput);
        this.entityEntry = new EntityEntryModel(this, entityType.orElse(SpawnEggItem.getType(stack)), editorData, value -> {
        });
        getEntries().add(this.entityEntry.withWeight(0));
        this.initialSerializedData = this.spawnData == null ? new CompoundTag() : this.spawnData.copy();
        this.initialEditorData = editorData.copy();
    }

    @Override 
    public void apply() {
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        CompoundTag editorValue = sanitizeEntityData(this.entityEntry == null ? this.spawnData : this.entityEntry.copyValue());
        CompoundTag selectedData = editorValue.equals(this.initialEditorData) ? this.initialSerializedData.copy() : editorValue.copy();
        enforceVillagerLevelFallbackOnProfessionChange(selectedData, this.initialSerializedData);
        CompoundTag sanitizedData = sanitizeSpawnEggComponentPayload(selectedData);
        this.spawnData = sanitizedData.copy();
        CompoundTag itemData = getData();
        CompoundTag legacyTag = itemData.getCompound("tag").orElse(null);
        if (legacyTag != null && legacyTag.contains("EntityTag")) {
            legacyTag.remove("EntityTag");
            if (legacyTag.isEmpty()) {
                itemData.remove("tag");
            }
        }
        if (sanitizedData.isEmpty() || !sanitizedData.contains("id") || sanitizedData.getString("id").orElse("").isEmpty()) {
            stack.remove(DataComponents.ENTITY_DATA);
            this.spawnData = new CompoundTag();
            this.initialSerializedData = new CompoundTag();
            this.initialEditorData = new CompoundTag();
            return;
        }
        EntityType<?> entityType = resolveEntityType(stack, sanitizedData);
        CompoundTag componentPayload = sanitizedData.copy();
        componentPayload.remove("id");
        stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(entityType, componentPayload));
        this.spawnData = componentPayload.copy();
        this.spawnData.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
        this.initialSerializedData = this.spawnData.copy();
        this.initialEditorData = editorValue.copy();
    }

    private static CompoundTag readSpawnData(ItemStack stack, CompoundTag rootTag) {
        CompoundTag legacy;
        CompoundTag entityTag;
        TypedEntityData<EntityType<?>> data;
        if (stack != null && (data = (TypedEntityData) stack.get(DataComponents.ENTITY_DATA)) != null) {
            CompoundTag tag = data.copyTagWithoutId();
            tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey((EntityType) data.type()).toString());
            return tag;
        }
        if (rootTag != null && (legacy = rootTag.getCompound("tag").orElse(null)) != null && (entityTag = legacy.getCompound("EntityTag").orElse(null)) != null) {
            return entityTag.copy();
        }
        return new CompoundTag();
    }

    private CompoundTag prepareEditorData(CompoundTag source, ItemStack stack) {
        CompoundTag normalized = sanitizeSpawnEggComponentPayload(source);
        EntityType<?> type = resolveEntityType(stack, normalized);
        Identifier key = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (key != null) {
            normalized.putString("id", key.toString());
        }
        return normalized;
    }

    private static CompoundTag sanitizeEntityData(CompoundTag source) {
        if (source == null || source.isEmpty()) {
            return new CompoundTag();
        }
        CompoundTag sanitized = source.copy();
        String id = sanitized.getStringOr("id", "").trim();
        if (id.isEmpty()) {
            sanitized.remove("id");
            return sanitized;
        }
        Identifier parsed = ClientUtil.parseResourceLocation(id);
        sanitized.putString("id", parsed == null ? id : parsed.toString());
        return sanitized;
    }

    static CompoundTag sanitizeSpawnEggComponentPayload(CompoundTag source) {
        CompoundTag sanitized = sanitizeEntityData(source);
        if (sanitized.isEmpty()) {
            return sanitized;
        }
        migrateLegacyEquipmentData(sanitized);
        normalizeVillagerTradeData(sanitized);
        for (String key : TRANSIENT_ENTITY_TAG_KEYS) {
            sanitized.remove(key);
        }
        float health = sanitized.getFloatOr("Health", Float.NaN);
        if (!Float.isNaN(health) && health <= 0.0f) {
            sanitized.remove("Health");
        }
        return sanitized;
    }

    private EntityType<?> resolveEntityType(ItemStack stack, CompoundTag data) {
        String id = data == null ? "" : data.getString("id").orElse("");
        Identifier parsed = ClientUtil.parseResourceLocation(id);
        if (parsed != null && BuiltInRegistries.ENTITY_TYPE.containsKey(parsed)) {
            return (EntityType) BuiltInRegistries.ENTITY_TYPE.getValue(parsed);
        }
        SpawnEggItem spawnEggItem = this.item;
        return SpawnEggItem.getType(stack);
    }

    private static void migrateLegacyEquipmentData(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        boolean hasLegacy = tag.contains(LEGACY_HAND_ITEMS_TAG) || tag.contains(LEGACY_HAND_DROPS_TAG) || tag.contains(LEGACY_ARMOR_ITEMS_TAG) || tag.contains(LEGACY_ARMOR_DROPS_TAG);
        if (!hasLegacy) {
            return;
        }
        CompoundTag equipment = tag.getCompound(EQUIPMENT_TAG).map(value -> value.copy()).orElseGet(CompoundTag::new);
        CompoundTag dropChances = tag.getCompound(DROP_CHANCES_TAG).map(value -> value.copy()).orElseGet(CompoundTag::new);
        migrateLegacySlot(tag, equipment, dropChances, "mainhand", LEGACY_HAND_ITEMS_TAG, LEGACY_HAND_DROPS_TAG, 0);
        migrateLegacySlot(tag, equipment, dropChances, "offhand", LEGACY_HAND_ITEMS_TAG, LEGACY_HAND_DROPS_TAG, 1);
        migrateLegacySlot(tag, equipment, dropChances, "feet", LEGACY_ARMOR_ITEMS_TAG, LEGACY_ARMOR_DROPS_TAG, 0);
        migrateLegacySlot(tag, equipment, dropChances, "legs", LEGACY_ARMOR_ITEMS_TAG, LEGACY_ARMOR_DROPS_TAG, 1);
        migrateLegacySlot(tag, equipment, dropChances, "chest", LEGACY_ARMOR_ITEMS_TAG, LEGACY_ARMOR_DROPS_TAG, 2);
        migrateLegacySlot(tag, equipment, dropChances, "head", LEGACY_ARMOR_ITEMS_TAG, LEGACY_ARMOR_DROPS_TAG, 3);
        if (equipment.isEmpty()) {
            tag.remove(EQUIPMENT_TAG);
        } else {
            tag.put(EQUIPMENT_TAG, equipment);
        }
        if (dropChances.isEmpty()) {
            tag.remove(DROP_CHANCES_TAG);
        } else {
            tag.put(DROP_CHANCES_TAG, dropChances);
        }
        tag.remove(LEGACY_HAND_ITEMS_TAG);
        tag.remove(LEGACY_HAND_DROPS_TAG);
        tag.remove(LEGACY_ARMOR_ITEMS_TAG);
        tag.remove(LEGACY_ARMOR_DROPS_TAG);
    }

    private static void normalizeVillagerTradeData(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return;
        }
        CompoundTag villagerData = tag.getCompound(VILLAGER_DATA_TAG).orElse(null);
        boolean hasOffers = hasCustomOffers(tag);
        if (villagerData == null) {
            if (!hasOffers) {
                return;
            } else {
                villagerData = new CompoundTag();
            }
        }
        String profession = normalizeId(villagerData.getStringOr("profession", ""), NONE_PROFESSION);
        if (hasOffers && NONE_PROFESSION.equals(profession)) {
            profession = DEFAULT_TRADING_PROFESSION;
        }
        villagerData.putString("profession", profession);
        villagerData.putString("type", normalizeId(villagerData.getStringOr("type", ""), DEFAULT_VILLAGER_TYPE));
        villagerData.putInt("level", clampVillagerLevel(villagerData.getIntOr("level", 1)));
        tag.put(VILLAGER_DATA_TAG, villagerData);
        if (hasOffers || !NONE_PROFESSION.equals(profession)) {
            tag.putBoolean(ASSIGN_PROFESSION_WHEN_SPAWNED_TAG, false);
        }
        if (!NONE_PROFESSION.equals(profession) && !hasOffers && tag.getIntOr(XP_TAG, 0) < 1) {
            tag.putInt(XP_TAG, 1);
        }
    }

    static void enforceVillagerLevelFallbackOnProfessionChange(CompoundTag current, CompoundTag baseline) {
        CompoundTag currentVillagerData;
        String normalizedBaselineProfession;
        if (current == null || current.isEmpty() || (currentVillagerData = current.getCompound(VILLAGER_DATA_TAG).orElse(null)) == null) {
            return;
        }
        String currentProfession = normalizeId(currentVillagerData.getStringOr("profession", ""), NONE_PROFESSION);
        if (NONE_PROFESSION.equals(currentProfession)) {
            return;
        }
        CompoundTag baselineVillagerData = baseline == null ? null : baseline.getCompound(VILLAGER_DATA_TAG).orElse(null);
        if (baselineVillagerData == null) {
            normalizedBaselineProfession = NONE_PROFESSION;
        } else {
            normalizedBaselineProfession = normalizeId(baselineVillagerData.getStringOr("profession", ""), NONE_PROFESSION);
        }
        String baselineProfession = normalizedBaselineProfession;
        if (currentProfession.equals(baselineProfession)) {
            return;
        }
        int level = clampVillagerLevel(currentVillagerData.getIntOr("level", 1));
        if (level < 2) {
            currentVillagerData.putInt("level", 2);
            current.put(VILLAGER_DATA_TAG, currentVillagerData);
        }
    }

    private static boolean hasCustomOffers(CompoundTag root) {
        ListTag recipes;
        CompoundTag offers = root.getCompound(OFFERS_TAG).orElse(null);
        return offers != null && (recipes = offers.getList(RECIPES_TAG).orElse(null)) != null && !recipes.isEmpty();
    }

    private static String normalizeId(String value, String defaultValue) {
        String result = (value == null || value.isBlank()) ? defaultValue : value;
        if (!result.contains(":")) {
            result = "minecraft:" + result;
        }
        return result;
    }

    private static int clampVillagerLevel(int level) {
        if (level < 1) {
            return 1;
        }
        if (level > 5) {
            return 5;
        }
        return level;
    }

    private static void migrateLegacySlot(CompoundTag root, CompoundTag equipment, CompoundTag dropChances, String slotKey, String legacyItemsKey, String legacyDropsKey, int index) {
        Float chance;
        CompoundTag legacyItem;
        if (!equipment.contains(slotKey) && (legacyItem = readLegacyItem(root, legacyItemsKey, index)) != null && !legacyItem.isEmpty()) {
            equipment.put(slotKey, legacyItem);
        }
        if (!dropChances.contains(slotKey) && (chance = readLegacyDropChance(root, legacyDropsKey, index)) != null && Float.isFinite(chance.floatValue()) && chance.floatValue() >= 0.0f && chance.floatValue() <= 1.0f && Math.abs(chance.floatValue() - 0.085f) > 1.0E-4f) {
            dropChances.putFloat(slotKey, chance.floatValue());
        }
    }

    private static CompoundTag readLegacyItem(CompoundTag root, String key, int index) {
        ListTag list = (ListTag) root.getList(key).orElse(null);
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        CompoundTag compoundTag = (CompoundTag) list.get(index);
        if (!(compoundTag instanceof CompoundTag)) {
            return null;
        }
        CompoundTag compound = compoundTag;
        return compound.copy();
    }

    private static Float readLegacyDropChance(CompoundTag root, String key, int index) {
        ListTag list = (ListTag) root.getList(key).orElse(null);
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        FloatTag floatTag = (FloatTag) list.get(index);
        if (floatTag instanceof FloatTag) {
            FloatTag chanceTag = floatTag;
            return chanceTag.floatValue();
        }
        return null;
    }
}

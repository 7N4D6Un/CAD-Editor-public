package com.github.rinorsi.cadeditor.client.screen.model.category;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;
import java.util.function.Predicate;


public final class SpawnerNbtHelper {
    public static final String KEY_SPAWN_DATA_SNAKE = "spawn_data";
    public static final String KEY_SPAWN_DATA_LEGACY = "SpawnData";
    private static final String[] ENTITY_KEYS = {"entity", "Entity"};

    public static final String KEY_DELAY_SNAKE = "delay";
    public static final String KEY_DELAY_LEGACY = "Delay";
    public static final String KEY_MIN_DELAY_SNAKE = "min_spawn_delay";
    public static final String KEY_MIN_DELAY_LEGACY = "MinSpawnDelay";
    public static final String KEY_MAX_DELAY_SNAKE = "max_spawn_delay";
    public static final String KEY_MAX_DELAY_LEGACY = "MaxSpawnDelay";
    public static final String KEY_SPAWN_COUNT_SNAKE = "spawn_count";
    public static final String KEY_SPAWN_COUNT_LEGACY = "SpawnCount";
    public static final String KEY_MAX_NEARBY_SNAKE = "max_nearby_entities";
    public static final String KEY_MAX_NEARBY_LEGACY = "MaxNearbyEntities";
    public static final String KEY_REQUIRED_PLAYER_RANGE_SNAKE = "required_player_range";
    public static final String KEY_REQUIRED_PLAYER_RANGE_LEGACY = "RequiredPlayerRange";
    public static final String KEY_SPAWN_RANGE_SNAKE = "spawn_range";
    public static final String KEY_SPAWN_RANGE_LEGACY = "SpawnRange";
    public static final String KEY_SPAWN_POTENTIALS_SNAKE = "spawn_potentials";
    public static final String KEY_SPAWN_POTENTIALS_LEGACY = "SpawnPotentials";
    public static final String KEY_NEXT_SPAWN_DATA_SNAKE = "next_spawn_data";
    public static final String KEY_NEXT_SPAWN_DATA_LEGACY = "NextSpawnData";

    public static final String KEY_CUSTOM_RULES_SNAKE = "custom_spawn_rules";
    public static final String KEY_CUSTOM_RULES_LEGACY = "CustomSpawnRules";
    public static final String KEY_BLOCK_LIGHT = "block_light_limit";
    public static final String KEY_SKY_LIGHT = "sky_light_limit";
    public static final String KEY_EQUIPMENT = "equipment";
    public static final String KEY_LOOT_TABLE = "loot_table";
    public static final String KEY_SLOT_DROP_CHANCES = "slot_drop_chances";
    public static final String KEY_RANGE_MIN = "min_inclusive";
    public static final String KEY_RANGE_MAX = "max_inclusive";

    public static final Predicate<Integer> INT_LIGHT = value -> value != null && value >= 0 && value <= 15;
    public static final Predicate<Float> FLOAT_DROP = value -> value != null && value >= 0.0f && value <= 1.0f;

    private SpawnerNbtHelper() {
    }

    public static EntityType<?> resolveEntityType(CompoundTag entityData) {
        if (entityData == null || entityData.isEmpty()) {
            return null;
        }
        String id = entityData.getStringOr("id", "");
        Identifier location = ClientUtil.parseResourceLocation(id);
        return location == null ? null : BuiltInRegistries.ENTITY_TYPE.getOptional(location).orElse(null);
    }

    public static CompoundTag extractEntityData(CompoundTag spawnData) {
        if (spawnData == null || spawnData.isEmpty()) {
            return new CompoundTag();
        }
        for (String key : ENTITY_KEYS) {
            if (spawnData.contains(key)) {
                return spawnData.getCompound(key).map(CompoundTag::copy).orElseGet(CompoundTag::new);
            }
        }
        if (looksLikeEntityData(spawnData)) {
            return spawnData.copy();
        }
        return new CompoundTag();
    }

    public static CompoundTag extractSpawnDataExtras(CompoundTag spawnData) {
        if (spawnData == null || spawnData.isEmpty()) {
            return new CompoundTag();
        }
        if (looksLikeEntityData(spawnData) && !spawnData.contains("entity") && !spawnData.contains("Entity")) {
            return new CompoundTag();
        }
        CompoundTag extras = spawnData.copy();
        extras.remove("entity");
        extras.remove("Entity");
        return extras;
    }

    public static boolean looksLikeEntityData(CompoundTag tag) {
        return tag.contains("id") || tag.contains("Pos") || tag.contains("Health") || tag.contains("Passengers");
    }

    public static CompoundTag sanitizeEntityData(CompoundTag entityData) {
        if (entityData == null || entityData.isEmpty()) {
            return new CompoundTag();
        }
        CompoundTag sanitized = entityData.copy();
        String id = sanitized.getStringOr("id", "").trim();
        if (id.isEmpty()) {
            sanitized.remove("id");
            return sanitized;
        }
        Identifier parsed = ClientUtil.parseResourceLocation(id);
        if (parsed != null) {
            sanitized.putString("id", parsed.toString());
        } else {
            sanitized.putString("id", id);
        }
        return sanitized;
    }

    public static int readInt(CompoundTag tag, int fallback, String primaryKey, String secondaryKey) {
        if (tag == null) {
            return fallback;
        }
        if (tag.contains(primaryKey)) {
            return tag.getIntOr(primaryKey, fallback);
        }
        if (tag.contains(secondaryKey)) {
            return tag.getIntOr(secondaryKey, fallback);
        }
        return fallback;
    }

    public static CompoundTag readCompound(CompoundTag tag, String primaryKey, String secondaryKey) {
        if (tag == null) {
            return new CompoundTag();
        }
        if (tag.contains(primaryKey)) {
            return tag.getCompound(primaryKey).map(CompoundTag::copy).orElseGet(CompoundTag::new);
        }
        if (tag.contains(secondaryKey)) {
            return tag.getCompound(secondaryKey).map(CompoundTag::copy).orElseGet(CompoundTag::new);
        }
        return new CompoundTag();
    }

    public static void writeInt(CompoundTag tag, String snakeKey, String legacyKey, int value, boolean useSnakeKey) {
        if (tag == null) {
            return;
        }
        String target = useSnakeKey ? snakeKey : legacyKey;
        String other = useSnakeKey ? legacyKey : snakeKey;
        tag.putInt(target, value);
        tag.remove(other);
    }

    public static void writeCompound(CompoundTag tag, String snakeKey, String legacyKey, CompoundTag value, boolean useSnakeKey) {
        if (tag == null) {
            return;
        }
        String target = useSnakeKey ? snakeKey : legacyKey;
        String other = useSnakeKey ? legacyKey : snakeKey;
        tag.put(target, value);
        tag.remove(other);
    }

    public static boolean detectSnakeCase(CompoundTag tag) {
        if (tag == null) {
            return false;
        }
        if (tag.contains(KEY_SPAWN_DATA_SNAKE) || tag.contains(KEY_DELAY_SNAKE) || tag.contains(KEY_MIN_DELAY_SNAKE)
                || tag.contains(KEY_MAX_DELAY_SNAKE) || tag.contains(KEY_SPAWN_COUNT_SNAKE)) {
            return true;
        }
        if (tag.contains(KEY_SPAWN_DATA_LEGACY) || tag.contains(KEY_DELAY_LEGACY) || tag.contains(KEY_MIN_DELAY_LEGACY)
                || tag.contains(KEY_MAX_DELAY_LEGACY) || tag.contains(KEY_SPAWN_COUNT_LEGACY)) {
            return false;
        }
        return false;
    }

    public static String normalizeEntityId(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        Identifier parsed = ClientUtil.parseResourceLocation(trimmed);
        return parsed == null ? trimmed : parsed.toString();
    }

    public static boolean isEmptyDefaultSpawner(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return true;
        }
        CompoundTag spawnData = readCompound(tag, KEY_SPAWN_DATA_SNAKE, KEY_SPAWN_DATA_LEGACY);
        if (!extractEntityData(spawnData).getString("id").orElse("").isBlank()) {
            return false;
        }
        if (tag.contains(KEY_SPAWN_POTENTIALS_SNAKE) || tag.contains(KEY_SPAWN_POTENTIALS_LEGACY)) {
            return false;
        }
        if (tag.contains(KEY_NEXT_SPAWN_DATA_SNAKE) || tag.contains(KEY_NEXT_SPAWN_DATA_LEGACY)) {
            return false;
        }
        return readInt(tag, 20, KEY_DELAY_SNAKE, KEY_DELAY_LEGACY) == 20
                && readInt(tag, 200, KEY_MIN_DELAY_SNAKE, KEY_MIN_DELAY_LEGACY) == 200
                && readInt(tag, 800, KEY_MAX_DELAY_SNAKE, KEY_MAX_DELAY_LEGACY) == 800
                && readInt(tag, 4, KEY_SPAWN_COUNT_SNAKE, KEY_SPAWN_COUNT_LEGACY) == 4
                && readInt(tag, 6, KEY_MAX_NEARBY_SNAKE, KEY_MAX_NEARBY_LEGACY) == 6
                && readInt(tag, 16, KEY_REQUIRED_PLAYER_RANGE_SNAKE, KEY_REQUIRED_PLAYER_RANGE_LEGACY) == 16
                && readInt(tag, 4, KEY_SPAWN_RANGE_SNAKE, KEY_SPAWN_RANGE_LEGACY) == 4;
    }

    public static int[] readRange(CompoundTag parent, String key) {
        if (parent == null || !parent.contains(key)) {
            return new int[]{0, 15};
        }
        Tag value = parent.get(key);
        if (value instanceof NumericTag numeric) {
            int single = numeric.intValue();
            return new int[]{single, single};
        }
        if (value instanceof CompoundTag compound) {
            int min = compound.contains(KEY_RANGE_MIN) ? compound.getIntOr(KEY_RANGE_MIN, 0) : 0;
            int max = compound.contains(KEY_RANGE_MAX) ? compound.getIntOr(KEY_RANGE_MAX, 15) : 15;
            return new int[]{min, max};
        }
        return new int[]{0, 15};
    }

    public static void writeRange(CompoundTag parent, String key, int min, int max) {
        if (parent == null) {
            return;
        }
        CompoundTag range = new CompoundTag();
        range.putInt(KEY_RANGE_MIN, Math.min(min, max));
        range.putInt(KEY_RANGE_MAX, Math.max(min, max));
        parent.put(key, range);
    }
}
package com.github.rinorsi.cadeditor.client.screen.model.category;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;

public final class TrialSpawnerNbtHelper {
    public static final String KEY_NORMAL_CONFIG = "normal_config";
    public static final String KEY_OMINOUS_CONFIG = "ominous_config";
    public static final String KEY_TARGET_COOLDOWN_LENGTH = "target_cooldown_length";
    public static final String KEY_REQUIRED_PLAYER_RANGE = "required_player_range";
    public static final String KEY_SPAWN_RANGE = "spawn_range";
    public static final String KEY_TOTAL_MOBS = "total_mobs";
    public static final String KEY_SIMULTANEOUS_MOBS = "simultaneous_mobs";
    public static final String KEY_TOTAL_MOBS_ADDED_PER_PLAYER = "total_mobs_added_per_player";
    public static final String KEY_SIMULTANEOUS_MOBS_ADDED_PER_PLAYER = "simultaneous_mobs_added_per_player";
    public static final String KEY_TICKS_BETWEEN_SPAWN = "ticks_between_spawn";
    public static final String KEY_SPAWN_POTENTIALS = "spawn_potentials";
    public static final String KEY_SPAWN_DATA = "spawn_data";
    public static final String KEY_LOOT_TABLES_TO_EJECT = "loot_tables_to_eject";
    public static final String KEY_ITEMS_TO_DROP_WHEN_OMINOUS = "items_to_drop_when_ominous";

    private TrialSpawnerNbtHelper() {
    }

    public static int readInt(CompoundTag config, String key, int def) {
        return config.getIntOr(key, def);
    }

    public static float readFloat(CompoundTag config, String key, float def) {
        return config.getFloatOr(key, def);
    }

    public static void putInt(CompoundTag config, String key, int value) {
        config.putInt(key, value);
    }

    public static void putFloat(CompoundTag config, String key, float value) {
        config.putFloat(key, value);
    }

    public static CompoundTag getConfig(CompoundTag root, String key) {
        return root.getCompound(key).orElse(new CompoundTag());
    }

    public static CompoundTag loadConfig(CompoundTag root, String key) {
        CompoundTag inline = getConfig(root, key);
        if (!inline.isEmpty()) {
            return inline;
        }
        String ref = root.getString(key).orElse("");
        if (!ref.isBlank()) {
            CompoundTag resolved = resolveConfigFromRegistry(ref);
            if (resolved != null) {
                return resolved;
            }
        }
        return encodeConfig(TrialSpawnerConfig.DEFAULT);
    }

    public static void putConfig(CompoundTag root, String key, CompoundTag config) {
        root.put(key, config);
    }

    public static boolean hasDistinctOminous(CompoundTag root) {
        return !loadConfig(root, KEY_NORMAL_CONFIG).equals(loadConfig(root, KEY_OMINOUS_CONFIG));
    }

    public static CompoundTag readSpawnEntity(CompoundTag config) {
        ListTag potentials = config.getList(KEY_SPAWN_POTENTIALS).orElse(null);
        if (potentials == null || potentials.isEmpty()) {
            return new CompoundTag();
        }
        CompoundTag entry = potentials.getCompound(0).orElse(null);
        if (entry == null) {
            return new CompoundTag();
        }
        CompoundTag data = entry.getCompound("data").orElse(null);
        if (data == null) {
            return new CompoundTag();
        }
        CompoundTag entity = data.getCompound("entity").orElse(null);
        return entity == null ? new CompoundTag() : entity.copy();
    }

    public static void writeSpawnEntity(CompoundTag config, CompoundTag entityTag) {
        CompoundTag data = new CompoundTag();
        data.put("entity", entityTag == null ? new CompoundTag() : entityTag.copy());
        CompoundTag entry = new CompoundTag();
        entry.putInt("weight", 1);
        entry.put("data", data);
        ListTag list = new ListTag();
        list.add(entry);
        config.put(KEY_SPAWN_POTENTIALS, list);
    }

    public static String readSingleLootKey(CompoundTag config, String listKey) {
        ListTag list = config.getList(listKey).orElse(null);
        if (list == null || list.isEmpty()) {
            return "";
        }
        CompoundTag entry = list.getCompound(0).orElse(null);
        if (entry == null) {
            return "";
        }
        return entry.getString("data").orElse("");
    }

    public static void writeSingleLootKey(CompoundTag config, String listKey, String id) {
        String trimmed = id == null ? "" : id.trim();
        if (trimmed.isEmpty()) {
            config.remove(listKey);
            return;
        }
        CompoundTag entry = new CompoundTag();
        entry.putInt("weight", 1);
        entry.putString("data", trimmed);
        ListTag list = new ListTag();
        list.add(entry);
        config.put(listKey, list);
    }

    public static void syncSpawnData(CompoundTag root) {
        CompoundTag spawnData = root.getCompound(KEY_SPAWN_DATA).orElse(null);
        if (spawnData == null) {
            return;
        }
        CompoundTag entity = spawnData.getCompound("entity").orElse(null);
        if (entity == null || entity.getStringOr("id", "").isBlank()) {
            root.remove(KEY_SPAWN_DATA);
        }
    }

    private static CompoundTag resolveConfigFromRegistry(String id) {
        Identifier rl = Identifier.tryParse(id);
        if (rl == null) {
            return null;
        }
        Optional<? extends HolderLookup.RegistryLookup<TrialSpawnerConfig>> lookup = ClientUtil.registryAccess().lookup(Registries.TRIAL_SPAWNER_CONFIG);
        return lookup.flatMap(l -> l.get(ResourceKey.create(Registries.TRIAL_SPAWNER_CONFIG, rl)))
                .map(Holder::value)
                .map(TrialSpawnerNbtHelper::encodeConfig)
                .orElse(null);
    }

    private static CompoundTag encodeConfig(TrialSpawnerConfig config) {
        return TrialSpawnerConfig.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, config)
                .result()
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .orElse(new CompoundTag());
    }
}
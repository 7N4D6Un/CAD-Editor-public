package com.github.rinorsi.cadeditor.client.screen.model.category;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.function.Predicate;

public final class BeehiveNbtHelper {
    public static final String KEY_BEES = "bees";
    public static final String KEY_ENTITY_DATA = "entity_data";
    public static final String KEY_TICKS_IN_HIVE = "ticks_in_hive";
    public static final String KEY_MIN_TICKS_IN_HIVE = "min_ticks_in_hive";

    public static final Predicate<Integer> INT_COUNT = value -> value != null && value >= 0;
    public static final Predicate<Integer> INT_HONEY = value -> value != null && value >= 0 && value <= 5;

    private BeehiveNbtHelper() {
    }

    public static CompoundTag defaultBeeOccupant() {
        CompoundTag entityData = new CompoundTag();
        entityData.putString("id", "minecraft:bee");
        CompoundTag occupant = new CompoundTag();
        occupant.put(KEY_ENTITY_DATA, entityData);
        occupant.putInt(KEY_TICKS_IN_HIVE, 0);
        occupant.putInt(KEY_MIN_TICKS_IN_HIVE, 600);
        return occupant;
    }

    public static ListTag resizeBees(ListTag bees, int target) {
        ListTag result = new ListTag();
        int current = bees == null ? 0 : bees.size();
        int keep = Math.min(current, Math.max(0, target));
        if (bees != null) {
            for (int i = 0; i < keep; i++) {
                if (bees.get(i) instanceof CompoundTag c) {
                    result.add(c.copy());
                } else {
                    result.add(defaultBeeOccupant());
                }
            }
        }
        for (int i = keep; i < target; i++) {
            result.add(defaultBeeOccupant());
        }
        return result;
    }
}
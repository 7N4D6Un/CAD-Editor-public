package com.github.rinorsi.cadeditor.fabric.loot;

import com.github.rinorsi.cadeditor.common.loot.LootTableIndex;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootTable;


public final class FabricLootTableTracker {
    private FabricLootTableTracker() {
    }

    public static void register() {
        LootTableEvents.ALL_LOADED.register((resourceManager, registry) -> {
            updateIndex(registry);
        });
    }

    
    public static void updateIndex(Registry<LootTable> registry) {
        try {
            List<Identifier> ids = new ArrayList<>();
            Set<Identifier> setKeySet = registry.keySet();
            setKeySet.forEach(ids::add);
            LootTableIndex.updateAll(ids);
        } catch (Throwable th) {
        }
    }
}

package com.github.rinorsi.cadeditor.client.logic;

import com.github.rinorsi.cadeditor.client.context.BlockEditorContext;
import com.github.rinorsi.cadeditor.client.context.EntityEditorContext;
import com.github.rinorsi.cadeditor.client.context.ItemEditorContext;
import com.github.rinorsi.cadeditor.client.debug.DebugLog;
import com.github.rinorsi.cadeditor.common.network.BlockEditorPacket;
import com.github.rinorsi.cadeditor.common.network.BlockInventoryItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.EntityEditorPacket;
import com.github.rinorsi.cadeditor.common.network.EntityInventoryItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.MainHandItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.NetworkManager;
import com.github.rinorsi.cadeditor.common.network.PlayerInventoryItemEditorPacket;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;


public class ClientEditorUpdateLogic {

    public static void updateMainHandItem(MainHandItemEditorPacket.Response response, ItemEditorContext context) {
        NetworkManager.sendToServer(NetworkManager.MAIN_HAND_ITEM_EDITOR_UPDATE, new MainHandItemEditorPacket.Update(response, context.getItemStack().copy()));
        DebugLog.infoKey("cadeditor.debug.update.mainhand", response.getEditorType());
    }

    public static void updatePlayerInventoryItem(PlayerInventoryItemEditorPacket.Response response, ItemEditorContext context) {
        NetworkManager.sendToServer(NetworkManager.PLAYER_INVENTORY_ITEM_EDITOR_UPDATE, new PlayerInventoryItemEditorPacket.Update(response, context.getItemStack().copy()));
        DebugLog.infoKey("cadeditor.debug.update.player_inventory", response.getSlot());
    }

    public static void updateBlockInventoryItem(BlockInventoryItemEditorPacket.Response response, ItemEditorContext context) {
        NetworkManager.sendToServer(NetworkManager.BLOCK_INVENTORY_ITEM_EDITOR_UPDATE, new BlockInventoryItemEditorPacket.Update(response, context.getItemStack().copy()));
        DebugLog.infoKey("cadeditor.debug.update.block_inventory", response.getSlot());
    }

    public static void updateEntityInventoryItem(EntityInventoryItemEditorPacket.Response response, ItemEditorContext context) {
        NetworkManager.sendToServer(NetworkManager.ENTITY_INVENTORY_ITEM_EDITOR_UPDATE, new EntityInventoryItemEditorPacket.Update(response, context.getItemStack().copy()));
        DebugLog.infoKey("cadeditor.debug.update.entity_inventory", response.getSlot());
    }

    public static void updateBlock(BlockEditorPacket.Response response, BlockEditorContext context) {
        NetworkManager.sendToServer(NetworkManager.BLOCK_EDITOR_UPDATE, new BlockEditorPacket.Update(response, context.getBlockState(), context.getTag()));
        DebugLog.infoKey("cadeditor.debug.update.block", response.getBlockPos());
        DebugLog.info((Supplier<String>) () -> {
            return "[BlockUpdate] submitted pos=" + String.valueOf(response.getBlockPos()) + " " + summarizeBlockTag(context.getTag());
        });
    }

    public static void updateEntity(EntityEditorPacket.Response response, EntityEditorContext context) {
        CompoundTag tag = context.getTag();
        Exception error = null;
        try {
            NetworkManager.sendToServer(NetworkManager.ENTITY_EDITOR_UPDATE, new EntityEditorPacket.Update(response, tag));
            DebugLog.infoKey("cadeditor.debug.update.entity", new Object[0]);
        } catch (Exception e) {
            error = e;
        }
        try {
            applyEntityUpdateOnClient(response, tag);
        } catch (Exception e) {
            if (error == null) {
                error = e;
            }
        }
        if (error != null) {
            throw new RuntimeException("实体更新失败: " + error.getMessage(), error);
        }
    }

    private static void applyEntityUpdateOnClient(EntityEditorPacket.Response response, CompoundTag tag) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity existingEntity = mc.level.getEntity(response.getEntityId());
        if (existingEntity == null) {
            return;
        }
        try {
            ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, mc.level.registryAccess(), tag);
            existingEntity.load(input);
        } catch (Exception ignored) {
        }
    }

    private static String summarizeBlockTag(CompoundTag tag) {
        if (tag == null) {
            return "tag=null";
        }
        String blockEntityId = tag.getStringOr("id", "");
        CompoundTag spawnData = readCompound(tag, "spawn_data", "SpawnData");
        CompoundTag entityData = readCompound(spawnData, "entity", "Entity");
        String entityId = entityData.getStringOr("id", "");
        return "tagKeys=" + tag.size() + " blockEntityId=" + (blockEntityId.isEmpty() ? "-" : blockEntityId) + " spawnEntityId=" + (entityId.isEmpty() ? "-" : entityId) + " hasSpawnData=" + (!spawnData.isEmpty());
    }

    private static CompoundTag readCompound(CompoundTag tag, String primary, String secondary) {
        if (tag == null) {
            return new CompoundTag();
        }
        if (tag.contains(primary)) {
            return tag.getCompound(primary).map(value -> value.copy()).orElseGet(CompoundTag::new);
        }
        if (tag.contains(secondary)) {
            return tag.getCompound(secondary).map(value -> value.copy()).orElseGet(CompoundTag::new);
        }
        return new CompoundTag();
    }
}

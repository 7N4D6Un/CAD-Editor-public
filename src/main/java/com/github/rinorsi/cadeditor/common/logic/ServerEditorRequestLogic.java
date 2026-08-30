package com.github.rinorsi.cadeditor.common.logic;

import com.github.rinorsi.cadeditor.common.CommonUtil;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.github.rinorsi.cadeditor.common.network.BlockEditorPacket;
import com.github.rinorsi.cadeditor.common.network.BlockInventoryItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.EntityEditorPacket;
import com.github.rinorsi.cadeditor.common.network.EntityInventoryItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.MainHandItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.NetworkManager;
import com.github.rinorsi.cadeditor.common.network.PlayerInventoryItemEditorPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;


public final class ServerEditorRequestLogic {
    public static void onMainHandItemEditorRequest(ServerPlayer player, MainHandItemEditorPacket.Request request) {
        NetworkManager.sendToClient(player, NetworkManager.MAIN_HAND_ITEM_EDITOR_RESPONSE, new MainHandItemEditorPacket.Response(request, PermissionLogic.hasPermission(player), player.getMainHandItem()));
    }

    public static void onPlayerInventoryItemEditorRequest(ServerPlayer player, PlayerInventoryItemEditorPacket.Request request) {
        NetworkManager.sendToClient(player, NetworkManager.PLAYER_INVENTORY_ITEM_EDITOR_RESPONSE, new PlayerInventoryItemEditorPacket.Response(request, PermissionLogic.hasPermission(player), player.getInventory().getItem(request.getSlot())));
    }

    public static void onBlockInventoryItemEditorRequest(ServerPlayer player, BlockInventoryItemEditorPacket.Request request) {
        if (player.level().getBlockEntity(request.getBlockPos()) instanceof Container container) {
            NetworkManager.sendToClient(player, NetworkManager.BLOCK_INVENTORY_ITEM_EDITOR_RESPONSE, new BlockInventoryItemEditorPacket.Response(request, PermissionLogic.hasPermission(player), container.getItem(request.getSlot())));
        } else {
            CommonUtil.showTargetError(player, ModTexts.ITEM);
        }
    }

    public static void onEntityInventoryItemEditorRequest(ServerPlayer player, EntityInventoryItemEditorPacket.Request request) {
        if (player.level().getEntity(request.getEntityId()) instanceof Container container) {
            NetworkManager.sendToClient(player, NetworkManager.ENTITY_INVENTORY_ITEM_EDITOR_RESPONSE, new EntityInventoryItemEditorPacket.Response(request, PermissionLogic.hasPermission(player), container.getItem(request.getSlot())));
        } else {
            CommonUtil.showTargetError(player, ModTexts.ITEM);
        }
    }

    public static void onBlockEditorRequest(ServerPlayer player, BlockEditorPacket.Request request) {
        ServerLevel level = player.level();
        BlockState blockState = level.getBlockState(request.getBlockPos());
        BlockEntity blockEntity = level.getBlockEntity(request.getBlockPos());
        CompoundTag tag = null;
        if (blockEntity != null) {
            TagValueOutput writer = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
            blockEntity.saveWithId(writer);
            tag = writer.buildResult();
        }
        NetworkManager.sendToClient(player, NetworkManager.BLOCK_EDITOR_RESPONSE, new BlockEditorPacket.Response(request, PermissionLogic.hasPermission(player), blockState, tag));
    }

    public static void onEntityEditorRequest(ServerPlayer player, EntityEditorPacket.Request request) {
        Entity entity = player.level().getEntity(request.getEntityId());
        CompoundTag tag = (CompoundTag) null;
        if (entity != null) {
            TagValueOutput valueOutputCreateWithContext = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
            if (!entity.save(valueOutputCreateWithContext)) {
                valueOutputCreateWithContext = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
                entity.saveWithoutId(valueOutputCreateWithContext);
                valueOutputCreateWithContext.putString("id", EntityType.getKey(entity.getType()).toString());
            }
            tag = valueOutputCreateWithContext.buildResult();
        }
        NetworkManager.sendToClient(player, NetworkManager.ENTITY_EDITOR_RESPONSE, new EntityEditorPacket.Response(request, PermissionLogic.hasPermission(player), tag));
    }
}
package com.github.rinorsi.cadeditor.client.logic;

import com.github.rinorsi.cadeditor.client.ClientContext;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.context.BlockEditorContext;
import com.github.rinorsi.cadeditor.client.context.EntityEditorContext;
import com.github.rinorsi.cadeditor.client.context.ItemEditorContext;
import com.github.rinorsi.cadeditor.client.debug.DebugLog;
import com.github.rinorsi.cadeditor.common.EditorType;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.github.rinorsi.cadeditor.common.network.BlockEditorPacket;
import com.github.rinorsi.cadeditor.common.network.BlockInventoryItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.EntityEditorPacket;
import com.github.rinorsi.cadeditor.common.network.EntityInventoryItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.MainHandItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.NetworkManager;
import com.github.rinorsi.cadeditor.common.network.PlayerInventoryItemEditorPacket;
import com.github.rinorsi.cadeditor.mixin.AbstractContainerScreenMixin;
import com.github.rinorsi.cadeditor.mixin.InventoryAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;


public final class ClientEditorRequestLogic {

    public static void requestWorldEditor(EditorType editorType) {
        DebugLog.infoKey("cadeditor.debug.request.world.start", editorType);
        if (!requestEntityEditor(editorType) && !requestBlockEditor(editorType) && !requestMainHandItemEditor(editorType)) {
            if (ClientContext.isModInstalledOnServer() && Minecraft.getInstance().player != null) {
                DebugLog.infoKey("cadeditor.debug.request.world.fallback", Minecraft.getInstance().player.getName().getString());
                requestSelfEditor(editorType);
            } else {
                DebugLog.infoKey("cadeditor.debug.request.world.no_target", editorType);
                ClientUtil.showMessage(ModTexts.Messages.errorNoTargetFound(ModTexts.ENTITY));
            }
        }
    }

    public static boolean requestEntityEditor(EditorType editorType) {
        DebugLog.infoKey("cadeditor.debug.request.entity.start", editorType);
        Entity entity = resolveEntityEditorTarget();
        if (entity != null) {
            if (ClientContext.isModInstalledOnServer()) {
                DebugLog.infoKey("cadeditor.debug.request.entity.server", entity.getId());
                NetworkManager.sendToServer(NetworkManager.ENTITY_EDITOR_REQUEST, new EntityEditorPacket.Request(editorType, entity.getId()));
                return true;
            }
            TagValueOutput writer = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, ClientUtil.registryAccess());
            if (!entity.save(writer)) {
                entity.saveWithoutId(writer);
                writer.putString("id", EntityType.getKey(entity.getType()).toString());
            }
            CompoundTag tag = writer.buildResult();
            DebugLog.infoKey("cadeditor.debug.request.entity.local", entity.getName().getString());
            ModScreenHandler.openEditor(editorType, new EntityEditorContext(tag, ModTexts.errorServerModRequiredEntity(), true, null));
            return true;
        }
        DebugLog.infoKey("cadeditor.debug.request.entity.missing", new Object[0]);
        return false;
    }

    private static Entity resolveEntityEditorTarget() {
        if (!(Minecraft.getInstance().hitResult instanceof EntityHitResult entityHitResult)) {
            return null;
        }
        return entityHitResult.getEntity();
    }

    public static boolean requestBlockEditor(EditorType editorType) {
        DebugLog.infoKey("cadeditor.debug.request.block.start", editorType);
        if (Minecraft.getInstance().hitResult instanceof BlockHitResult blockHitResult) {
            if (blockHitResult.getType() != HitResult.Type.MISS) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (ClientContext.isModInstalledOnServer()) {
                    DebugLog.infoKey("cadeditor.debug.request.block.server", blockPos);
                    NetworkManager.sendToServer(NetworkManager.BLOCK_EDITOR_REQUEST, new BlockEditorPacket.Request(editorType, blockPos));
                    return true;
                }
                ClientLevel level = Minecraft.getInstance().level;
                BlockState blockState = level.getBlockState(blockPos);
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                CompoundTag tag = null;
                if (blockEntity != null) {
                    tag = blockEntity.saveWithFullMetadata(ClientUtil.registryAccess());
                }
                DebugLog.infoKey("cadeditor.debug.request.block.local", blockState.getBlock().getName().getString());
                ModScreenHandler.openEditor(editorType, new BlockEditorContext(blockState, tag, ModTexts.errorServerModRequiredBlock(), null));
                return true;
            }
        }
        DebugLog.infoKey("cadeditor.debug.request.block.missing", new Object[0]);
        return false;
    }

    public static boolean requestMainHandItemEditor(EditorType editorType) {
        DebugLog.infoKey("cadeditor.debug.request.mainhand.start", editorType);
        ItemStack item = Minecraft.getInstance().player.getMainHandItem();
        if (item.isEmpty()) {
            DebugLog.infoKey("cadeditor.debug.request.mainhand.empty", new Object[0]);
            return false;
        }
        if (ClientContext.isModInstalledOnServer()) {
            DebugLog.infoKey("cadeditor.debug.request.mainhand.server", item.getDisplayName().getString());
            NetworkManager.sendToServer(NetworkManager.MAIN_HAND_ITEM_EDITOR_REQUEST, new MainHandItemEditorPacket.Request(editorType));
            return true;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        int selectedSlot = player.getInventory().getSelectedSlot();
        boolean isCreative = player.isCreative();
        DebugLog.infoKey("cadeditor.debug.request.mainhand.local", item.getDisplayName().getString());
        ModScreenHandler.openEditor(editorType, new ItemEditorContext(item, isCreative ? null : ModTexts.errorServerModCreativeHint(ModTexts.ITEM), true, context -> {
            ItemStack newStack = context.getItemStack().copy();
            if (isCreative) {
                player.connection.send(new ServerboundSetCreativeModeSlotPacket(selectedSlot + 36, newStack));
                InventoryAccessor inventory = (InventoryAccessor) (Object) player.getInventory();
                inventory.cadeditor$getItems().set(selectedSlot, newStack);
                ((Inventory) (Object) inventory).setChanged();
                player.containerMenu.broadcastChanges();
            }
        }));
        return true;
    }

    public static void requestSelfEditor(EditorType editorType) {
        LocalPlayer entity = Minecraft.getInstance().player;
        if (ClientContext.isModInstalledOnServer()) {
            DebugLog.infoKey("cadeditor.debug.request.self.server", entity.getName().getString());
            NetworkManager.sendToServer(NetworkManager.ENTITY_EDITOR_REQUEST, new EntityEditorPacket.Request(editorType, entity.getId()));
        } else {
            DebugLog.infoKey("cadeditor.debug.request.self.missing", new Object[0]);
        }
    }

    public static boolean requestInventoryItemEditor(EditorType editorType, AbstractContainerScreen<?> screen) {
        Slot slot = ((AbstractContainerScreenMixin) screen).getHoveredSlot();
        if (slot != null && slot.hasItem()) {
            int slotIndex = slot.getContainerSlot();
            boolean creativeInventoryScreen = false;
            if (slot.container instanceof Inventory) {
                if (screen instanceof CreativeModeInventoryScreen) {
                    CreativeModeInventoryScreen creative = (CreativeModeInventoryScreen) screen;
                    creativeInventoryScreen = true;
                    if (creative.isInventoryOpen()) {
                        slotIndex = ClientUtil.convertCreativeInventorySlot(slotIndex);
                    }
                }
                int finalSlot = slotIndex;
                boolean finalCreative = creativeInventoryScreen;
                DebugLog.infoKey("cadeditor.debug.request.inventory.start", finalSlot, editorType);
                if (ClientContext.isModInstalledOnServer()) {
                    DebugLog.infoKey("cadeditor.debug.request.inventory.server", finalSlot, finalCreative);
                    NetworkManager.sendToServer(NetworkManager.PLAYER_INVENTORY_ITEM_EDITOR_REQUEST, new PlayerInventoryItemEditorPacket.Request(editorType, finalSlot, finalCreative));
                    return true;
                }
                DebugLog.infoKey("cadeditor.debug.request.inventory.local", finalSlot);
                LocalPlayer player = Minecraft.getInstance().player;
                boolean isCreative = player.isCreative();
                ModScreenHandler.openEditor(editorType, new ItemEditorContext(slot.getItem(), isCreative ? null : ModTexts.errorServerModCreativeHint(ModTexts.ITEM), true, context -> {
                    ItemStack newStack = context.getItemStack().copy();
                    if (isCreative && finalSlot >= 36) {
                        player.connection.send(new ServerboundSetCreativeModeSlotPacket(finalSlot, newStack));
                        InventoryAccessor inventory = (InventoryAccessor) (Object) player.getInventory();
                        inventory.cadeditor$getItems().set(finalSlot - 36, newStack);
                        ((Inventory) (Object) inventory).setChanged();
                        player.containerMenu.broadcastChanges();
                    }
                }));
                return true;
            }
            if (Minecraft.getInstance().hitResult instanceof BlockHitResult blockHitResult) {
                if (Minecraft.getInstance().level.getBlockEntity(blockHitResult.getBlockPos()) instanceof Container) {
                    if (ClientContext.isModInstalledOnServer()) {
                        DebugLog.infoKey("cadeditor.debug.request.inventory.block", blockHitResult.getBlockPos(), slotIndex);
                        NetworkManager.sendToServer(NetworkManager.BLOCK_INVENTORY_ITEM_EDITOR_REQUEST, new BlockInventoryItemEditorPacket.Request(editorType, slotIndex, blockHitResult.getBlockPos()));
                        return true;
                    }
                    DebugLog.infoKey("cadeditor.debug.request.inventory.block_local", blockHitResult.getBlockPos(), slotIndex);
                    ModScreenHandler.openEditor(editorType, new ItemEditorContext(slot.getItem(), ModTexts.errorServerModCreativeHint(ModTexts.ITEM), true, null));
                    return true;
                }
            }
            EntityHitResult entityHitResult = Minecraft.getInstance().hitResult instanceof EntityHitResult ehr ? ehr : null;
            if (entityHitResult != null) {
                if (entityHitResult.getEntity() instanceof Container) {
                    if (ClientContext.isModInstalledOnServer()) {
                        DebugLog.infoKey("cadeditor.debug.request.inventory.entity", entityHitResult.getEntity().getId(), slotIndex);
                        NetworkManager.sendToServer(NetworkManager.ENTITY_INVENTORY_ITEM_EDITOR_REQUEST, new EntityInventoryItemEditorPacket.Request(editorType, slotIndex, entityHitResult.getEntity().getId()));
                        return true;
                    }
                    DebugLog.infoKey("cadeditor.debug.request.inventory.entity_local", entityHitResult.getEntity().getId(), slotIndex);
                    ModScreenHandler.openEditor(editorType, new ItemEditorContext(slot.getItem(), ModTexts.errorServerModCreativeHint(ModTexts.ITEM), true, null));
                    return true;
                }
            }
        }
        DebugLog.infoKey("cadeditor.debug.request.inventory.missing", new Object[0]);
        return false;
    }
}

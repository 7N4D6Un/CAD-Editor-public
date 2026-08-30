package com.github.rinorsi.cadeditor.client.logic;

import com.github.rinorsi.cadeditor.client.ClientContext;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.Vault;
import com.github.rinorsi.cadeditor.client.debug.DebugLog;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.VaultItemListSelectionElementModel;
import com.github.rinorsi.cadeditor.common.CommonUtil;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.github.rinorsi.cadeditor.common.network.GiveVaultItemPacket;
import com.github.rinorsi.cadeditor.common.network.NetworkManager;
import com.github.rinorsi.cadeditor.mixin.AbstractContainerScreenMixin;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;


public final class ClientVaultActionLogic {
    public static void giveVaultItem(ItemStack itemStack) {
        DebugLog.infoKey("cadeditor.debug.vault.give", itemStack.getDisplayName().getString());
        NetworkManager.sendToServer(NetworkManager.GIVE_VAULT_ITEM, new GiveVaultItemPacket(itemStack));
    }

    public static void giveToSelectedHotbar(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer localPlayer = mc.player;
        if (localPlayer == null) {
            return;
        }
        ItemStack copy = stack.copy();
        if (ClientContext.isModInstalledOnServer()) {
            DebugLog.infoKey("cadeditor.debug.vault.send", new Object[0]);
            giveVaultItem(copy);
            return;
        }
        if (!localPlayer.isCreative()) {
            ClientUtil.showMessage(ModTexts.Messages.errorServerModRequiredVault());
            DebugLog.infoKey("cadeditor.debug.vault.server_required", new Object[0]);
            return;
        }
        applyToCreativeInventory(localPlayer, copy);
    }

    private static void applyToCreativeInventory(LocalPlayer player, ItemStack stack) {
        Inventory inventory = player.getInventory();
        ItemStack remaining = stack.copy();
        ItemStack[] before = new ItemStack[Inventory.INVENTORY_SIZE];
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            before[i] = inventory.getItem(i).copy();
        }
        inventory.add(remaining);
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            ItemStack current = inventory.getItem(i);
            if (!ItemStack.matches(before[i], current)) {
                player.connection.send(new ServerboundSetCreativeModeSlotPacket(i + 36, current.copy()));
            }
        }
        inventory.setChanged();
        player.containerMenu.broadcastChanges();
        if (remaining.isEmpty()) {
            CommonUtil.showVaultItemGiveSuccess(player);
        } else if (remaining.getCount() < stack.getCount()) {
            ClientUtil.showMessage(ModTexts.Messages.vaultItemGivePartial(stack.getCount() - remaining.getCount()));
        } else {
            ClientUtil.showMessage(ModTexts.Messages.VAULT_ITEM_GIVE_FULL);
        }
        DebugLog.infoKey("cadeditor.debug.vault.apply_creative", new Object[0]);
    }

    public static boolean openVaultSelection(AbstractContainerScreen<?> screen) {
        DebugLog.infoKey("cadeditor.debug.vault.open", new Object[0]);
        Slot hoveredSlot = ((AbstractContainerScreenMixin) screen).getHoveredSlot();
        if (hoveredSlot == null || !(hoveredSlot.container instanceof Inventory)) {
            DebugLog.infoKey("cadeditor.debug.vault.invalid_slot", new Object[0]);
            return false;
        }
        List<VaultItemListSelectionElementModel> elements = new ArrayList<>();
        Map<String, ItemStack> stacksById = new LinkedHashMap<>();
        List<CompoundTag> storedItems = Vault.getInstance().getItems();
        for (int i = 0; i < storedItems.size(); i++) {
            ItemStack stack = ClientUtil.parseItemStack(storedItems.get(i));
            if (!stack.isEmpty()) {
                Identifier id = Identifier.fromNamespaceAndPath("cadeditor", "vault_item_" + i);
                elements.add(new VaultItemListSelectionElementModel(id, stack));
                stacksById.put(id.toString(), stack.copy());
            }
        }
        if (elements.isEmpty()) {
            DebugLog.infoKey("cadeditor.debug.vault.empty", new Object[0]);
            return false;
        }
        ModScreenHandler.openListSelectionScreen(ModTexts.VAULT, "vault_item", elements, selectedId -> {
            ItemStack chosen = (ItemStack) stacksById.get(selectedId);
            if (chosen == null) {
                return;
            }
            DebugLog.infoKey("cadeditor.debug.vault.choice", chosen.getHoverName().getString());
            giveToSelectedHotbar(chosen);
        });
        return true;
    }
}

package com.github.rinorsi.cadeditor.common.logic;

import com.github.rinorsi.cadeditor.common.CommonUtil;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.github.rinorsi.cadeditor.common.network.GiveVaultItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;


public class ServerVaultActionLogic {
    public static void onGiveVaultItem(ServerPlayer player, GiveVaultItemPacket response) {
        if (!PermissionLogic.hasPermission(player)) {
            CommonUtil.showPermissionError(player, ModTexts.ITEM);
            return;
        }
        ItemStack requested = response.itemStack();
        ItemStack copy = requested.copy();
        player.getInventory().add(copy);
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
        if (copy.isEmpty()) {
            CommonUtil.showVaultItemGiveSuccess(player);
        } else if (copy.getCount() < requested.getCount()) {
            CommonUtil.showVaultItemGivePartial(player, requested.getCount() - copy.getCount());
        } else {
            CommonUtil.showVaultItemGiveFull(player);
        }
    }
}

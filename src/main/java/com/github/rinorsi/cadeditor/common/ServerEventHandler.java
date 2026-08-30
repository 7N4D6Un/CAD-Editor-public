package com.github.rinorsi.cadeditor.common;

import com.github.rinorsi.cadeditor.common.network.ModNotificationPacket;
import com.github.rinorsi.cadeditor.common.network.NetworkManager;
import net.minecraft.server.level.ServerPlayer;

public final class ServerEventHandler {
    public static void onPlayerJoin(ServerPlayer player) {
        NetworkManager.sendToClient(player, NetworkManager.SERVER_NOTIFICATION,
                new ModNotificationPacket.Server(CommonConfiguration.INSTANCE.getPermissionLevel(), CommonConfiguration.INSTANCE.isCreativeOnly()));
    }

    public static void onPlayerLeave(ServerPlayer player) {
        ServerContext.removeModdedClient(player);
    }
}

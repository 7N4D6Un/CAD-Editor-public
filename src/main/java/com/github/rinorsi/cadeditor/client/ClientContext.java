package com.github.rinorsi.cadeditor.client;

import com.github.rinorsi.cadeditor.common.network.ModNotificationPacket;
import com.github.rinorsi.cadeditor.common.network.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ClientContext {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean modInstalledOnServer;
    private static int serverPermissionLevel;
    private static boolean serverCreativeOnly;

    public static boolean isModInstalledOnServer() {
        return modInstalledOnServer;
    }

    public static void setModInstalledOnServer(boolean value) {
        LOGGER.debug("Setting 'modInstalledOnServer' to {}", value);
        modInstalledOnServer = value;
    }

    public static void onServerNotification(ModNotificationPacket.Server packet) {
        setModInstalledOnServer(true);
        serverPermissionLevel = packet.permissionLevel();
        serverCreativeOnly = packet.creativeOnly();
        NetworkManager.sendToServer(NetworkManager.CLIENT_NOTIFICATION, ModNotificationPacket.Client.INSTANCE);
    }

    public static boolean vaultGiveEnabled() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (!modInstalledOnServer) {
            return player != null && player.isCreative();
        }
        if (player == null) {
            return false;
        }
        return hasCommandPermission(player, serverPermissionLevel) && (!serverCreativeOnly || player.isCreative());
    }

    private static boolean hasCommandPermission(LocalPlayer player, int level) {
        int clamped = Math.max(0, Math.min(4, level));
        Permission permission = new Permission.HasCommandLevel(PermissionLevel.byId(clamped));
        return player.permissions().hasPermission(permission);
    }
}

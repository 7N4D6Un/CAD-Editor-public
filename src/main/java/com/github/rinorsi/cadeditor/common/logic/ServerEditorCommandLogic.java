package com.github.rinorsi.cadeditor.common.logic;

import com.github.rinorsi.cadeditor.common.CommonConfiguration;
import com.github.rinorsi.cadeditor.common.EditorType;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.github.rinorsi.cadeditor.common.ServerContext;
import com.github.rinorsi.cadeditor.common.network.EditorCommandPacket;
import com.github.rinorsi.cadeditor.common.network.NetworkManager;
import net.minecraft.server.level.ServerPlayer;

public class ServerEditorCommandLogic {

    public static int commandOpenEditor(ServerPlayer player, EditorCommandPacket.Target target, EditorType type) {
        if (ServerContext.isClientModded(player)) {
            if (CommonConfiguration.INSTANCE.isCreativeOnly() && !player.isCreative()) {
                player.sendSystemMessage(ModTexts.commandCreativeOnly());
                return 2;
            }
            NetworkManager.sendToClient(player, NetworkManager.EDITOR_COMMAND, new EditorCommandPacket(target, type));
            return 0;
        }
        player.sendSystemMessage(ModTexts.commandMustInstall());
        player.sendSystemMessage(ModTexts.commandDownload());
        return 1;
    }
}
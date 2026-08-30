package com.github.rinorsi.cadeditor;

import com.github.rinorsi.cadeditor.common.network.NetworkHandler;
import com.github.rinorsi.cadeditor.fabric.PlatformUtilImpl;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;

public final class PlatformUtil {
    private static final PlatformUtilImpl IMPL = new PlatformUtilImpl();

    private PlatformUtil() {
    }

    public static Path getConfigDir() {
        return IMPL.getConfigDir();
    }

    public static <P> void sendToServer(NetworkHandler.Server<P> handler, P packet) {
        IMPL.sendToServer(handler, packet);
    }

    public static <P> void sendToClient(ServerPlayer player, NetworkHandler.Client<P> handler, P packet) {
        IMPL.sendToClient(player, handler, packet);
    }

    public static <P> void registerServerHandler(NetworkHandler.Server<P> handler) {
        IMPL.registerServerHandler(handler);
    }

    public static <P> void registerClientHandler(NetworkHandler.Client<P> handler) {
        IMPL.registerClientHandler(handler);
    }
}

package com.github.rinorsi.cadeditor.fabric;

import com.github.rinorsi.cadeditor.common.network.NetworkHandler;
import com.github.rinorsi.cadeditor.common.network.PacketSerializer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;


public final class PlatformUtilImpl {
    private static final Map<NetworkHandler<?>, CustomPacketPayload.Type<?>> TYPES = new ConcurrentHashMap<>();
    private static final List<NetworkHandler.Client<?>> CLIENT_HANDLERS = new ArrayList<>();

    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public <P> void sendToServer(NetworkHandler.Server<P> handler, P packet) {
        ClientPlayNetworking.send(wrap(handler, packet));
    }

    public <P> void sendToClient(ServerPlayer player, NetworkHandler.Client<P> handler, P packet) {
        ServerPlayNetworking.send(player, wrap(handler, packet));
    }

    public <P> void registerServerHandler(NetworkHandler.Server<P> handler) {
        CustomPacketPayload.Type<WrappedPayload<P>> type = type(handler);
        PayloadTypeRegistry.serverboundPlay().register(type, codec(handler));
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            context.server().execute(() -> {
                handler.getPacketHandler().handle(context.player(), payload.packet());
            });
        });
    }

    public <P> void registerClientHandler(NetworkHandler.Client<P> handler) {
        CustomPacketPayload.Type<WrappedPayload<P>> type = type(handler);
        PayloadTypeRegistry.clientboundPlay().register(type, codec(handler));
        CLIENT_HANDLERS.add(handler);
    }

    public static void registerClientReceivers() {
        for (NetworkHandler.Client<?> handler : CLIENT_HANDLERS) {
            registerClientReceiver(handler);
        }
    }

    private static <P> void registerClientReceiver(NetworkHandler.Client<P> handler) {
        CustomPacketPayload.Type<WrappedPayload<P>> type = type(handler);
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            context.client().execute(() -> {
                handler.getPacketHandler().handle(payload.packet());
            });
        });
    }

    private static <P> StreamCodec<RegistryFriendlyByteBuf, WrappedPayload<P>> codec(NetworkHandler<P> handler) {
        PacketSerializer<P> serializer = handler.getSerializer();
        return StreamCodec.of((buf, payload) -> {
            serializer.write(payload.packet(), buf);
        }, buf -> wrap(handler, serializer.read(buf)));
    }

    
    @SuppressWarnings("unchecked")
    private static <P> CustomPacketPayload.Type<WrappedPayload<P>> type(NetworkHandler<P> handler) {
        return (CustomPacketPayload.Type<WrappedPayload<P>>) (CustomPacketPayload.Type<?>) TYPES.computeIfAbsent(handler, PlatformUtilImpl::createType);
    }

    private static CustomPacketPayload.Type<?> createType(NetworkHandler<?> handler) {
        Identifier location = handler.getLocation();
        return new CustomPacketPayload.Type<>(location);
    }

    
    public static <P> WrappedPayload<P> wrap(NetworkHandler<P> handler, P packet) {
        return new WrappedPayload<>(packet, handler);
    }

    
    private static record WrappedPayload<P>(P packet, NetworkHandler<P> handler) implements CustomPacketPayload {

        public void write(RegistryFriendlyByteBuf buf) {
            this.handler.getSerializer().write(this.packet, buf);
        }

        public CustomPacketPayload.Type<WrappedPayload<P>> type() {
            return PlatformUtilImpl.type(this.handler);
        }
    }
}
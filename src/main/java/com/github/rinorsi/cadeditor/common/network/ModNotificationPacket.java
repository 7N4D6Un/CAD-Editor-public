package com.github.rinorsi.cadeditor.common.network;

import net.minecraft.network.FriendlyByteBuf;

public final class ModNotificationPacket {
    public record Client() {
        public static final Client INSTANCE = new Client();
        public static final PacketSerializer<Client> SERIALIZER = PacketSerializer.empty(INSTANCE);
    }

    public record Server(int permissionLevel, boolean creativeOnly) {
        public static final PacketSerializer<Server> SERIALIZER = new PacketSerializer<>() {
            @Override
            public void write(Server obj, FriendlyByteBuf buf) {
                buf.writeInt(obj.permissionLevel());
                buf.writeBoolean(obj.creativeOnly());
            }

            @Override
            public Server read(FriendlyByteBuf buf) {
                return new Server(buf.readInt(), buf.readBoolean());
            }
        };
    }
}

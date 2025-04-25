package org.copycraftDev.new_horizons.core.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TeleportPayload(Identifier dimensionId, double x, double y, double z)
        implements CustomPayload {

    public static final Id<TeleportPayload> ID =
            new Id<>(Identifier.of("new_horizons", "teleport"));

    // <— HERE: use RegistryByteBuf, not PacketByteBuf
    public static final PacketCodec<RegistryByteBuf, TeleportPayload> CODEC =
            PacketCodec.of(
                    // encoder: write() takes PacketByteBuf, but RegistryByteBuf is a subclass
                    (buf, payload) -> {
                        // you can also call payload.write(buf) if you prefer
                    },
                    // decoder must accept RegistryByteBuf
                    buf -> new TeleportPayload(
                            buf.readIdentifier(),
                            buf.readDouble(),
                            buf.readDouble(),
                            buf.readDouble()
                    )
            );

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(dimensionId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

package org.copycraftDev.new_horizons.core.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class TeleportNetworking {
    public static void registerReceivers() {
        // <-- use the full codec here
        PayloadTypeRegistry
                .playC2S()
                .register(TeleportPayload.ID, TeleportPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
                TeleportPayload.ID,
                (payload, ctx) -> {
                    var player = ctx.player();
                    TeleportUtils.teleport(
                            player,
                            payload.dimensionId().toString(),
                            payload.x(),
                            payload.y(),
                            payload.z()
                    );
                }
        );
    }
}

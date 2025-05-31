package org.copycraftDev.new_horizons.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.copycraftDev.new_horizons.client.DimensionSwapUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * When the client receives a respawn (dimension change) packet,
 * set the flag so the next reload + screen get skipped.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class InstantDimensionNetworkMixin {

    @Inject(
            method = "onPlayerRespawn(Lnet/minecraft/network/packet/s2c/play/PlayerRespawnS2CPacket;)V",
            at = @At("HEAD")
    )
    private void beforePlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        DimensionSwapUtil.skipNextReload = true;
    }
}

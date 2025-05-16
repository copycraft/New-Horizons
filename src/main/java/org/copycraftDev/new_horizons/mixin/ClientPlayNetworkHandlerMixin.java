package org.copycraftDev.new_horizons.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(
            method = "onPlayerRespawn(Lnet/minecraft/network/packet/s2c/play/PlayerRespawnS2CPacket;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRespawnHead(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen current = client.currentScreen;

        // If it’s already the death screen, let it do its thing:
        if (current instanceof DeathScreen) {
            return;
        }

        // Otherwise, cancel the handler so it never opens the loading‐terrain screen,
        // and clear whatever is on top
        ci.cancel();
        client.execute(() -> client.setScreen(null));
    }
}


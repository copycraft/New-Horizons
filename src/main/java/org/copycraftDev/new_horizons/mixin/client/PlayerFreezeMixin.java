package org.copycraftDev.new_horizons.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.copycraftDev.new_horizons.Lidar.FreezeControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class PlayerFreezeMixin {


    @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
    private void freezeInput(CallbackInfo ci) {
        if (FreezeControl.isFrozen) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void freezePlayerMovement(CallbackInfo ci) {
        if (FreezeControl.isFrozen && MinecraftClient.getInstance().player != null) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            player.setVelocity(0, 0, 0);
            player.setPitch(player.getPitch());
            player.setYaw(player.getYaw());
            player.forwardSpeed = 0;
            player.sidewaysSpeed = 0;
            player.upwardSpeed = 0;
            player.headYaw = 0;
        }
    }
}

package org.copycraftDev.new_horizons.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {

    // Shadow the fields for pitch and yaw from the Camera class.
    @Shadow private float pitch;

    @Shadow private float yaw;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            // Check if the player is riding any entity.
            Entity vehicle = client.player.getVehicle();
            if (vehicle != null) {
                // Force pitch and yaw to 0.
                this.pitch = 0.0F;
                this.yaw = 0.0F;
                // Cancel the rest of the camera update.
                ci.cancel();
            }
        }
    }
}

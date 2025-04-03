package org.copycraftDev.new_horizons.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraRotationMixin {

    @Inject(method = "getRotation", at = @At("HEAD"), cancellable = true)
    private void onGetRotation(CallbackInfoReturnable<Quaternionf> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.hasVehicle()) {
            // Create an identity quaternion representing 0° rotation (pitch=0, yaw=0, roll=0)
            Quaternionf identity = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
            cir.setReturnValue(identity);
        }
    }
}

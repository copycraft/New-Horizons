package org.copycraftDev.new_horizons.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class NoCloudsMixin {
    /**
     * Cancels cloud rendering by injecting at the start of the renderClouds method.
     */
    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void onRenderClouds(MatrixStack matrices, Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        // Prevent the clouds from being drawn
        ci.cancel();
    }
}


package org.copycraftDev.new_horizons.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to scale the vanilla panorama cube by 5×.
 */
@Mixin(BackgroundRenderer.class)
public class PanoramaScaleMixin {

    /**
     * Inject code to scale the panorama cube by 5x.
     */
    @Inject(
            method = "render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V",
            at = @At("HEAD")
    )
    private static void scalePanoramaHead(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        // Using MatrixStack to scale the rendering of the panorama cube.
        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushMatrix();
        matrixStack.scale(500.0F, 500.0F, 500.0F);  // Scale 5x
    }

    /**
     * Reset matrix after the panorama is rendered.
     */
    @Inject(
            method = "render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V",
            at = @At("TAIL")
    )
    private static void scalePanoramaTail(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        // Restore matrix stack after rendering panorama.
        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.popMatrix();
    }
}

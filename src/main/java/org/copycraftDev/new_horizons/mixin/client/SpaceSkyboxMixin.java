package org.copycraftDev.new_horizons.mixin.client;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class SpaceSkyboxMixin {
    @Unique
    private static final Identifier SKYBOX_TEXTURE = Identifier.of( "new_horizons:textures/skyboxes/space_skybox.png");

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    public void renderCustomSky(Matrix4f matrix4f, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        if (MinecraftClient.getInstance().world.getRegistryKey().getValue().getPath().equals("space")) {
            VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            MinecraftClient.getInstance().getTextureManager().bindTexture(SKYBOX_TEXTURE);

            // Render your custom skybox here using quads or shaders

            ci.cancel(); // Prevent default sky rendering
        }
    }
}

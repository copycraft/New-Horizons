package org.copycraftDev.new_horizons.mixin.client;

import net.minecraft.client.render.BackgroundRenderer;
import org.copycraftDev.new_horizons.client.FogSettings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    private void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
        if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
            float start = FogSettings.getFogStart();
            float end = FogSettings.getFogEnd();

            RenderSystem.setShaderFogStart(start);
            RenderSystem.setShaderFogEnd(end);
            // If a linear fog shape is desired and a constant exists, use it.
            // Otherwise, remove or replace this line if no valid constant exists:
            // RenderSystem.setShaderFogShape(FogShape.LINEAR);

            ci.cancel(); // Cancel the default fog setup so custom settings take effect.
        }
    }
}

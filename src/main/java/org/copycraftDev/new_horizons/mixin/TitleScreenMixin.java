package org.copycraftDev.new_horizons.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.DrawContext;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRenderer;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRendererPanorama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to hook our celestial‐body renderers into the Minecraft title screen.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    /** Screen width. */
    private int width;
    /** Screen height. */
    private int height;
    /** Alpha of the vanilla panorama (0→1). */
    @Shadow private float backgroundAlpha;

    /** Ensure we only register the world‐render listener once. */
    private boolean initDone = false;

    /** Rotation parameters for celestial bodies */
    private float rotationX = 0f;
    private float rotationY = 90f;  // Start with a 90-degree rotation on the Y axis
    private float rotationZ = 0f;
    private float scale = 1.0f; // Default scale
    private float offsetX = 0f; // Default offsetX
    private float offsetY = 0f; // Default offsetY

    /**
     * After TitleScreen.init(), register the in‐world renderer.
     */
    @Inject(method = "init()V", at = @At("TAIL"))
    private void onTitleInit(CallbackInfo ci) {
        if (!initDone) {
            CelestialBodyRenderer.register();
            initDone = true;
        }
    }

    /**
     * After TitleScreen.render(...), draw our custom panorama overlay.
     * Also set the rotations, scale, and offsets for the celestial bodies here.
     */
    @Inject(
            method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
            at = @At("TAIL")
    )
    private void onTitleRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Update screen dimensions (these might not always be set during init)
        this.width = context.getScaledWindowWidth();
        this.height = context.getScaledWindowHeight();

        // Set rotation, scale, and offsets for the celestial bodies
        CelestialBodyRendererPanorama.setRotationX(rotationX);
        CelestialBodyRendererPanorama.setRotationY(rotationY);
        CelestialBodyRendererPanorama.setRotationZ(rotationZ);
        CelestialBodyRendererPanorama.setScale(scale);
        CelestialBodyRendererPanorama.setOffsetX(offsetX);
        CelestialBodyRendererPanorama.setOffsetY(offsetY);

        // Render celestial panorama overlay
        CelestialBodyRendererPanorama.render(context, this.width, this.height, this.backgroundAlpha, delta);
    }

    /**
     * Optional method to adjust rotations dynamically (you could call this from other parts of your mod).
     */
    public void setRotationX(float rotationX) {
        this.rotationX = rotationX;
    }

    public void setRotationY(float rotationY) {
        this.rotationY = rotationY;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public float getScale() {
        return scale;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }
}

package org.copycraftDev.new_horizons.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.DrawContext;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRenderer;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRendererPanorama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to hook our celestial‐body renderers into the Minecraft title screen,
 * using the renderer’s own centering math.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Shadow private float backgroundAlpha;
    private boolean initDone = false;

    @Unique private float rotationX = 0f;
    @Unique private float rotationY = 90f;
    @Unique private float rotationZ = 45f;
    @Unique private float scale     = 0.115f;
    // Manual offset tweaks (renderer will add these to width/2, height/2)
    @Unique private float offsetX   = 0f;
    @Unique private float offsetY   = 0f;
    @Unique private float cameraX   = 0f;
    @Unique private float cameraY   = 0f;
    @Unique private float cameraZ   = 0f;
    @Unique private float planetZ   = -300f;

    @Inject(method = "init()V", at = @At("TAIL"))
    private void onTitleInit(CallbackInfo ci) {
        if (!initDone) {
            CelestialBodyRenderer.register();
            initDone = true;
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
            at = @At("TAIL")
    )
    private void onTitleRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int screenWidth  = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        // Push all params into the panorama renderer
        CelestialBodyRendererPanorama.setRotationX(rotationX);
        CelestialBodyRendererPanorama.setRotationY(rotationY);
        CelestialBodyRendererPanorama.setRotationZ(rotationZ);
        CelestialBodyRendererPanorama.setScale(scale);

        // These offsets are now purely additive to (width/2, height/2)
        CelestialBodyRendererPanorama.setOffsetX(offsetX);
        CelestialBodyRendererPanorama.setOffsetY(offsetY);

        CelestialBodyRendererPanorama.setCameraOffset(cameraX, cameraY, cameraZ);
        CelestialBodyRendererPanorama.setPlanetZ(planetZ);

        // Let the renderer handle centering internally
        CelestialBodyRendererPanorama.render(
                context,
                screenWidth,
                screenHeight,
                this.backgroundAlpha,
                delta
        );
    }

    // Accessors for runtime tweaking…
    public void setRotationX(float rotationX) { this.rotationX = rotationX; }
    public void setRotationY(float rotationY) { this.rotationY = rotationY; }
    public void setRotationZ(float rotationZ) { this.rotationZ = rotationZ; }
    public void setScale(float scale)         { this.scale     = scale;     }
    public void setOffsetX(float offsetX)     { this.offsetX   = offsetX;   }
    public void setOffsetY(float offsetY)     { this.offsetY   = offsetY;   }
    public void setCameraX(float cameraX)     { this.cameraX   = cameraX;   }
    public void setCameraY(float cameraY)     { this.cameraY   = cameraY;   }
    public void setCameraZ(float cameraZ)     { this.cameraZ   = cameraZ;   }
    public void setPlanetZ(float planetZ)     { this.planetZ   = planetZ;   }

    public float getRotationX() { return rotationX; }
    public float getRotationY() { return rotationY; }
    public float getRotationZ() { return rotationZ; }
    public float getScale()     { return scale;     }
    public float getOffsetX()   { return offsetX;   }
    public float getOffsetY()   { return offsetY;   }
    public float getCameraX()   { return cameraX;   }
    public float getCameraY()   { return cameraY;   }
    public float getCameraZ()   { return cameraZ;   }
    public float getPlanetZ()   { return planetZ;   }
}
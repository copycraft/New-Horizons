package org.copycraftDev.new_horizons.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRendererPanorama;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Unique private boolean solarView = false;
    @Unique private boolean draggingSlider = false;
    @Unique private boolean draggingSlider2 = false;
    @Unique private boolean isDraggingPan = false;
    @Unique private boolean isDraggingOrbit = false;

    @Unique private float targetZoom = 0.115f;
    @Unique private float targetspeed = 1f;
    @Unique private float smoothZoom = 0.115f;
    @Unique private float smoothspeed = 1f;

    @Unique private float panX = 0f, panY = 0f;
    @Unique private float lastMouseX = 0f, lastMouseY = 0f;

    @Unique private float rotationX = 0f;
    @Unique private float rotationY = 90f;
    @Unique private float rotationZ = 45f;
    @Unique private float scale = 0.113f;

    @Unique private float cameraX = 0f, cameraY = 0f, cameraZ = 0f;
    @Unique private float planetZ = 0f;

    @Shadow private float backgroundAlpha;
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("HEAD"))
    private void renderSolar(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        smoothZoom += (targetZoom - smoothZoom) * 0.15f;
        smoothspeed += (targetspeed - smoothspeed) * 0.15f;

        int screenWidth  = ctx.getScaledWindowWidth();
        int screenHeight = ctx.getScaledWindowHeight();

        int sliderY = screenHeight / 2 - 100;
        int sliderH = 200;
        int sliderW = 8;

        int slider1X = screenWidth - 30;
        int slider2X = 20;

        int backX = screenWidth / 2 - 30;
        int backY = screenHeight - 30;
        int backW = screenWidth / 2 + 30;
        int backH = 20;

        // Calculate new scale based on aspect ratio
        float aspectScale = smoothZoom;

        // Pass parameters to renderer
        CelestialBodyRendererPanorama.setRotationX(rotationX);
        CelestialBodyRendererPanorama.setRotationY(rotationY);
        CelestialBodyRendererPanorama.setRotationZ(rotationZ);
        CelestialBodyRendererPanorama.setScale(aspectScale);
        CelestialBodyRendererPanorama.setCameraOffset(panX, panY, cameraZ);
        CelestialBodyRendererPanorama.setPlanetZ(planetZ);
        CelestialBodyRendererPanorama.render(ctx, screenWidth, screenHeight, this.backgroundAlpha, delta);
        if (!solarView) return;

        // BACK BUTTON
        ctx.fill(backX, backY, backX + backW, backY + backH, 0xAA000000);
        ctx.drawText(MinecraftClient.getInstance().textRenderer, "Back", screenWidth / 2, backY + 6, 0xFFFFFF, false);

        // SLIDERS
        ctx.fill(slider1X, sliderY, slider1X + sliderW, sliderY + sliderH, 0xAA333333);
        ctx.fill(slider2X, sliderY, slider2X + sliderW, sliderY + sliderH, 0xAA333333);

        float zoomRatio = (targetZoom - 0.01f) / (10f - 0.01f);
        int zoomHandleY = sliderY + (int)((1f - zoomRatio) * (sliderH - 10));
        ctx.fill(slider1X - 2, zoomHandleY, slider1X + sliderW + 2, zoomHandleY + 10, 0xFFAAAAAA);

        float speedRatio = (targetspeed - 0.01f) / (5f - 0.01f);
        int speedHandleY = sliderY + (int)((1f - speedRatio) * (sliderH - 10));
        ctx.fill(slider2X - 2, speedHandleY, slider2X + sliderW + 2, speedHandleY + 10, 0xFF55FF55);
    }

    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("TAIL"))
    private void handleDragging(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!solarView) return;

        int screenWidth  = ctx.getScaledWindowWidth();
        int screenHeight = ctx.getScaledWindowHeight();

        int sliderY = screenHeight / 2 - 100;
        int sliderH = 200;
        int sliderW = 8;

        int slider1X = screenWidth - 30;
        int slider2X = 20;

        if (draggingSlider) {
            int clamped = Math.min(Math.max(mouseY, sliderY), sliderY + sliderH - 10);
            float t = 1f - (float)(clamped - sliderY) / (sliderH - 10);
            targetZoom = 0.01f + t * (10f - 0.01f);
        }
        if (draggingSlider2) {
            int clamped = Math.min(Math.max(mouseY, sliderY), sliderY + sliderH - 10);
            float t = 1f - (float)(clamped - sliderY) / (sliderH - 10);
            targetspeed = 0.01f + t * (5f - 0.01f);
        }

        if (isDraggingPan) {
            panX += (mouseX - lastMouseX);
            panY += (mouseY - lastMouseY);
        }

        if (isDraggingOrbit) {
            rotationY += (mouseX - lastMouseX) * 0.4f;
            rotationX += (mouseY - lastMouseY) * 0.4f;
            rotationX = Math.max(-90f, Math.min(90f, rotationX));
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;

        if (!MinecraftClient.getInstance().mouse.wasLeftButtonClicked()) {
            draggingSlider = false;
            draggingSlider2 = false;
            isDraggingOrbit = false;
        }
        if (!MinecraftClient.getInstance().mouse.wasRightButtonClicked()) {
            isDraggingPan = false;
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void onClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();

        int sliderY = screenHeight / 2 - 100;
        int sliderH = 200;
        int sliderW = 8;

        int slider1X = screenWidth - 30;
        int slider2X = 20;

        int backX = 10;
        int backY = screenHeight - 30;
        int backW = 60;
        int backH = 20;

        if (!solarView) {
            solarView = true;
            cir.setReturnValue(true);
            return;
        }

        if (mouseX >= backX && mouseX <= backX + backW && mouseY >= backY && mouseY <= backY + backH) {
            solarView = false;
            targetZoom = 0.115f;
            panX = 0; panY = 0;
            cir.setReturnValue(true);
            return;
        }

        if (mouseX >= slider1X - 2 && mouseX <= slider1X + sliderW + 2 && mouseY >= sliderY && mouseY <= sliderY + sliderH) {
            draggingSlider = true;
            cir.setReturnValue(true);
            return;
        }

        if (mouseX >= slider2X - 2 && mouseX <= slider2X + sliderW + 2 && mouseY >= sliderY && mouseY <= sliderY + sliderH) {
            draggingSlider2 = true;
            cir.setReturnValue(true);
            return;
        }

        if (button == 1) {
            isDraggingPan = true;
            lastMouseX = (float) mouseX;
            lastMouseY = (float) mouseY;
            cir.setReturnValue(true);
            return;
        }

        if (button == 0) {
            isDraggingOrbit = true;
            lastMouseX = (float) mouseX;
            lastMouseY = (float) mouseY;
            cir.setReturnValue(true);
        }
    }
}
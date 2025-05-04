package org.copycraftDev.new_horizons.mixin;

import foundry.veil.api.client.render.MatrixStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRendererPanorama;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Unique
    private boolean solarView = false;
    @Unique
    private boolean draggingSlider = false;
    @Unique
    private boolean draggingSlider2 = false;
    @Unique
    private boolean isDraggingPan = false;
    @Unique
    private boolean isDraggingOrbit = false;

    @Unique
    private float targetZoom = 0.115f;
    @Unique
    private float targetspeed = 1f;
    @Unique
    private float smoothZoom = 0.115f;
    @Unique
    private float smoothspeed = 1f;

    @Unique
    private float panX = 0f, panY = 0f;
    @Unique
    private float lastMouseX = 0f, lastMouseY = 0f;

    @Unique
    private float rotationX = 0f;
    @Unique
    private float rotationY = 90f;
    @Unique
    private float rotationZ = 45f;
    @Unique
    private float scale = 0.113f;

    @Unique
    private float cameraX = 0f, cameraY = 0f, cameraZ = 0f;
    @Unique
    private float planetZ = 0f;

    protected TitleScreenMixin(Text title) {
        super(title);
    }


    @Shadow
    private float backgroundAlpha;
    @Shadow
    @Final
    private static Logger LOGGER;



    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("HEAD"))
    public void renderSolar(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (solarView) {
            this.clearChildren();
            
        }

        int screenWidth = ctx.getScaledWindowWidth();
        int screenHeight = ctx.getScaledWindowHeight();

        if (!solarView) {
            this.init(client, screenWidth, screenHeight);
            SplashTextRenderer.HAPPY_NEW_YEAR_.render(ctx, screenWidth, textRenderer, 100);
        }


        smoothZoom += (targetZoom - smoothZoom) * 0.15f;
        smoothspeed += (targetspeed - smoothspeed) * 0.15f;


        int sliderY = screenHeight / 2 - 100;
        int sliderH = 200;
        int sliderW = 8;

        int slider1X = screenWidth - 30;
        int slider2X = 20;

        int backX = screenWidth / 2 - 30;
        int backY = screenHeight - 30;
        int backW = 60;
        int backH = 20;

        int backX2 = screenWidth / 2 - 30;
        int backY2 = screenHeight - 30;
        int backW2 = 60;
        int backH2 = 20;

        float aspectScale = smoothZoom;

        CelestialBodyRendererPanorama.setRotationX(rotationX);
        CelestialBodyRendererPanorama.setRotationY(rotationY);
        CelestialBodyRendererPanorama.setRotationZ(rotationZ);
        CelestialBodyRendererPanorama.setScale(aspectScale);
        CelestialBodyRendererPanorama.setCameraOffset(panX, panY, cameraZ);
        CelestialBodyRendererPanorama.setPlanetZ(planetZ);
        CelestialBodyRendererPanorama.render(ctx, screenWidth, screenHeight, this.backgroundAlpha, delta);

        if (!solarView) {
            ctx.fill(backX2, backY2, backX2 + backW2, backY2 + backH2, 0xAA000000);
            ctx.drawText(MinecraftClient.getInstance().textRenderer, "Start", screenWidth / 2 - 15, backY2 + 6, 0xFFFFFF, false);
        } else {
            ctx.fill(backX, backY, backX + backW, backY + backH, 0xAA000000);
            ctx.drawText(MinecraftClient.getInstance().textRenderer, "Back", screenWidth / 2 - 14, backY + 6, 0xFFFFFF, false);

            ctx.fill(slider1X, sliderY, slider1X + sliderW, sliderY + sliderH, 0xAA333333);
            ctx.fill(slider2X, sliderY, slider2X + sliderW, sliderY + sliderH, 0xAA333333);

            float zoomRatio = (targetZoom - 0.01f) / (10f - 0.01f);
            int zoomHandleY = sliderY + (int) ((1f - zoomRatio) * (sliderH - 10));
            ctx.fill(slider1X - 2, zoomHandleY, slider1X + sliderW + 2, zoomHandleY + 10, 0xFFAAAAAA);

            float speedRatio = (targetspeed - 0.01f) / (5f - 0.01f);
            int speedHandleY = sliderY + (int) ((1f - speedRatio) * (sliderH - 10));
            ctx.fill(slider2X - 2, speedHandleY, slider2X + sliderW + 2, speedHandleY + 10, 0xFF55FF55);
        }
    }

    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("TAIL"))
    private void handleDragging(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!solarView) return;

        final long handle = MinecraftClient.getInstance().getWindow().getHandle();

        int screenWidth = ctx.getScaledWindowWidth();
        int screenHeight = ctx.getScaledWindowHeight();

        int sliderY = screenHeight / 2 - 100;
        int sliderH = 200;
        int sliderW = 8;

        int slider1X = screenWidth - 30;
        int slider2X = 20;

        if (draggingSlider) {
            int clamped = Math.min(Math.max(mouseY, sliderY), sliderY + sliderH - 10);
            float t = 1f - (float) (clamped - sliderY) / (sliderH - 10);
            targetZoom = 0.01f + t * (10f - 0.01f);
        }
        if (draggingSlider2) {
            int clamped = Math.min(Math.max(mouseY, sliderY), sliderY + sliderH - 10);
            float t = 1f - (float) (clamped - sliderY) / (sliderH - 10);
            targetspeed = 0.01f + t * (5f - 0.01f);
        }

        // Calculate mouse deltas
        float dx = mouseX - lastMouseX;
        float dy = mouseY - lastMouseY;

        if (isDraggingPan) {
            panX += dx;
            panY += dy;
        }

        if (isDraggingOrbit) {
            rotationY += dx * 0.4f;
            rotationX += dy * 0.4f;
            rotationX = Math.max(-90f, Math.min(90f, rotationX));
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;

        // Update dragging states based on current input
        if (!InputUtil.isKeyPressed(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            draggingSlider = false;
            draggingSlider2 = false;
            isDraggingOrbit = false;
        }
        if (!InputUtil.isKeyPressed(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            isDraggingPan = false;
        }
    }

    // Add this method to start dragging from mouse click handler
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {

        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        int sliderY = screenHeight / 2 - 100;
        int sliderH = 200;
        int sliderW = 8;

        int slider1X = screenWidth - 30;
        int slider2X = 20;

        int backX = screenWidth / 2 - 30;
        int backY = screenHeight - 30;
        int backW = 60;
        int backH = 20;

        int backX2 = screenWidth / 2 - 30;
        int backY2 = screenHeight - 30;
        int backW2 = 60;
        int backH2 = 20;

        if (!solarView) {
            if (mouseX >= backX2 && mouseX <= backX2 + backW2 && mouseY >= backY2 && mouseY <= backY2 + backH2) {
                solarView = true;
                cir.setReturnValue(true); // Cancel further processing
            }
        } else {

            if (mouseX >= backX && mouseX <= backX + backW && mouseY >= backY && mouseY <= backY + backH) {
                solarView = false;
                cir.setReturnValue(true); // Cancel further processing
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (mouseX >= slider1X && mouseX <= slider1X + sliderW && mouseY >= sliderY && mouseY <= sliderY + sliderH) {
                    draggingSlider = true;
                    cir.cancel();
                } else if (mouseX >= slider2X && mouseX <= slider2X + sliderW && mouseY >= sliderY && mouseY <= sliderY + sliderH) {
                    draggingSlider2 = true;
                    cir.cancel();
                } else {
                    isDraggingOrbit = true;
                }
            }

            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                isDraggingPan = true;
            }

            lastMouseX = (float) mouseX;
            lastMouseY = (float) mouseY;
        }
    }
}
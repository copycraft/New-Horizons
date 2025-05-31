package org.copycraftDev.new_horizons.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.Camera;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRendererPanorama;
import org.copycraftDev.new_horizons.extrastuff.TextureResizer;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends net.minecraft.client.gui.screen.Screen {
    @Unique private boolean solarView = false, showSidebar = false;
    @Unique private boolean draggingZ = false, draggingS = false, dragOrbit = false, dragPan = false;
    @Unique private float targetZoom = 0.115f, smoothZoom = 0.115f;
    @Unique private float targetSpeed = 1, smoothSpeed = 1;
    @Unique private float panX, panY, lastX, lastY;
    @Unique private float rotX = 0, rotY = 90, rotZ = 45;
    @Unique private String selected = "sun", hovered = null;
    @Unique private final int icon = 20;
    @Shadow private float backgroundAlpha;
    @Shadow @Final private static org.slf4j.Logger LOGGER;
    @Shadow @Nullable private net.minecraft.client.gui.screen.SplashTextRenderer splashText;

    @Shadow protected abstract void init();

    private static Identifier splashRes = null;

    protected TitleScreenMixin(Text t) { super(t); }

    @Inject(method = "init", at = @At("HEAD"))
    public void onInit(CallbackInfo ci) {
        selected = "sun";
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (!solarView) return;
        if (!(selected == null)){
        Camera cam = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vector3f tgt = CelestialBodyRendererPanorama.getPlanetLocation(selected).toVector3f();
        long w = MinecraftClient.getInstance().getWindow().getHandle();
        boolean left = GLFW.glfwGetMouseButton(w, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(w, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        // Hook zoom into camera distance: distance inversely proportional to smoothZoom
        float r = 100f / smoothZoom;
        float ax = (float) Math.toRadians(rotX), ay = (float) Math.toRadians(rotY);
        float cx = tgt.x + r * (float) Math.cos(ax) * (float) Math.cos(ay);
        float cy = tgt.y + r * (float) Math.sin(ax);
        float cz = tgt.z + r * (float) Math.cos(ax) * (float) Math.sin(ay);
        try {
            var acc = (CameraAccessor) cam;
            acc.invokeSetPos(cx, cy, cz);
            acc.invokeSetRotation(rotY, rotX);
        } catch (Exception e) {
            LOGGER.warn("cam fail", e);
        }
    }}

    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("HEAD"))
    public void renderSolar(DrawContext ctx, int mx, int my, float d, CallbackInfo ci) {
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();
        if (solarView) {
            clearChildren();
            splashText = null;
        } else {
            MinecraftClient client = MinecraftClient.getInstance();
            init(client, w, h);

            int sw = 265, sh = 66;
            if (splashRes == null) {
                Identifier resized = null;

                // Try up to 2 times
                for (int attempt = 1; attempt <= 2; attempt++) {
                    try {
                        resized = TextureResizer.resizeTexture(
                                "minecraft",
                                "textures/gui/title/titlesplash.png",
                                sw, sh,
                                "ts_res",
                                false
                        );
                        if (resized != null) {
                            break;
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Attempt #{} to resize splash texture failed", attempt, e);
                    }
                }

                if (resized != null) {
                    splashRes = resized;
                } else {
                    LOGGER.warn("All resize attempts failed—using vanilla splash texture");
                    splashRes = Identifier.of("minecraft/textures/gui/title/titlesplash.png");
                }
            }

            // only draw if we have a non-null Identifier
            if (splashRes != null) {
                ctx.drawTexture(
                        splashRes,
                        w / 2 - sw / 2, 0,
                        0, 0,
                        sw, sh, sw, sh
                );
            }
        }

        smoothZoom  += (targetZoom  - smoothZoom ) * 0.15f;
        smoothSpeed += (targetSpeed - smoothSpeed) * 0.15f;
        CelestialBodyRendererPanorama.setRotationX(rotX);
        CelestialBodyRendererPanorama.setRotationY(rotY);
        CelestialBodyRendererPanorama.setRotationZ(rotZ);
        CelestialBodyRendererPanorama.setScale(smoothZoom);
        CelestialBodyRendererPanorama.setOffsetX(panX);
        CelestialBodyRendererPanorama.setOffsetY(panY);
        CelestialBodyRendererPanorama.setSimulationSpeed(targetSpeed);
        CelestialBodyRendererPanorama.setPlanetZ(0);
        CelestialBodyRendererPanorama.render(ctx, w, h, backgroundAlpha, d, solarView ? selected : null);


    hovered = null;
        for (var e : CelestialBodyRendererPanorama.getScreenPositions().entrySet()) {
            float dx = mx - e.getValue().x, dy = my - e.getValue().y;
            if (dx * dx + dy * dy < 100) { hovered = e.getKey(); break; }
        }
        if (hovered != null) {
            var sp = CelestialBodyRendererPanorama.getScreenPositions().get(hovered);
            ctx.fill((int) sp.x - 6, (int) sp.y - 6, (int) sp.x + 6, (int) sp.y + 6, 0x80FFFFFF);
            int tx = mx + 8, ty = my + 8, tw = textRenderer.getWidth(hovered);
            ctx.fill(tx - 2, ty - 2, tx + tw + 2, ty + 10, 0xAA000000);
            ctx.drawText(textRenderer, Text.of(hovered), tx, ty, 0xFFFFFF, false);
        }

        if (solarView) {
            ctx.drawText(textRenderer, Text.of("☰"), w - icon, 5, 0xFFFFFF, false);
            if (showSidebar) {
                ctx.fill(w - 100, 0, w, h, 0xAA000000);
                int y = 10;
                for (String n : CelestialBodyRendererPanorama.getScreenPositions().keySet()) {
                    ctx.drawText(textRenderer, Text.of(n), w - 95, y, 0xFFFFFF, false); y += 12;
                }
            }
        }
        int sy = h / 2 - 100, sh = 200;
        ctx.fill(w / 2 - 30, h - 30, w / 2 + 30, h - 10, 0xAA000000);
        ctx.drawText(textRenderer, Text.of(solarView ? "Back" : "Start"), w / 2 - 25, h - 26, 0xFFFFFF, false);
        if (solarView) {
            ctx.fill(w - 30, sy, w - 22, sy + sh, 0xAA333333);
            ctx.fill(20, sy, 28, sy + sh, 0xAA333333);
            int zy = sy + (int) ((1f - ((targetZoom - 0.01f) / 9.99f)) * (sh - 10));
            int sy2 = sy + (int) ((1f - ((targetSpeed - 0.01f) / 4.99f)) * (sh - 10));
            ctx.fill(w - 32, zy, w - 20, zy + 10, 0xFFAAAAAA);
            ctx.fill(18, sy2, 30, sy2 + 10, 0xFF55FF55);
        }
    }

    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("TAIL"))
    private void drag(DrawContext ctx, int mx, int my, float d, CallbackInfo ci) {
        if (!solarView) return;
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();
        int sy = h / 2 - 100, sh = 200;
        boolean left = GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        if (left && !draggingZ && mx >= w - 30 && mx <= w - 22 && my >= sy && my <= sy + sh) draggingZ = true;
        if (left && !draggingS && mx >= 20 && mx <= 28 && my >= sy && my <= sy + sh) draggingS = true;
        if (left && !dragOrbit && !draggingZ && !draggingS) dragOrbit = true;
        if (right && !dragPan) dragPan = true;

        if (draggingZ) { int c = Math.min(Math.max(my, sy), sy + sh - 10); float t = 1f - (c - sy) / (sh - 10f); targetZoom = 0.01f + t * 9.99f; }
        if (draggingS) { int c = Math.min(Math.max(my, sy), sy + sh - 10); float t = 1f - (c - sy) / (sh - 10f); targetSpeed = 0.01f + t * 4.99f; }

        float dx = mx - lastX, dy = my - lastY;
        if (dragOrbit) { rotY = (rotY + dx) % 360; rotX = Math.max(-90, Math.min(90, rotX + dy)); }
        if (dragPan) { panX += dx; panY += dy; }

        lastX = mx; lastY = my;
        if (!left) { draggingZ = draggingS = dragOrbit = false; }
        if (!right) { dragPan = false; }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void click(double mx, double my, int btn, CallbackInfoReturnable<Boolean> cir) {
        int w = width, h = height;
        if (solarView) {
            if (mx >= w - icon - 5 && mx <= w - 5 && my >= 5 && my <= 5 + icon) { showSidebar = !showSidebar; cir.setReturnValue(true); return; }
            if (showSidebar && mx >= w - 100) {
                int y = 10;
                for (String n : CelestialBodyRendererPanorama.getScreenPositions().keySet()) {
                    if (my >= y && my < y + 12) { selected = n; showSidebar = false; cir.setReturnValue(true); return; }
                    y += 12;
                }
            }
            if (hovered != null && btn == GLFW.GLFW_MOUSE_BUTTON_LEFT) { selected = hovered; cir.setReturnValue(true); return; }
        }
        if (mx >= w / 2 - 30 && mx <= w / 2 + 30 && my >= h - 30 && my <= h - 10) { solarView = !solarView; cir.setReturnValue(true); }
    }
}

package org.copycraftDev.new_horizons.extrastuff;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

public class ZoomHandler implements ClientModInitializer {

    private static boolean zoomActive = false;
    private static double zoomLevel = 1.0; // 1.0 = normal, smaller = zoomed in
    private static final double ZOOM_MIN = 0.25;  // max zoom in (4x zoom)
    private static final double ZOOM_MAX = 1.0;   // normal FOV
    private static final double ZOOM_STEP = 0.05; // zoom step per scroll tick
    private static double FOV; // zoom step per scroll tick

    private GLFWScrollCallback scrollCallback;
    private GLFWScrollCallback originalScrollCallback;
    private boolean callbackRegistered = false;

    // Public methods to toggle or start zoom
    public static void toggleZoom() {
        zoomActive = !zoomActive;
        if (!zoomActive) {
            zoomLevel = ZOOM_MAX;
        }
    }

    public static void startZoom() {
        zoomActive = true;
        zoomLevel = ZOOM_MAX;
    }

    public static void stopZoom() {
        zoomActive = false;
        zoomLevel = ZOOM_MAX;
    }

    public static boolean isZoomActive() {
        return zoomActive;
    }

    @Override
    public void onInitializeClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        FOV = client.options.getFov().getValue();

        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if (!callbackRegistered && client.getWindow() != null) {
                long windowHandle = client.getWindow().getHandle();

                scrollCallback = new GLFWScrollCallback() {
                    @Override
                    public void invoke(long window, double horizontal, double vertical) {
                        if (zoomActive) {
                            // Negative vertical = scroll up = zoom in
                            zoomLevel -= vertical * ZOOM_STEP;
                            if (zoomLevel < ZOOM_MIN) zoomLevel = ZOOM_MIN;
                            if (zoomLevel > ZOOM_MAX) zoomLevel = ZOOM_MAX;
                        } else {
                            // Pass scroll event to original handler when not zooming
                            if (originalScrollCallback != null) {
                                originalScrollCallback.invoke(window, horizontal, vertical);
                            }
                        }
                    }
                };

                originalScrollCallback = GLFW.glfwSetScrollCallback(windowHandle, scrollCallback);
                callbackRegistered = true;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(clientTick -> {
            if (client.player == null) return;

            if (zoomActive) {
                // Multiply FOV by zoomLevel
                client.options.getFov().setValue((int) (client.options.getFov().getValue() * zoomLevel));

            } else {
                // Reset FOV if zoom inactive
                // Use default FOV (90) or store original if you want to preserve it between zooms
                client.options.getFov().setValue((int) FOV);
            }
        });

        // Optional: draw zoom level HUD (remove if you don't want)
        HudRenderCallback.EVENT.register((DrawContext context, RenderTickCounter tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (zoomActive && mc.player != null) {
                String text = String.format("Zoom: %.2fx", 1 / zoomLevel);
                context.drawText(mc.textRenderer, text, 10, 10, 0xFFFFFF, true);
            }
        });
    }
}

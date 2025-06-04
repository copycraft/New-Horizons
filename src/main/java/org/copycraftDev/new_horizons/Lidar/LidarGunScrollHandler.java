package org.copycraftDev.new_horizons.Lidar;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.copycraftDev.new_horizons.core.items.custom.LidarGunItem;

public class LidarGunScrollHandler implements ClientModInitializer {

    public static int selectedIndex = 0;
    public static float radius = 5.0f;
    public static final float MIN_RADIUS = 1.0f;
    public static final float MAX_RADIUS = 20.0f;

    public static final String[] OPTIONS = new String[] {
            "Standard",
            "Deepsearch",
            "Grenade"
    };

    private GLFWScrollCallback scrollCallback;
    private GLFWScrollCallback originalScrollCallback;
    private boolean callbackRegistered = false;

    @Override
    public void onInitializeClient() {
        MinecraftClient client = MinecraftClient.getInstance();

        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if (!callbackRegistered && client.getWindow() != null) {
                long windowHandle = client.getWindow().getHandle();

                scrollCallback = new GLFWScrollCallback() {
                    @Override
                    public void invoke(long window, double h, double v) {
                        if (client.player != null &&
                                client.player.getMainHandStack().getItem() instanceof LidarGunItem) {

                            boolean ctrlHeld = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;

                            boolean shiftHeld = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

                            if (ctrlHeld) {
                                if (v > 0) {
                                    selectedIndex = MathHelper.floorMod(selectedIndex - 1, OPTIONS.length);
                                } else if (v < 0) {
                                    selectedIndex = MathHelper.floorMod(selectedIndex + 1, OPTIONS.length);
                                }
                                return; // Consumed by Lidar Gun logic
                            } else if (shiftHeld && "Standard".equals(OPTIONS[selectedIndex])) {
                                if (v > 0) {
                                    radius = Math.max(MIN_RADIUS, radius - 1);
                                } else if (v < 0) {
                                    radius = Math.min(MAX_RADIUS, radius + 1);
                                }
                                return; // Consumed by Lidar Gun logic
                            }
                        }

                        // Not handled by Lidar Gun: pass to original callback
                        if (originalScrollCallback != null) {
                            originalScrollCallback.invoke(window, h, v);
                        }
                    }
                };

                // Register our scroll callback, caching the original one
                originalScrollCallback = GLFW.glfwSetScrollCallback(windowHandle, scrollCallback);
                callbackRegistered = true;
            }
        });

        HudRenderCallback.EVENT.register((DrawContext context, RenderTickCounter tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;

            boolean ctrlHeld = GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;

            boolean shiftHeld = GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

            if (shiftHeld && "Standard".equals(OPTIONS[selectedIndex]) &&
                    mc.player.getMainHandStack().getItem() instanceof LidarGunItem) {

                String displayText = "Selected Radius: " + radius + "°";
                context.drawText(mc.textRenderer, displayText, 10, 10, 0xFFFFFF, true);
            }

            if (ctrlHeld &&
                    mc.player.getMainHandStack().getItem() instanceof LidarGunItem) {

                String displayText = "Selected Mode: " + OPTIONS[selectedIndex];
                context.drawText(mc.textRenderer, displayText, 10, 10, 0xFFFFFF, true);
            }
        });
    }
}

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
import org.copycraftDev.new_horizons.core.items.custom.SprayPaintItem;

public class SpraypaintScrollHandler implements ClientModInitializer {

    public static int selectedcIndex = 0;
    public static float VALUE1 = 0f;
    public static float VALUE2 = 0f;
    public static float VALUE3 = 0f;
    public static final float MIN_VALUE = 1.0f;
    public static final float MAX_VALUE = 20.0f;

    public static final String[] COLORS = new String[] {
            "Red",
            "Green",
            "Blue"
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
                                client.player.getMainHandStack().getItem() instanceof SprayPaintItem) {

                            boolean ctrlHeld = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;

                            boolean shiftHeld = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

                            if (ctrlHeld) {
                                if (v > 0) {
                                    selectedcIndex = MathHelper.floorMod(selectedcIndex - 1, COLORS.length);
                                } else if (v < 0) {
                                    selectedcIndex = MathHelper.floorMod(selectedcIndex + 1, COLORS.length);
                                }
                                return; // Consumed by Lidar Gun logic
                            } else if (shiftHeld && "Red".equals(COLORS[selectedcIndex])) {
                                if (v < 0) {
                                    VALUE1 = Math.max(MIN_VALUE, VALUE1 - 1);
                                } else if (v > 0) {
                                    VALUE1 = Math.min(MAX_VALUE, VALUE1 + 1);
                                }
                                return; // Consumed by Lidar Gun logic
                            }
                            if (shiftHeld && "Green".equals(COLORS[selectedcIndex])) {
                                if (v < 0) {
                                    VALUE2 = Math.max(MIN_VALUE, VALUE2 - 1);
                                } else if (v > 0) {
                                    VALUE2 = Math.min(MAX_VALUE, VALUE2 + 1);
                                }
                                return; // Consumed by Lidar Gun logic
                            }
                            if (shiftHeld && "Blue".equals(COLORS[selectedcIndex])) {
                                if (v < 0) {
                                    VALUE3 = Math.max(MIN_VALUE, VALUE3 - 1);
                                } else if (v > 0) {
                                    VALUE3 = Math.min(MAX_VALUE, VALUE3 + 1);
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

            if (shiftHeld && "Red".equals(COLORS[selectedcIndex]) &&
                    mc.player.getMainHandStack().getItem() instanceof SprayPaintItem) {

                String displayText = "Selected Red value: " + VALUE1 + "°";
                context.drawText(mc.textRenderer, displayText, 10, 10, 0xFFFFFF, true);
            }
            if (shiftHeld && "Green".equals(COLORS[selectedcIndex]) &&
                    mc.player.getMainHandStack().getItem() instanceof SprayPaintItem) {

                String displayText = "Selected Green value: " + VALUE2 + "°";
                context.drawText(mc.textRenderer, displayText, 10, 10, 0xFFFFFF, true);
            }
            if (shiftHeld && "Blue".equals(COLORS[selectedcIndex]) &&
                    mc.player.getMainHandStack().getItem() instanceof SprayPaintItem) {

                String displayText = "Selected Blue value: " + VALUE3 + "°";
                context.drawText(mc.textRenderer, displayText, 10, 10, 0xFFFFFF, true);
            }

            if (ctrlHeld &&
                    mc.player.getMainHandStack().getItem() instanceof SprayPaintItem) {

                String displayText = "Selected Color: " + COLORS[selectedcIndex];
                context.drawText(mc.textRenderer, displayText, 10, 10, 0xFFFFFF, true);
            }
        });
    }
}

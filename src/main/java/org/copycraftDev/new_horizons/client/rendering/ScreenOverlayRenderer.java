package org.copycraftDev.new_horizons.client.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class ScreenOverlayRenderer {
    private static boolean enabled = false;
    private static String message = "";

    public static void register() {
        HudRenderCallback.EVENT.register(ScreenOverlayRenderer::renderOverlay);
    }

    private static void renderOverlay(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!enabled || message.isEmpty()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        drawContext.drawText(mc.textRenderer, message, 5, 5, 0xFFFFFFFF, false);
    }


    // Public API
    public static void showOverlay(String msg) {
        message = msg != null ? msg : "";
        enabled = true;
    }

    public static void clearOverlay() {
        enabled = false;
        message = "";
    }

    public static void toggle(boolean flag) {
        enabled = flag;
    }
}

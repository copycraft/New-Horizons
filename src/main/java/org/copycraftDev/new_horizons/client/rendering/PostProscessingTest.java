package org.copycraftDev.new_horizons.client.rendering;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;

import java.io.IOException;

@LibyAutoRegister(method = "register")
public class PostProscessingTest {
    private static boolean initialized = false;

    public static void register() {
        WorldRenderEvents.LAST.register(context -> {
            if (!initialized) {
                try {
                    init();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load red tint shader", e);
                }
            }
        });
    }

    private static void init() throws IOException {
        MinecraftClient client = MinecraftClient.getInstance();
        Framebuffer mainFramebuffer = client.getFramebuffer();


        initialized = true;
    }
}

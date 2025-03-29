package org.copycraftDev.new_horizons.core.redoingminecraftshit;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import nazario.liby.api.registry.auto.LibyEntrypoints;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

@LibyAutoRegister(method = "register", entrypoints = LibyEntrypoints.MAIN)
public class TickHandler {
    public static float partialTicks = 1.0f;
    private static long lastTickTime = System.nanoTime();
    private static final long TICK_DURATION_NANOS = 50_000_000L; // 50 ms

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long now = System.nanoTime();
            long elapsed = now - lastTickTime;
            // Clamp the value between 0 and 1
            partialTicks = Math.min(1.0f, (float) elapsed / TICK_DURATION_NANOS);
            // Reset for the next tick
            lastTickTime = now;
        });
    }
}


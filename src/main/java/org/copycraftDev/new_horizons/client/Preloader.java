package org.copycraftDev.new_horizons.client;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@LibyAutoRegister(method = "init")
public class Preloader {
    private static final int RADIUS = 6;      // or pull from client.options.viewDistance
    private static final int PER_TICK = 16;   // how many chunks to kick off each tick

    private static boolean preloading = false;
    private static final Set<ChunkPos> pending = new HashSet<>();

    /** Call from your main mod’s onInitializeClient() */
    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            startPreload();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!preloading || client.world == null || client.player == null) return;
            tickPreload(client);
        });
    }

    private static void startPreload() {
        preloading = true;
        pending.clear();
    }

    private static void tickPreload(MinecraftClient client) {
        ClientChunkManager mgr = client.world.getChunkManager();

        // First tick: populate chunk list around player
        if (pending.isEmpty()) {
            int cx = client.player.getBlockPos().getX() >> 4;
            int cz = client.player.getBlockPos().getZ() >> 4;
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    pending.add(new ChunkPos(cx + dx, cz + dz));
                }
            }
        }

        // Pump a few loads per tick
        int count = 0;
        Iterator<ChunkPos> it = pending.iterator();
        while (it.hasNext() && count++ < PER_TICK) {
            ChunkPos pos = it.next();
            mgr.getChunk(pos.x, pos.z, ChunkStatus.FULL, true);
            it.remove();
        }

        // When done, let the game clear the screen
        if (pending.isEmpty()) {
            preloading = false;
            client.execute(() -> client.setScreen(null));
        }
    }

    /** Used by the Mixin to decide if we should cancel a screen */
    public static boolean isPreloading() {
        return preloading;
    }
}

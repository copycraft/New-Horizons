package org.copycraftDev.new_horizons.core.bigbang;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRenderer;

import java.util.*;

public class BigBangCutsceneManager {
    private static final Map<UUID, CutsceneState> activeCutscenes = new HashMap<>();

    public static void execute(ServerPlayerEntity player) {
        if (activeCutscenes.containsKey(player.getUuid())) return;
        player.changeGameMode(GameMode.SPECTATOR);
        ServerWorld world = player.getServerWorld();
        double x = -20, y = 20, z = 20;
        float yaw = -160f, pitch = 70f;

        // Setup camera entity
        ArmorStandEntity camera = new ArmorStandEntity(world, x, y, z);
        camera.setInvisible(true);
        camera.setInvulnerable(true);
        camera.setNoGravity(true);
        camera.setYaw(yaw);
        camera.setPitch(pitch);
        world.spawnEntity(camera);



        CutsceneState state = new CutsceneState(player.getUuid(), camera.getUuid());
        activeCutscenes.put(player.getUuid(), state);

        // Hide sun
        CelestialBodyRenderer.setShouldRender(false);

        // Move player to cutscene location
        player.teleport(world, x, y, z, yaw, pitch);
        player.setCameraEntity(camera);
        player.changeGameMode(GameMode.SPECTATOR);

        // Start Big Bang particles
        BigBangManager.spawnParticles(player.getServer(), 1000, 50);
    }

    public static void tick(MinecraftServer server) {
        for (UUID playerId : new ArrayList<>(activeCutscenes.keySet())) {
            CutsceneState state = activeCutscenes.get(playerId);
            state.ticks++;

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
            if (player == null) continue;

            // After ~2s (collapse + explosion done)
            if (!state.particlesFinished && state.ticks >= 140) {
                state.particlesFinished = true;
                state.ticks = 0;
            }

            // Wait 60 seconds before ending cutscene
            if (state.particlesFinished && state.ticks >= 1200) {
                ArmorStandEntity camera = (ArmorStandEntity) ((ServerWorld) player.getWorld()).getEntity(state.cameraId);
                if (camera != null) {
                    camera.discard();
                }

                player.setCameraEntity(player);
                player.changeGameMode(GameMode.SURVIVAL);
                player.teleport(player.getServerWorld(), -200, 100, 0, 0, 0);

                CelestialBodyRenderer.setShouldRender(true);
                player.sendMessage(Text.literal("You awaken in a new world..."), false);

                activeCutscenes.remove(playerId);
            }
        }
    }

    private static class CutsceneState {
        UUID playerId;
        UUID cameraId;
        int ticks = 0;
        boolean particlesFinished = false;

        CutsceneState(UUID playerId, UUID cameraId) {
            this.playerId = playerId;
            this.cameraId = cameraId;
        }
    }
}

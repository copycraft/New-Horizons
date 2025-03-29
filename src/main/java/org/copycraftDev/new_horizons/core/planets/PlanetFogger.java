package org.copycraftDev.new_horizons.core.planets;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.copycraftDev.new_horizons.core.particle.ModParticles;

@LibyAutoRegister(method = "init")
public class PlanetFogger {

    private static final double CENTER_X = 100.0;
    private static final double CENTER_Y = 64.0;
    private static final double CENTER_Z = 100.0;
    private static final double DETECTION_RADIUS = 30.0; // Increased detection range
    private static final double FOG_RADIUS = 10.0; // Fog effect radius

    public static void init() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (isWithinDetectionRange(player.getX(), player.getY(), player.getZ())) {
                    spawnFog(player);
                }
            }
        });
    }

    private static boolean isWithinDetectionRange(double x, double y, double z) {
        double dx = x - CENTER_X;
        double dy = y - CENTER_Y;
        double dz = z - CENTER_Z;
        return dx * dx + dy * dy + dz * dz <= DETECTION_RADIUS * DETECTION_RADIUS;
    }

    private static void spawnFog(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getWorld();
        double px = player.getX();
        double py = player.getEyeY();
        double pz = player.getZ();

        // Instant, dense fog covering 10-block radius
        world.spawnParticles(
                ModParticles.FOG_PARTICLE,
                px, py, pz,
                500,  // Increased particle count for denser fog
                FOG_RADIUS, FOG_RADIUS, FOG_RADIUS, // Fog spread radius
                0.0  // Static particles
        );
    }
}
package org.copycraftDev.new_horizons.client.planets;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MeteorScheduler {
    public static int tickCounter = 0;
    public static final int TICKS_PER_RUN = 200;
    public static final int DIAMETER = 2000;
    public static final int RADIUS = DIAMETER / 2;
    public static final int STEP = 5;
    public static final int POINTS = DIAMETER / STEP;

    public static void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < TICKS_PER_RUN) return;
        tickCounter = 0;

        ServerWorld world = server.getOverworld();
        if (world == null) return;

        BlockPos center = world.getSpawnPos();
        double centerX = center.getX() + 0.5;
        double centerY = center.getY(); // Optional: raise if needed
        double centerZ = center.getZ() + 0.5;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            for (int i = 0; i < POINTS; i++) {
                double angle = 2 * Math.PI * i / POINTS;
                int x = (int) Math.round(centerX + Math.cos(angle) * RADIUS);
                int z = (int) Math.round(centerZ + Math.sin(angle) * RADIUS);
                int y = (int) centerY;

                // Run as this player
                ServerCommandSource source = player.getCommandSource()
                        .withPosition(new Vec3d(x + 0.5, y, z + 0.5)) // makes ~ ~ ~ meaningful if used
                        .withSilent();

                // Execute the meteor command as the player
                String command = String.format("quasar new_horizons:meteor %d %d %d", x, y, z);
                server.getCommandManager().executeWithPrefix(source, command);
            }
        }
    }
}

package org.copycraftDev.new_horizons.client.planets;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.planets.MeteorScheduler;

public class MeteorCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Register the command /execute_meteor
        dispatcher.register(
                CommandManager.literal("execute_meteor")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            MinecraftServer server = source.getServer();
                            ServerWorld world = server.getOverworld();
                            if (world == null) return Command.SINGLE_SUCCESS;

                            BlockPos center = world.getSpawnPos();
                            double centerX = center.getX() + 0.5;
                            double centerY = center.getY();
                            double centerZ = center.getZ() + 0.5;

                            // Loop over all players and trigger meteor command manually
                            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                for (int i = 0; i < MeteorScheduler.POINTS; i++) {
                                    double angle = 2 * Math.PI * i / MeteorScheduler.POINTS;
                                    int x = (int) Math.round(centerX + Math.cos(angle) * MeteorScheduler.RADIUS);
                                    int z = (int) Math.round(centerZ + Math.sin(angle) * MeteorScheduler.RADIUS);
                                    int y = (int) centerY;

                                    // Execute meteor command as the player
                                    ServerCommandSource playerSource = player.getCommandSource()
                                            .withPosition(new Vec3d(x + 0.5, y, z + 0.5)) // use position of each point
                                            .withSilent(); // Silent command to avoid feedback spam

                                    // Build and run the meteor command at each (x, y, z)
                                    String command = String.format("quasar new_horizons:meteor %d %d %d", x, y, z);
                                    server.getCommandManager().executeWithPrefix(playerSource, command);
                                }
                            }

                            return Command.SINGLE_SUCCESS;
                        })
        );
    }
}

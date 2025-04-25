package org.copycraftDev.new_horizons.core.networking;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class TeleportUtils {

    public static void teleport(ServerPlayerEntity player, String dimensionId, double x, double y, double z) {
        RegistryKey<World> targetKey = RegistryKey.of(RegistryKey.ofRegistry(Identifier.of("minecraft:dimension")), Identifier.of(dimensionId));
        ServerWorld targetWorld = player.server.getWorld(targetKey);

        if (targetWorld != null) {
            player.teleport(targetWorld, x, y, z, player.getYaw(), player.getPitch());
        }
    }
}

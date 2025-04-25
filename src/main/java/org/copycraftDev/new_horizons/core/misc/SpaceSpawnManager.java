package org.copycraftDev.new_horizons.core.misc;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import nazario.liby.api.registry.auto.LibyEntrypoints;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import org.copycraftDev.new_horizons.core.blocks.ModBlocks;
/**
 * Helper class to handle spawning players in the custom space dimension
 * with a stone platform and an item reward beneath them.
 * Call SpaceSpawnManager.register() from your ModInitializer onInitialize().
 */

@LibyAutoRegister(method = "register", entrypoints = LibyEntrypoints.MAIN)
public class SpaceSpawnManager {
    public static final Identifier SPACE_DIMENSION_ID =  Identifier.of("new_horizons", "space");
    public static final RegistryKey<World> SPACE_DIMENSION = RegistryKey.of(RegistryKeys.WORLD, SPACE_DIMENSION_ID);

    /**
     * Registers lifecycle and player join events to manage teleportation,
     * chunk loading, platform generation, and item granting.
     */
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> loadSpawnChunks(server));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> teleportOnFirstJoin(handler.getPlayer(), server));
    }

    private static void loadSpawnChunks(MinecraftServer server) {
        ServerWorld spaceWorld = server.getWorld(SPACE_DIMENSION);
        if (spaceWorld != null) {
            spaceWorld.getChunkManager().addTicket(
                    ChunkTicketType.START,
                    new ChunkPos(0, 0),
                    1,
                    Unit.INSTANCE
            );
        }
    }

    private static void teleportOnFirstJoin(ServerPlayerEntity player, MinecraftServer server) {
        if (!player.getCommandTags().contains("spawned_in_space")) {
            ServerWorld spaceWorld = server.getWorld(SPACE_DIMENSION);
            if (spaceWorld != null) {
                double spawnX = -200;
                double spawnY = 100;
                double spawnZ = 0;

                // Teleport the player
                player.teleport(spaceWorld, spawnX, spawnY, spawnZ, player.getYaw(), player.getPitch());
                player.addCommandTag("spawned_in_space");

                // Generate a 3x3 stone platform beneath the player
                generatePlatform(spaceWorld, spawnX, spawnY, spawnZ);

                // Give the player a diamond to get started
                giveStartingItem(player);
            }
        }
    }

    /**
     * Creates a 3x3 stone platform centered under the given coordinates.
     */
    private static void generatePlatform(ServerWorld world, double x, double y, double z) {
        int centerX = (int) Math.floor(x);
        int baseY = (int) y - 1;
        int centerZ = (int) Math.floor(z);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = new BlockPos(centerX + dx, baseY, centerZ + dz);
                world.setBlockState(pos, Blocks.STONE.getDefaultState());
            }
        }
    }

    /**
     * Gives the player a starter item (diamond).
     */
    private static void giveStartingItem(ServerPlayerEntity player) {
        ItemStack diamond = new ItemStack(ModBlocks.CAPTAINS_CHAIR, 1);
        if (!player.getInventory().insertStack(diamond)) {
            // If inventory is full, drop on ground
            player.dropItem(diamond, false);
        }
    }
}
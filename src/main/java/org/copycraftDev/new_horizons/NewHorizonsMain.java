package org.copycraftDev.new_horizons;

import com.mojang.brigadier.CommandDispatcher;
import foundry.veil.Veil;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import nazario.liby.api.registry.auto.LibyEntrypoints;
import nazario.liby.api.registry.auto.LibyRegistryLoader;
import nazario.liby.api.registry.runtime.recipe.LibyIngredient;
import nazario.liby.api.registry.runtime.recipe.LibyRecipeRegistry;
import nazario.liby.api.registry.runtime.recipe.types.LibyShapelessCraftingRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import org.copycraftDev.new_horizons.client.planets.MeteorCommand;
import org.copycraftDev.new_horizons.client.planets.MeteorScheduler;
import org.copycraftDev.new_horizons.core.bigbang.BigBangCutsceneManager;
import org.copycraftDev.new_horizons.core.blocks.ModBlocks;
import org.copycraftDev.new_horizons.core.entity.ModEntities;
import org.copycraftDev.new_horizons.core.items.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.copycraftDev.new_horizons.core.world.biome.ModBiomes;

import java.util.UUID;

public class NewHorizonsMain implements ModInitializer {

    public static final String MOD_ID = "new_horizons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final SoundEvent ENGINE_AMBIANCE = register("engine_ambiance");
    public static final SoundEvent ENGINE_BROKEN   = register("engine_broken");
    public static final SoundEvent ENGINE_POWERUP  = register("engine_powerup");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.of("new_horizons", name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }


    @Override
    public void onInitialize() {
        LibyRegistryLoader.load("org.copycraftDev.new_horizons", LOGGER, LibyEntrypoints.MAIN);
        Veil.init();
        ServerTickEvents.END_SERVER_TICK.register(MeteorScheduler::onServerTick);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
            MeteorCommand.register(dispatcher);  // Register the custom command
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            server.execute(() -> {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    BigBangCutsceneManager.execute(player);
                }
            });
        });
        createRecipes();

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient() &&
                    hand == Hand.MAIN_HAND &&
                    world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.GOLD_BLOCK) {

                LOGGER.info("[NewHorizons] Gold block clicked—calling teleportPlayerWithPreload");
                NewHorizonsMain.teleportPlayerWithPreload(
                        (ServerPlayerEntity) player,
                        World.NETHER,
                        new BlockPos(0, 100, 0)
                );
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });

    }
    public void createRecipes() {
        LibyRecipeRegistry.addRecipe(
                new LibyShapelessCraftingRecipe(
                        Identifier.of(MOD_ID, "wood_top_planks"),
                        new LibyIngredient[]{
                                LibyIngredient.createItem(ModBlocks.REDWOOD_LOGS)
                        },
                        ModBlocks.REDWOOD_PLANKS.liby$getId(),
                        4
                )
        );
    }

    public static void teleportPlayerWithPreload(ServerPlayerEntity player,
                                                 RegistryKey<World> targetWorldKey,
                                                 BlockPos targetPos) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            LOGGER.error("[NewHorizons] server instance was null!");
            return;
        }

        ServerWorld targetWorld = server.getWorld(targetWorldKey);
        if (targetWorld == null) {
            LOGGER.error("[NewHorizons] target world {} not found!", targetWorldKey.getValue());
            return;
        }

        ChunkPos chunkPos = new ChunkPos(targetPos);
        LOGGER.info("[NewHorizons] Adding portal ticket for chunk {}", chunkPos);

        //noinspection unchecked
        ChunkTicketType<BlockPos> portalTicket =
                (ChunkTicketType<BlockPos>)(Object) ChunkTicketType.PORTAL;

        // Keep that chunk loaded
        targetWorld.getChunkManager().addTicket(
                portalTicket,
                chunkPos,
                2,
                targetPos
        );

        // Schedule the teleport on the next server tick,
        // giving Minecraft time to send chunk data properly.
        server.execute(() -> {
            LOGGER.info("[NewHorizons] Performing delayed teleport to {}", targetPos);
            player.teleport(
                    targetWorld,
                    targetPos.getX() + 0.5,
                    targetPos.getY(),
                    targetPos.getZ() + 0.5,
                    player.getYaw(),
                    player.getPitch()
            );
            LOGGER.info("[NewHorizons] Teleport complete for {}", player.getName().getString());
        });
    }
    public static Identifier id(String name){
        return Identifier.of(MOD_ID, name);
    }
}



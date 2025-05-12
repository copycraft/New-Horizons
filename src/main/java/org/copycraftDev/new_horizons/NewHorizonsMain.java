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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.client.planets.MeteorCommand;
import org.copycraftDev.new_horizons.client.planets.MeteorScheduler;
import org.copycraftDev.new_horizons.core.bigbang.BigBangCutsceneManager;
import org.copycraftDev.new_horizons.core.blocks.ModBlocks;
import org.copycraftDev.new_horizons.core.entity.ModEntities;
import org.copycraftDev.new_horizons.core.items.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.copycraftDev.new_horizons.core.world.biome.ModBiomes;

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
    // In your client entrypoint class:


    public static Identifier id(String name){
        return Identifier.of(MOD_ID, name);
    }
}



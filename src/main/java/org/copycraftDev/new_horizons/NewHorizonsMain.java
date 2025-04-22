package org.copycraftDev.new_horizons;

import com.mojang.brigadier.CommandDispatcher;
import foundry.veil.Veil;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import nazario.liby.api.registry.auto.LibyEntrypoints;
import nazario.liby.api.registry.auto.LibyRegistryLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.client.planets.MeteorCommand;
import org.copycraftDev.new_horizons.client.planets.MeteorScheduler;
import org.copycraftDev.new_horizons.core.entity.ModEntities;
import org.copycraftDev.new_horizons.core.items.ModItems;
import org.copycraftDev.new_horizons.core.render.ShaderClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.copycraftDev.new_horizons.core.world.biome.ModBiomes;

public class NewHorizonsMain implements ModInitializer {

    public static final String MOD_ID = "new_horizons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Identifier CUSTOM_POST_PIPELINE = Identifier.of(MOD_ID,"planet");
    private static final Identifier CUSTOM_POST_SHADER = Identifier.of(MOD_ID,"planet");


    @Override
    public void onInitialize() {
        LibyRegistryLoader.load("org.copycraftDev.new_horizons", LOGGER, LibyEntrypoints.MAIN);
        Veil.init();
        ServerTickEvents.END_SERVER_TICK.register(MeteorScheduler::onServerTick);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
            MeteorCommand.register(dispatcher);  // Register the custom command
        });
    }
    public static Identifier id(String name){
        return Identifier.of(MOD_ID, name);
    }
}



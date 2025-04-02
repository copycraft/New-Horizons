package org.copycraftDev.new_horizons;

import com.mojang.brigadier.CommandDispatcher;
import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.command.v2 .CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;

@LibyAutoRegister(method = "registerCommands")
public class ModCommands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerReloadPlanetsCommand(dispatcher);
        });
    }

    //=================[ CREATE SPELL COMMAND ]=================
    private static void registerReloadPlanetsCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("reloadPlanets")
                .requires(source -> source.hasPermissionLevel(2)) // Admin-level permission

                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity player = source.getPlayer();
                                            executeReloadPlanets();
                                            return 1;
                                        }));
    }

    private static void executeReloadPlanets() {
        CelestialBodyRegistry.registerAllPlanets("new_horizons/planets", NewHorizonsMain.MOD_ID);
    }
}

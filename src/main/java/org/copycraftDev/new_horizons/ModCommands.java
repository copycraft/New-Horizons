package org.copycraftDev.new_horizons;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRenderer;
import org.copycraftDev.new_horizons.client.rendering.ScreenOverlayRenderer;
import org.copycraftDev.new_horizons.core.bigbang.BigBangClientManager;
import org.copycraftDev.new_horizons.core.bigbang.BigBangCutsceneManager;
import org.copycraftDev.new_horizons.physics.block.AssemblerBlock;

/**
 * All /new_horizons commands are server-side only.
 * Directly invokes client-side renderers without networking (unsupported in dedicated server).
 */
@LibyAutoRegister(method = "registerCommands")
public class ModCommands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("new_horizons")
                            .requires(src -> src.hasPermissionLevel(2))

                            // reload planet data
                            .then(CommandManager.literal("reloadPlanets")
                                    .executes(ctx -> {
                                        CelestialBodyRegistry.registerAllPlanets(
                                                "new_horizons/planets", NewHorizonsMain.MOD_ID
                                        );
                                        return 1;
                                    })
                            )
                            .then(CommandManager.literal("setassemblermaxrange")
                                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                            .executes(ctx -> {
                                                int val = IntegerArgumentType.getInteger(ctx, "value");
                                                AssemblerBlock.maxRange = val;
                                                ctx.getSource().sendFeedback(() -> Text.of("Assembler max range set to " + val), false);
                                                return 1;
                                            })
                                    )
                            )


                            // trigger kaboom effect
                            .then(CommandManager.literal("kaboom")
                                    .then(CommandManager.argument("planet", StringArgumentType.string())
                                            .executes(ctx -> {
                                                String planet = StringArgumentType.getString(ctx, "planet");
                                                CelestialBodyRenderer.kaboom(planet);
                                                return 1;
                                            })
                                    )
                            )

                            // restore explosions
                            .then(CommandManager.literal("restore")
                                    .executes(ctx -> {
                                        CelestialBodyRenderer.restore();
                                        return 1;
                                    })
                            )

                            // toggle planet rendering on/off
                            .then(CommandManager.literal("toggleRender")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean flag = BoolArgumentType.getBool(ctx, "enabled");
                                                CelestialBodyRenderer.setShouldRender(flag);
                                                return 1;
                                            })
                                    )
                            )

                            // blackout: full-screen black + message
                            .then(CommandManager.literal("blackout")
                                    .then(CommandManager.argument("message", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                String msg = StringArgumentType.getString(ctx, "message");
                                                ScreenOverlayRenderer.showOverlay(msg);
                                                return 1;
                                            })
                                    )
                            )

                            // clear overlay
                            .then(CommandManager.literal("clearOverlay")
                                    .executes(ctx -> {
                                        ScreenOverlayRenderer.clearOverlay();
                                        return 1;
                                    })
                            )
                            // initiate bigbang particle event
                            // /new_horizons bigbang <count> <radius>
                            .then(CommandManager.literal("bigbang")
                                    .then(CommandManager.argument("count", IntegerArgumentType.integer(1))
                                            .then(CommandManager.argument("radius", IntegerArgumentType.integer(1, 200))
                                                    .executes(ctx -> {
                                                        int count = IntegerArgumentType.getInteger(ctx, "count");
                                                        int radius = IntegerArgumentType.getInteger(ctx, "radius");

                                                        if (ctx.getSource().getServer().isDedicated()) {
                                                            ctx.getSource().sendError(Text.of("This only works in singleplayer."));
                                                            return 0;
                                                        }

                                                        MinecraftClient client = MinecraftClient.getInstance();
                                                        client.execute(() -> BigBangClientManager.spawnParticles( count, radius));

                                                        ctx.getSource().sendFeedback(() -> Text.of("Big Bang initiated with " + count + " particles in " + radius + " block radius!"), false);
                                                        return 1;
                                                    })
                                            )
                                    ))
                            // toggle bigbang particles
                            .then(CommandManager.literal("executeBigBang")
                                    .executes(ctx -> {
                                        ServerPlayerEntity player = ctx.getSource().getPlayer();
                                        BigBangCutsceneManager.execute(player);
                                        return 1;
                                    })
                            )



            );
        });
    }
}

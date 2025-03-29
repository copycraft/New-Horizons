package org.copycraftDev.new_horizons.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import foundry.veil.Veil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.copycraftDev.new_horizons.client.particle.ModParticlesClient;
import org.copycraftDev.new_horizons.client.rendering.LazuliHudRenderStep;
import org.copycraftDev.new_horizons.client.rendering.ModShaders;
import org.copycraftDev.new_horizons.core.particle.FogParticle;
import org.copycraftDev.new_horizons.core.particle.ModParticles;
import org.copycraftDev.new_horizons.core.redoingminecraftshit.TickHandler;
import org.copycraftDev.new_horizons.core.render.PlanetRenderer;
import org.copycraftDev.new_horizons.core.render.ShaderClass;
import org.lwjgl.glfw.GLFW;

public class NewHorizonsClient implements ClientModInitializer {
    // Static field to store the current tick delta
    private static float currentTickDelta = 0.0F;

    public static float getCurrentTickDelta() {
        return currentTickDelta;
    }

    @Override
    public void onInitializeClient() {
        Veil.init();
        ModShaders.registerShaders();
        LazuliHudRenderStep.register();

        ParticleFactoryRegistry.getInstance().register(ModParticles.FOG_PARTICLE, spriteProvider ->
                new ModParticlesClient.FogParticle.Factory(spriteProvider)
        );

        // Register a keybinding
        KeyBinding flashlightKeyBinding = new KeyBinding(
                "key.new_horizons.override_my_ass",  // Translation key
                GLFW.GLFW_KEY_F5,                   // Default key
                "category.new_horizons"             // Keybinding category
        );
        KeyBindingHelper.registerKeyBinding(flashlightKeyBinding);

        // Register the client-side /fog command
        CommandDispatcher<FabricClientCommandSource> dispatcher = ClientCommandManager.getActiveDispatcher();
        if (dispatcher != null) {
            dispatcher.register(
                    ClientCommandManager.literal("fog")
                            .then(ClientCommandManager.argument("start", FloatArgumentType.floatArg(0.0F))
                                    .then(ClientCommandManager.argument("end", FloatArgumentType.floatArg(0.0F))
                                            .executes(context -> {
                                                float start = FloatArgumentType.getFloat(context, "start");
                                                float end = FloatArgumentType.getFloat(context, "end");

                                                // Update fog settings (assuming FogSettings is defined elsewhere)
                                                FogSettings.setFogStart(start);
                                                FogSettings.setFogEnd(end);

                                                // Provide feedback to the player
                                                MinecraftClient client = MinecraftClient.getInstance();
                                                if (client.player != null) {
                                                    client.player.sendMessage(
                                                            Text.literal("Fog start set to " + start + " and end set to " + end)
                                                                    .formatted(Formatting.GREEN),
                                                            false
                                                    );
                                                }
                                                return 1;
                                            })
                                    )
                            )
            );
        }

        ParticleFactoryRegistry.getInstance().register(ModParticles.FOG_PARTICLE, FogParticle.Factory::new);

        WorldRenderEvents.LAST.register((context) -> {
            MatrixStack matrixStack = context.matrixStack();
            RenderTickCounter tickCounter = context.tickCounter();
            float tickDelta = tickCounter.getLastFrameDuration();


            // Get the vertex consumer provider from the Minecraft client buffers
            VertexConsumerProvider.Immediate vertexConsumers =
                    MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

            // Call your shader method using the updated tick delta
            ShaderClass.applyShader(matrixStack, vertexConsumers, currentTickDelta);
        });
        WorldRenderEvents.LAST.register((WorldRenderContext context) -> {
            MatrixStack matrixStack = context.matrixStack();
            if (matrixStack == null) {
                return;
            }
            PlanetRenderer.renderPlanets(matrixStack, TickHandler.partialTicks);
        });

    }
}

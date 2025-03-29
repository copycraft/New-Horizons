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


        ParticleFactoryRegistry.getInstance().register(ModParticles.FOG_PARTICLE, FogParticle.Factory::new);

    }
}

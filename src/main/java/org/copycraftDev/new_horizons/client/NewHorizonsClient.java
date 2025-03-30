package org.copycraftDev.new_horizons.client;

import foundry.veil.Veil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.option.KeyBinding;
import org.copycraftDev.new_horizons.client.particle.ModParticlesClient;
import org.copycraftDev.new_horizons.client.planets.PlanetRegistry;
import org.copycraftDev.new_horizons.client.rendering.PlanetRenderer;
import org.copycraftDev.new_horizons.core.particle.FogParticle;
import org.copycraftDev.new_horizons.core.particle.ModParticles;
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

        PlanetRenderer.register();

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

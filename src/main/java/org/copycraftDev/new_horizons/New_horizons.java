package org.copycraftDev.new_horizons;

import foundry.veil.Veil;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.registry.RegistryKeys;
import org.copycraftDev.new_horizons.core.items.ModItems;
import org.copycraftDev.new_horizons.core.world.dimension.ModDimensions;
import org.copycraftDev.new_horizons.datagen.ModWorldGenerator;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class New_horizons implements ModInitializer {

    public static final String MOD_ID = "new_horizons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        ModItems.initialize();
        Veil.init();
    }
}

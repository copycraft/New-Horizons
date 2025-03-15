package org.copycraftDev.new_horizons;

import foundry.veil.Veil;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import nazario.liby.registry.auto.LibyAutoRegister;
import nazario.liby.registry.auto.LibyRegistryLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKeys;
import org.copycraftDev.new_horizons.core.blocks.ModBlocks;
import org.copycraftDev.new_horizons.core.entity.ModEntities;
import org.copycraftDev.new_horizons.core.items.ModItems;
import org.copycraftDev.new_horizons.core.world.dimension.DimensionJsonGenerator;
import org.copycraftDev.new_horizons.core.world.dimension.ModDimensions;
import org.copycraftDev.new_horizons.datagen.ModWorldGenerator;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;



public class New_horizons implements ModInitializer {

    public static final String MOD_ID = "new_horizons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        Veil.init();
        ModItems.initialize();
        ModBlocks.initialize();
        ModEntities.initialize();
        LibyRegistryLoader.load("copycraftDev.new_horizons");
        List.of("space", "deep_void", "nebula", "lunar").forEach(dimName ->
                DimensionJsonGenerator.createDimensionJson(MOD_ID, dimName));

    }
}


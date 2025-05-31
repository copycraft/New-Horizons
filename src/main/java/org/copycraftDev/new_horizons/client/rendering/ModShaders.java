package org.copycraftDev.new_horizons.client.rendering;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;

import java.io.IOException;
import java.util.Optional;

@LibyAutoRegister(method = "registerShaders")
public class ModShaders {
    public static String RENDER_TYPE_PLANET = "rendertype_planet";
    public static String RENDER_TYPE_PLANET_WITH_NIGHT = "rendertype_planet_with_night";
    public static String RENDER_TYPE_ATMOSPHERE = "rendertype_test_atmosphere";
    public static String RENDER_TYPE_STAR = "rendertype_star";
    public static String RENDER_TYPE_STAR_AURA = "rendertype_star_aura";
    public static String RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1 = "rendertype_explosion_1";
    public static String RENDER_TYPE_RING = "rendertype_ring";
    public static String TEST_BLACK_HOLE = "rendertype_test_black_hole";


    public static final String BLUR_PROCESSOR = "shaders/post/blur.json";

    public static void registerShaders() throws IOException {
        LazuliShaderRegistry.registerShader(RENDER_TYPE_PLANET, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(RENDER_TYPE_PLANET_WITH_NIGHT, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(RENDER_TYPE_ATMOSPHERE, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(RENDER_TYPE_STAR, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(RENDER_TYPE_STAR_AURA, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(RENDER_TYPE_RING, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(TEST_BLACK_HOLE, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);


        LazuliShaderRegistry.registerPostProcessingShader(BLUR_PROCESSOR);



    }
}
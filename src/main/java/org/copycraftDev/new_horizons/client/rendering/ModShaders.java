package org.copycraftDev.new_horizons.client.rendering;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.minecraft.client.render.VertexFormats;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;

@LibyAutoRegister(method = "registerShaders")
public class ModShaders {
    public static String RENDER_TYPE_PLANET = "rendertype_planet";
    public static String RENDER_TYPE_PLANET_WITH_NIGHT = "rendertype_planet_with_night";
    public static String RENDER_TYPE_ATMOSPHERE = "rendertype_test_atmosphere";
    public static String RENDER_TYPE_STAR = "rendertype_star";
    public static String RENDER_TYPE_STAR_AURA = "rendertype_star_aura";


    public static void registerShaders(){
        LazuliShaderRegistry.registerShader(RENDER_TYPE_PLANET, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(RENDER_TYPE_PLANET_WITH_NIGHT, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(RENDER_TYPE_ATMOSPHERE, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(RENDER_TYPE_STAR, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        LazuliShaderRegistry.registerShader(RENDER_TYPE_STAR_AURA, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
    }
}
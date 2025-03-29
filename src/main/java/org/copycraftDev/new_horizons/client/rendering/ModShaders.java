package org.copycraftDev.new_horizons.client.rendering;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.minecraft.client.render.VertexFormats;

@LibyAutoRegister(method = "registerShaders")
public class ModShaders {
    public static String RENDER_TYPE_PLANET = "rendertype_planet";
    public static String RENDER_TYPE_ATMOSPHERE = "rendertype_test_atmosphere";


    public static void registerShaders(){
        LazuliShaderRegistry.registerShader(RENDER_TYPE_PLANET, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
    }
}
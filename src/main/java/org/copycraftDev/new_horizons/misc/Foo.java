package org.copycraftDev.new_horizons.misc;


import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.shader.ShaderPreDefinitions;
import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.minecraft.util.Identifier;

//@LibyAutoRegister(method = "onPreRender")
public class Foo {

    private static final Identifier SHADER_ID = Identifier.of("new_horizons", "definition");

    // Some event fired before rendering
    public static void onPreRender() {
        VeilRenderer renderer = VeilRenderSystem.renderer();
        ShaderPreDefinitions definitions = renderer.getShaderDefinitions();

        // This adds #define EXAMPLE_DEFINITION to all shaders that depend on it
        definitions.set("definition");
    }
}
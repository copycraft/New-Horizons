package org.copycraftDev.new_horizons.core.planets;

import foundry.veil.api.client.render.VeilRenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PlanetShaderClass { // Ensure this matches the file name!

    private static final Identifier CUSTOM_SHADER = Identifier.of("new_horizons", "program/planet");

    public static void applyShader(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float partialTicks) {
        MinecraftClient client = MinecraftClient.getInstance();
        GameRenderer renderer = client.gameRenderer;

        ShaderProgram shader = VeilRenderSystem.setShader(CUSTOM_SHADER);
        if (shader == null) {
            return;
        }

        try {
            shader.setFloat("CustomValue",37.2F);
            shader.setMatrix("CustomProjection", new Matrix4f().ortho(0, 10, 10, 0, 0.3F, 100.0F, false));
        } catch (Exception e) {
            System.err.println("Failed to set shader uniforms: " + e.getMessage());
        }

        shader.bind();
        // Rendering logic here...
        ShaderProgram.unbind();
    }
}

package org.copycraftDev.new_horizons.core.render;

import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.New_horizons;
import org.joml.Matrix4f;

public class RenderClass {

    public static final Identifier CUSTOM_SHADER = Identifier.of(New_horizons.MOD_ID,"planet");

    public static void render(MatrixStack stack, VertexConsumer source, float partialTicks) {
        ShaderProgram shader = VeilRenderSystem.setShader(CUSTOM_SHADER);
        if (shader == null) {
            return;
        }

        shader.setFloat("u_Radius", 0.5F);
        shader.setMatrix("u_MVP", new Matrix4f().ortho(0, 10, 10, 0, 0.3F, 100.0F, false));

        shader.bind();

        // Submit a single vertex at (0, 0, 0)
        source.vertex(stack.peek().getPositionMatrix(), 100.0f, 100.0f, 100.0f)
                .color(255, 255, 255, 255)  // Full white color (RGBA)
                .texture(0.0f, 0.0f)  // Default texture coordinates
                .overlay(OverlayTexture.DEFAULT_UV)  // Default overlay texture
                .light(0xF000F0)  // Max brightness
                .normal(0.0f, 1.0f, 0.0f);  // Normal vector pointing up

        ShaderProgram.unbind(); // Unbind shader after rendering.

    }
}

package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.function.Supplier;

public class LazuliHudRenderStep {

    private static Camera camera;
    private static Matrix4f matrix4f;
    private static ShaderProgram TEST_SHADER;


    private static final Identifier ALBEDO = Identifier.of("new_horizons", "textures/test_textures/planet_albedo.png");
    private static final Identifier HEIGHT = Identifier.of("new_horizons", "textures/test_textures/planet_albedo.png");
    private static final Identifier NORMAL = Identifier.of("new_horizons", "textures/test_textures/planet_normal.png");

    public static void register() {
        WorldRenderEvents.LAST.register((context) -> {
            // Validate camera & matrix before rendering
            if (camera == null || matrix4f == null) {
                return; // Skip rendering if data isn't set yet
            }

            Tessellator tessellator = Tessellator.getInstance();


            // Load shader
            TEST_SHADER = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET);
            if (TEST_SHADER == null) {
                return; // Shader not loaded yet, skip rendering
            }

            // Set the shader
            Supplier<ShaderProgram> shaderSupplier = () -> TEST_SHADER;
            RenderSystem.setShader(shaderSupplier);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.assertOnRenderThread();

            //==================================[Matrix transformations]=========================================================
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.multiplyPositionMatrix(matrix4f);
            matrixStack.push();
            matrixStack.multiply(camera.getRotation());

            Matrix4f matrix4f2 = matrixStack.peek().getPositionMatrix();


            RenderSystem.setShaderTexture(0, ALBEDO);
            RenderSystem.setShaderTexture(1, HEIGHT);
            RenderSystem.setShaderTexture(2, NORMAL);
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            LazuliGeometryBuilder.buildTexturedSphere(50, 2, new Vec3d(0,0,0), camera, matrix4f2, bufferBuilder);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

            RenderSystem.setShaderTexture(0, HEIGHT);
            RenderSystem.setShaderTexture(1, HEIGHT);
            RenderSystem.setShaderTexture(2, NORMAL);
            bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            LazuliGeometryBuilder.buildTexturedSphere(100, 300, new Vec3d(0,-600,0), camera, matrix4f2, bufferBuilder);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

            bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.setShaderColor(0.1F, 0.3F, 1.0F, 0.1F);
            LazuliGeometryBuilder.buildTexturedSphere(30, 2.2F, new Vec3d(0,0,0), camera, matrix4f2, bufferBuilder);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());



            // Start buffer and draw geometry
//            bufferBuilder.vertex(matrix4f2, x, y, z).color(0,0,1,1);
//            bufferBuilder.vertex(matrix4f2, x, y, z).color(0,1,1,1);
//            bufferBuilder.vertex(matrix4f2, x, y, z).color(1,1,1,1);
//            bufferBuilder.vertex(matrix4f2, x, y, z).color(1,0,1,1);
//
//            bufferBuilder.vertex(matrix4f2, 1 + x, 0 + y, 1 + z).texture(1,1);
//            bufferBuilder.vertex(matrix4f2, -1 + x, 0 + y, 1 + z).texture(0,1);
//            bufferBuilder.vertex(matrix4f2, -1 + x, 0 + y, -1 + z).texture(0,0);
//            bufferBuilder.vertex(matrix4f2, 1 + x, 0 + y, -1 + z).texture(1,0);

            // Draw the buffer


            // Cleanup
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
            RenderSystem.depthMask(true);
            RenderSystem.setShaderFogColor(0,0,0);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.enableDepthTest();


        });
    }

    public static void setThings(Camera cam, Matrix4f matrix, MatrixStack stack) {
        camera = cam;
        matrix4f = matrix;
    }
}
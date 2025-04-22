package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.AxisAngle4f;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CelestialBodyRendererPanorama {
    private static final AtomicReference<Float> time = new AtomicReference<>(0f);

    private static float rotationX = 0f;
    private static float rotationY = 90f;  // Default 90 degrees on Y-axis
    private static float rotationZ = 0f;
    private static float scale = 1.0f;  // Default scale (1 = normal size)

    // Offsets for X and Y positioning (can be dynamically updated)
    private static float offsetX = 0f;
    private static float offsetY = 0f;

    public static void setRotationX(float rotationX) {
        CelestialBodyRendererPanorama.rotationX = rotationX;
    }

    public static void setRotationY(float rotationY) {
        CelestialBodyRendererPanorama.rotationY = rotationY;
    }

    public static void setRotationZ(float rotationZ) {
        CelestialBodyRendererPanorama.rotationZ = rotationZ;
    }

    public static void setScale(float scale) {
        CelestialBodyRendererPanorama.scale = scale;
    }

    public static void setOffsetX(float offsetX) {
        CelestialBodyRendererPanorama.offsetX = offsetX;
    }

    public static void setOffsetY(float offsetY) {
        CelestialBodyRendererPanorama.offsetY = offsetY;
    }

    public static void render(DrawContext context, int width, int height, float backgroundAlpha, float delta) {
        time.updateAndGet(v -> v + delta);

        Tessellator tessellator = Tessellator.getInstance();
        MatrixStack matrixStack = new MatrixStack();

        // Calculate the center of the screen
        float centerX = width / 2.0f;
        float centerY = height / 2.0f;

        // Get current projection matrix
        matrixStack.multiplyPositionMatrix(context.getMatrices().peek().getPositionMatrix());

        // ✅ Begin matrix transformation for the celestial system
        matrixStack.push();

        // Apply scaling based on screen resolution
        matrixStack.scale(scale, scale, scale);

        // Apply rotation using the quaternion (apply X, Y, Z rotations)
        applyRotation(matrixStack);

        // ✅ Move the celestial body to the center of the screen, then apply the offsets
        matrixStack.translate(centerX + offsetX, centerY + offsetY, -200.0f);  // Offset Z to place it behind UI elements

        Matrix4f proj = matrixStack.peek().getPositionMatrix();

        MinecraftClient client = MinecraftClient.getInstance();
        Camera cam = client.gameRenderer.getCamera();

        for (Map.Entry<Identifier, CelestialBodyRegistry.CelestialBodyData> entry : CelestialBodyRegistry.getAllPlanets().entrySet()) {
            CelestialBodyRegistry.CelestialBodyData body = entry.getValue();

            RenderSystem.setShaderTexture(0, body.surfaceTexture);
            if (body.hasDarkAlbedoMap) {
                RenderSystem.setShaderTexture(1, body.darkAlbedoMap);
            }

            ShaderProgram shader = body.isStar
                    ? LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR)
                    : (body.hasDarkAlbedoMap
                    ? LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET_WITH_NIGHT)
                    : LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET));

            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderFogStart(Integer.MAX_VALUE);
            RenderSystem.setShaderFogEnd(Integer.MAX_VALUE);
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.assertOnRenderThread();

            BufferBuilder buf = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            float angle = (float) body.rotationSpeed * time.get();
            LazuliGeometryBuilder.buildTexturedSphere(
                    5,
                    (float) body.radius,
                    body.center,
                    new Vec3d(0, 1, 0),
                    angle,
                    false,
                    cam,
                    proj,
                    buf
            );
            BufferRenderer.drawWithGlobalProgram(buf.end());
        }

        // ✅ End of transformation — restore to original matrix
        matrixStack.pop();

        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
        RenderSystem.setShaderFogColor(0, 0, 0);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void applyRotation(MatrixStack matrixStack) {
        // Apply X, Y, Z rotations (ensure correct order)
        Quaternionf rotation = new Quaternionf();

        // Apply X-axis rotation first
        rotation.set(new AxisAngle4f(1, 0, 0, (float) Math.toRadians(rotationX)));
        matrixStack.multiply(rotation);

        // Apply Y-axis rotation second
        rotation.set(new AxisAngle4f(0, 1, 0, (float) Math.toRadians(rotationY)));
        matrixStack.multiply(rotation);

        // Apply Z-axis rotation last
        rotation.set(new AxisAngle4f(0, 0, 1, (float) Math.toRadians(rotationZ)));
        matrixStack.multiply(rotation);
    }
}

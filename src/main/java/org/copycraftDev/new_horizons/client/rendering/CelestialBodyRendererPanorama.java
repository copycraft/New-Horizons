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
import org.copycraftDev.new_horizons.lazuli_snnipets.LapisRenderer;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CelestialBodyRendererPanorama {
    private static final AtomicReference<Float> time = new AtomicReference<>(0f);

    private static float rotationX      = 0f;
    private static float rotationY      = 90f;
    private static float rotationZ      = 0f;
    private static float scale          = 1.0f;

    private static float offsetX        = 0f;
    private static float offsetY        = 0f;

    private static float cameraOffsetX  = 0f;
    private static float cameraOffsetY  = 0f;
    private static float cameraOffsetZ  = 0f;

    /** Base depth at which all bodies are drawn; per-body X goes to depth now. */
    private static float planetZ        = 0f;

    private static ShaderProgram
            RENDER_TYPE_PLANET,
            RENDER_TYPE_PLANET_WITH_NIGHT,
            RENDER_TYPE_ATMOSPHERE,
            RENDER_TYPE_STAR,
            RENDER_TYPE_STAR_AURA,
            RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1;

    // Track last window size to minimize scale recalculation
    private static int lastScreenWidth  = -1;
    private static int lastScreenHeight = -1;

    public static void setRotationX(float r)        { rotationX      = r; }
    public static void setRotationY(float r)        { rotationY      = r; }
    public static void setRotationZ(float r)        { rotationZ      = r; }
    public static void setScale(float s)            { scale          = s; }
    public static void setOffsetX(float x)          { offsetX        = x; }
    public static void setOffsetY(float y)          { offsetY        = y; }
    public static void setCameraOffset(float x, float y, float z) {
        cameraOffsetX = x;
        cameraOffsetY = y;
        cameraOffsetZ = z;
    }
    public static void setPlanetZ(float z)          { planetZ        = z; }

    /**
     * Renders the panorama, centering at (width/2, height/2), then placing each
     * body at ( body.center.z → X, body.center.y → Y, planetZ + body.center.x → Z ).
     */
    public static void render(DrawContext context, int width, int height, float backgroundAlpha, float delta) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull(); // Optional: ensures visibility
        RenderSystem.setShaderFogStart(Float.MAX_VALUE); // Disable fog
        RenderSystem.setShaderFogEnd(Float.MAX_VALUE);

        time.updateAndGet(v -> v + delta);

        Tessellator tessellator = Tessellator.getInstance();
        MatrixStack ms = new MatrixStack();

        // start from the current projection
        ms.multiplyPositionMatrix(context.getMatrices().peek().getPositionMatrix());
        ms.push();

        // compute true screen center plus offsets
        float centerX = (context.getScaledWindowHeight() * 0.5f) + offsetX + cameraOffsetX;
        float centerY = (height * 0.5f) + offsetY + cameraOffsetY;
        float screencenterx = context.getScaledWindowWidth() / 2;
        float screencentery = context.getScaledWindowHeight() / 2;
        // Ensure the correct transformation matrix
        ms.translate(screencenterx, screencentery, -10000);
        ms.scale((float) (scale*1.5), (float) (scale*1.5), (float) (scale*1.5));
        applyRotation(ms); // Make sure rotation is applied correctly


        Matrix4f proj = ms.peek().getPositionMatrix();
        MinecraftClient client = MinecraftClient.getInstance();
        Camera cam = client.gameRenderer.getCamera();

        for (Map.Entry<Identifier, CelestialBodyRegistry.CelestialBodyData> e : CelestialBodyRegistry.getAllPlanets().entrySet()) {
            CelestialBodyRegistry.CelestialBodyData body = e.getValue();

            // bind textures
            RenderSystem.setShaderTexture(0, body.surfaceTexture);

            // choose shader
            ShaderProgram shader = body.isStar
                    ? LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR)
                    : (body.hasDarkAlbedoMap
                    ? LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET_WITH_NIGHT)
                    : LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET));
            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderFogStart(Integer.MAX_VALUE);
            RenderSystem.setShaderFogEnd(Integer.MAX_VALUE);
            RenderSystem.disableCull();
            RenderSystem.assertOnRenderThread();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            BufferBuilder buf = tessellator.begin(
                    VertexFormat.DrawMode.QUADS,
                    VertexFormats.POSITION_TEXTURE_COLOR_NORMAL
            );
            // cast to float again for safety
            float spinAngle = (float)(body.rotationSpeed * time.get());

            // SWAPPED AXES: z→X, y→Y, x→Z
            Vec3d orig = body.center;
            float x = (float) orig.z;
            float y = (float) orig.y;
            float z = planetZ + (float) orig.x; // Ensure fixed Z-depth

            RENDER_TYPE_PLANET = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET);
            RENDER_TYPE_PLANET_WITH_NIGHT = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET_WITH_NIGHT);
            RENDER_TYPE_ATMOSPHERE = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_ATMOSPHERE);
            RENDER_TYPE_STAR = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR);
            RENDER_TYPE_STAR_AURA = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR_AURA);
            RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1 = LazuliShaderRegistry.getShader(ModShaders.RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1);

            Vec3d renderCenter = new Vec3d(x, y, z);
            if (body.hasDarkAlbedoMap) {
                LapisRenderer.setShaderTexture(3, body.darkAlbedoMap);
                LapisRenderer.setShader(RENDER_TYPE_PLANET_WITH_NIGHT);
            } else {
                LapisRenderer.setShader(RENDER_TYPE_PLANET);
            }
            LazuliGeometryBuilder.buildTexturedSphere(
                    64,
                    (float) body.radius,
                    renderCenter,
                    new Vec3d(0, 1, 0),
                    spinAngle,
                    false,
                    cam,
                    proj,
                    buf
            );
            BufferRenderer.drawWithGlobalProgram(buf.end());
        }

        // restore
        ms.pop();
        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
        RenderSystem.setShaderFogColor(0, 0, 0);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }


    private static void applyRotation(MatrixStack ms) {
        Quaternionf q = new Quaternionf();
        q.set(new AxisAngle4f(1, 0, 0, (float) Math.toRadians(rotationX)));
        ms.multiply(q);
        q.set(new AxisAngle4f(0, 1, 0, (float) Math.toRadians(rotationY)));
        ms.multiply(q);
        q.set(new AxisAngle4f(0, 0, 45, (float) Math.toRadians(rotationZ)));
        ms.multiply(q);
    }
}

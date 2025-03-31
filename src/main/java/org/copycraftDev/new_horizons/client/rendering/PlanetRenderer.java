package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.planets.PlanetRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class PlanetRenderer {

    private static Camera camera;
    private static Matrix4f matrix4f;
    private static ShaderProgram RENDER_TYPE_PLANET;
    private static ShaderProgram RENDER_TYPE_PLANET_WITH_NIGHT;
    private static ShaderProgram RENDER_TYPE_ATMOSPHERE;

    public static void register() {
        AtomicReference<Float> time = new AtomicReference<>((float) 0);

        WorldRenderEvents.LAST.register((context) -> {
            camera = context.camera();
            matrix4f = context.positionMatrix();
            // Validate camera & matrix before rendering
            if (camera == null || matrix4f == null) {
                return; // Skip rendering if data isn't set yet
            }


            Tessellator tessellator = Tessellator.getInstance();
            time.updateAndGet(v ->  (v + context.tickCounter().getTickDelta(true)));


            // Load shader
            RENDER_TYPE_PLANET = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET);
            RENDER_TYPE_PLANET_WITH_NIGHT = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET_WITH_NIGHT);
            RENDER_TYPE_ATMOSPHERE = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_ATMOSPHERE);
            if (RENDER_TYPE_PLANET == null) {
                return; // Shader not loaded yet, skip rendering
            }


            //==================================[Matrix transformations]=========================================================
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.multiplyPositionMatrix(matrix4f);
            matrixStack.push();
            matrixStack.multiply(camera.getRotation());
            Matrix4f matrix4f2 = matrixStack.peek().getPositionMatrix();

            Map<Identifier, PlanetRegistry.PlanetData> Planets = PlanetRegistry.getAllPlanets();

            Map<Identifier, PlanetRegistry.PlanetData> planets = PlanetRegistry.getAllPlanets();

            for (Map.Entry<Identifier, PlanetRegistry.PlanetData> entry : planets.entrySet()) {
                Identifier id = entry.getKey();
                PlanetRegistry.PlanetData planet = entry.getValue();

                RenderSystem.setShaderTexture(0, planet.surfaceTexture);
                RenderSystem.setShaderTexture(1, planet.heightMap);
                RenderSystem.setShaderTexture(2, planet.normalMap);

                float angle = (float) planet.rotationSpeed * time.get();
                BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

                if (planet.hasDarkAlbedoMap) {
                    RenderSystem.setShaderTexture(3, planet.darkAlbedoMap);
                    Supplier<ShaderProgram> shaderSupplier = () -> RENDER_TYPE_PLANET_WITH_NIGHT;
                    RenderSystem.setShaderTexture(3, planet.darkAlbedoMap);
                    RenderSystemSetup(shaderSupplier);
                    LazuliGeometryBuilder.buildTexturedSphere(100, (float) planet.radius, planet.center, new Vec3d(0,1, 0), angle,false,  camera, matrix4f2, bufferBuilder);
                    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                } else {
                    Supplier<ShaderProgram> shaderSupplier = () -> RENDER_TYPE_PLANET;
                    RenderSystemSetup(shaderSupplier);
                    LazuliGeometryBuilder.buildTexturedSphere(100, (float) planet.radius, planet.center, new Vec3d(0,1, 0), angle, false, camera, matrix4f2, bufferBuilder);
                    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

                }

                if (planet.hasAtmosphere){
                    bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
                    RenderSystem.setShaderTexture(0, planet.darkAlbedoMap);
                    Supplier<ShaderProgram> shaderSupplier = () -> RENDER_TYPE_ATMOSPHERE;
                    RenderSystemSetup(shaderSupplier);
                    RenderSystem.enableCull();
                    LazuliGeometryBuilder.buildTexturedSphereWithCameraRelativeNormals(40, (float) planet.atmosphereRadius, planet.center, 0, true, camera, matrix4f2, bufferBuilder);
                    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                }
            }





            // Cleanup
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
            RenderSystem.depthMask(true);
            RenderSystem.setShaderFogColor(0,0,0);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.enableDepthTest();


        });
    }


    private static void RenderSystemSetup(Supplier<ShaderProgram> shaderSupplier){
        RenderSystem.setShader(shaderSupplier);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.assertOnRenderThread();
    }
}
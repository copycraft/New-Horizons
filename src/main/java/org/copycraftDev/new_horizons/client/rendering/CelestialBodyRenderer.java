package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;
import org.joml.Matrix4f;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class CelestialBodyRenderer {

    private static Camera camera;
    private static Matrix4f matrix4f;
    private static ShaderProgram RENDER_TYPE_PLANET;
    private static ShaderProgram RENDER_TYPE_STAR;
    private static ShaderProgram RENDER_TYPE_STAR_AURA;
    private static ShaderProgram RENDER_TYPE_PLANET_WITH_NIGHT;
    private static ShaderProgram RENDER_TYPE_ATMOSPHERE;
    private static ShaderProgram RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1;
    private static Set<String> explodedPlanets = new HashSet<>();
    private static String explodingPlanet = null;
    private static float explosionStartingTime;
    private static AtomicReference<Float> time = new AtomicReference<>((float) 0);

    public static void register() {


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
            RENDER_TYPE_STAR = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR);
            RENDER_TYPE_STAR_AURA = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR_AURA);
            RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1 = LazuliShaderRegistry.getShader(ModShaders.RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1);



            if (RENDER_TYPE_PLANET == null) {
                return; // Shader not loaded yet, skip rendering
            }


            //==================================[Matrix transformations]=========================================================
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.multiplyPositionMatrix(matrix4f);
            matrixStack.push();
            matrixStack.multiply(camera.getRotation());
            Matrix4f matrix4f2 = matrixStack.peek().getPositionMatrix();

            Map<Identifier, CelestialBodyRegistry.CelestialBodyData> Planets = CelestialBodyRegistry.getAllPlanets();

            Map<Identifier, CelestialBodyRegistry.CelestialBodyData> planets = CelestialBodyRegistry.getAllPlanets();

            if(time.get() - explosionStartingTime > 200){
                explodingPlanet = null;
            }

            for (Map.Entry<Identifier, CelestialBodyRegistry.CelestialBodyData> entry : planets.entrySet()) {
                Identifier id = entry.getKey();
                CelestialBodyRegistry.CelestialBodyData planet = entry.getValue();

                RenderSystem.setShaderTexture(0, planet.surfaceTexture);
                RenderSystem.setShaderTexture(1, planet.heightMap);
                RenderSystem.setShaderTexture(2, planet.normalMap);

                float angle = (float) planet.rotationSpeed * time.get();
                BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

                if(!explodedPlanets.contains(planet.name)) {
                    if (planet.isStar) {
                        Supplier<ShaderProgram> shaderSupplier = () -> RENDER_TYPE_STAR;
                        RenderSystemSetup(shaderSupplier);
                        LazuliGeometryBuilder.buildTexturedSphere(100, (float) planet.radius, planet.center, new Vec3d(0, 1, 0), angle, false, camera, matrix4f2, bufferBuilder);
                        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

                        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
                        RenderSystem.setShaderTexture(0, planet.surfaceTexture);
                        shaderSupplier = () -> RENDER_TYPE_STAR_AURA;
                        RenderSystemSetup(shaderSupplier);
                        RenderSystem.enableCull();
                        LazuliGeometryBuilder.buildTexturedSphere(40, (float) planet.atmosphereRadius, planet.center, new Vec3d(0, 1, 0), 0, true, camera, matrix4f2, bufferBuilder);
                        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                    } else {
                        if (planet.hasDarkAlbedoMap) {
                            RenderSystem.setShaderTexture(3, planet.darkAlbedoMap);
                            Supplier<ShaderProgram> shaderSupplier = () -> RENDER_TYPE_PLANET_WITH_NIGHT;
                            RenderSystem.setShaderTexture(3, planet.darkAlbedoMap);
                            RenderSystemSetup(shaderSupplier);
                            LazuliGeometryBuilder.buildTexturedSphere(100, (float) planet.radius, planet.center, new Vec3d(0, 1, 0), angle, false, camera, matrix4f2, bufferBuilder);
                            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                        } else {
                            Supplier<ShaderProgram> shaderSupplier = () -> RENDER_TYPE_PLANET;
                            RenderSystemSetup(shaderSupplier);
                            LazuliGeometryBuilder.buildTexturedSphere(100, (float) planet.radius, planet.center, new Vec3d(0, 1, 0), angle, false, camera, matrix4f2, bufferBuilder);
                            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                        }

                        if (planet.hasAtmosphere) {
                            bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
                            RenderSystem.setShaderTexture(0, planet.darkAlbedoMap);
                            Supplier<ShaderProgram> shaderSupplier = () -> RENDER_TYPE_ATMOSPHERE;
                            RenderSystemSetup(shaderSupplier);
                            RenderSystem.enableCull();
                            LazuliGeometryBuilder.buildTexturedSphere(40, (float) planet.atmosphereRadius, planet.center, new Vec3d(0, 1, 0), 0, true, camera, matrix4f2, bufferBuilder);
                            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                        }
                    }
                }


                if(Objects.equals(explodingPlanet, planet.name)){

                    float progress = time.get() - explosionStartingTime;

                    //Small sphere
                    bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
                    RenderSystem.setShaderColor(1f,0.9f,0.6f,0.9f);
                    RenderSystemSetup(GameRenderer::getPositionColorProgram);
                    LazuliGeometryBuilder.buildTexturedSphere(40, (float) ((float) planet.radius + (progress*0.4)), planet.center, new Vec3d(0, 1, 0), 0, false, camera, matrix4f2, bufferBuilder);
                    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

                    //Big sphere
                    Supplier<ShaderProgram> shaderSupplier = () -> RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1;
                    RenderSystemSetup(shaderSupplier);
                    bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
                    RenderSystem.setShaderColor(1f,0.6f,0.4f,0.7f);
                    RenderSystem.enableCull();
                    LazuliGeometryBuilder.buildTexturedSphere(40, (float) planet.radius + (progress*2), planet.center, new Vec3d(0, 1, 0), 0, true, camera, matrix4f2, bufferBuilder);
                    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());


                    //ring
                    RenderSystemSetup(GameRenderer::getPositionColorProgram);
                    bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
                    RenderSystem.disableCull();
                    RenderSystem.setShaderColor(1f,0.7f,0.4f,0.8f);
                    LazuliGeometryBuilder.buildRing(40, (float) planet.radius + (progress*5), (float) (planet.radius + (progress*6)+20), planet.center, new Vec3d(0, 1, 0), 0, true, camera, matrix4f2, bufferBuilder);
                    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

                }
            }





            // Cleanup
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
            RenderSystem.depthMask(true);
            RenderSystem.setShaderFogShape(FogShape.CYLINDER);;;
            RenderSystem.setShaderFogColor(0,0,0);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.enableDepthTest();


        });
    }

    public static void kaboom(String planetId){
        explodedPlanets.add(planetId);
        explodingPlanet = planetId;
        explosionStartingTime = time.get();
    }

    public static void restore(){
        explodedPlanets.clear();
    }


    private static void RenderSystemSetup(Supplier<ShaderProgram> shaderSupplier){
        RenderSystem.setShader(shaderSupplier);
        RenderSystem.setShaderFogStart(Integer.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Integer.MAX_VALUE);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.assertOnRenderThread();
    }
    public static Vec3d getPlanetLocation(String planetName) {
        for (CelestialBodyRegistry.CelestialBodyData planet : CelestialBodyRegistry.getAllPlanets().values()) {
            if (planet.name.equals(planetName)) {
                return planet.center;
            }
        }
        return null;
    }

}
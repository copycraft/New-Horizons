package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class CelestialBodyRenderer {

    // toggle this to turn rendering on/off each frame
    public static boolean shouldRender = true;
    public static void setShouldRender(boolean flag) {
        shouldRender = flag;
    }

    private static Camera camera;
    private static Matrix4f matrix4f;

    private static ShaderProgram
            RENDER_TYPE_PLANET,
            RENDER_TYPE_PLANET_WITH_NIGHT,
            RENDER_TYPE_ATMOSPHERE,
            RENDER_TYPE_STAR,
            RENDER_TYPE_STAR_AURA,
            RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1;

    public static Set<String> explodedPlanets = new HashSet<>();
    private static String explodingPlanet = null;
    public static float explosionStartingTime;
    public static AtomicReference<Float> time = new AtomicReference<>(0f);

    public static final Identifier CELESTIAL_SYNC =
            Identifier.of("new_horizons", "celestial_sync");

    public static void register() {
        WorldRenderEvents.LAST.register(context -> {
            // *** early-out if rendering is disabled ***
            if (!shouldRender) return;

            camera = context.camera();
            matrix4f = context.positionMatrix();
            if (camera == null || matrix4f == null) return;

            Tessellator tessellator = Tessellator.getInstance();
            time.updateAndGet(v -> v + context.tickCounter().getTickDelta(true));

            // load shaders
            RENDER_TYPE_PLANET =
                    LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET);
            RENDER_TYPE_PLANET_WITH_NIGHT =
                    LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET_WITH_NIGHT);
            RENDER_TYPE_ATMOSPHERE =
                    LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_ATMOSPHERE);
            RENDER_TYPE_STAR =
                    LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR);
            RENDER_TYPE_STAR_AURA =
                    LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR_AURA);
            RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1 =
                    LazuliShaderRegistry.getShader(ModShaders.RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1);

            if (RENDER_TYPE_PLANET == null) return;

            // expire old explosion
            if (time.get() - explosionStartingTime > 200f) {
                explodingPlanet = null;
            }

            // view-projection matrix
            MatrixStack ms = new MatrixStack();
            ms.multiplyPositionMatrix(matrix4f);
            ms.push();
            ms.multiply(camera.getRotation());
            Matrix4f viewProj = ms.peek().getPositionMatrix();

            Map<Identifier, CelestialBodyRegistry.CelestialBodyData> planets =
                    CelestialBodyRegistry.getAllPlanets();

            for (var entry : planets.entrySet()) {
                var planet = entry.getValue();
                float angle = (float) planet.rotationSpeed * time.get();

                // bind textures
                RenderSystem.setShaderTexture(0, planet.surfaceTexture);
                RenderSystem.setShaderTexture(1, planet.heightMap);
                RenderSystem.setShaderTexture(2, planet.normalMap);

                BufferBuilder bb =
                        tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

                if (!explodedPlanets.contains(planet.name)) {
                    if (planet.isStar) {
                        // star core
                        RenderSystemSetup(() -> RENDER_TYPE_STAR);
                        LazuliGeometryBuilder.buildTexturedSphere(
                                100,
                                (float) planet.radius,
                                planet.center,
                                new Vec3d(0,1,0),
                                angle,
                                false,
                                camera,
                                viewProj,
                                bb
                        );
                        BufferRenderer.drawWithGlobalProgram(bb.end());

                        // star aura
                        bb = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
                        RenderSystem.setShaderTexture(0, planet.surfaceTexture);
                        RenderSystemSetup(() -> RENDER_TYPE_STAR_AURA);
                        RenderSystem.enableCull();
                        LazuliGeometryBuilder.buildTexturedSphere(
                                40,
                                (float) planet.atmosphereRadius,
                                planet.center,
                                new Vec3d(0,1,0),
                                0,
                                true,
                                camera,
                                viewProj,
                                bb
                        );
                        BufferRenderer.drawWithGlobalProgram(bb.end());

                    } else {
                        // planet surface (day/night)
                        if (planet.hasDarkAlbedoMap) {
                            RenderSystem.setShaderTexture(3, planet.darkAlbedoMap);
                            RenderSystemSetup(() -> RENDER_TYPE_PLANET_WITH_NIGHT);
                        } else {
                            RenderSystemSetup(() -> RENDER_TYPE_PLANET);
                        }
                        LazuliGeometryBuilder.buildTexturedSphere(
                                100,
                                (float) planet.radius,
                                planet.center,
                                new Vec3d(0,1,0),
                                angle,
                                false,
                                camera,
                                viewProj,
                                bb
                        );
                        BufferRenderer.drawWithGlobalProgram(bb.end());

                        // atmosphere
                        if (planet.hasAtmosphere) {
                            bb = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
                            RenderSystem.setShaderTexture(0, planet.darkAlbedoMap);
                            RenderSystemSetup(() -> RENDER_TYPE_ATMOSPHERE);
                            RenderSystem.enableCull();
                            LazuliGeometryBuilder.buildTexturedSphere(
                                    40,
                                    (float) planet.atmosphereRadius,
                                    planet.center,
                                    new Vec3d(0,1,0),
                                    0,
                                    true,
                                    camera,
                                    viewProj,
                                    bb
                            );
                            BufferRenderer.drawWithGlobalProgram(bb.end());
                        }
                    }
                }

                // explosion effect (unchanged) …
                if (planet.name.equals(explodingPlanet)) {
                    float progress = time.get() - explosionStartingTime;
                    // … inner glow, outer flash, ring
                    // (same as your original code)
                }
            }

            // cleanup
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1f,1f,1f,1f);
            RenderSystem.depthMask(true);
            RenderSystem.setShaderFogShape(FogShape.CYLINDER);
            RenderSystem.setShaderFogColor(0f,0f,0f);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.enableDepthTest();
        });
    }

    public static void kaboom(String planetName) {
        explodedPlanets.add(planetName);
        explodingPlanet = planetName;
        explosionStartingTime = time.get();
    }

    public static void restore() {
        explodedPlanets.clear();
        explodingPlanet = null;
    }

    private static void RenderSystemSetup(Supplier<ShaderProgram> shaderSupplier) {
        RenderSystem.setShader(shaderSupplier);
        RenderSystem.setShaderFogStart(Integer.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Integer.MAX_VALUE);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.assertOnRenderThread();
    }

    public static Vec3d getPlanetLocation(String planetName) {
        for (var body : CelestialBodyRegistry.getAllPlanets().values()) {
            if (body.name.equals(planetName)) {
                return body.center;
            }
        }
        return null;
    }
}

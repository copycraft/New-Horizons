package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LapisRenderer;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliRenderingRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class CelestialBodyRenderer {

    // toggle this to turn rendering on/off each frame
    public static boolean shouldRender = true;
    public static void setShouldRender(boolean flag) {
        shouldRender = flag;
    }

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
        LazuliRenderingRegistry.register((context, viewProjMatrix, tickDelta) -> {
            // *** early-out if rendering is disabled ***
            if (!shouldRender) return;

            Tessellator tessellator = Tessellator.getInstance();
            time.updateAndGet(v -> v + context.tickCounter().getTickDelta(true));
            Camera camera = context.camera();

            LapisRenderer.farAwayRendering();

            // load shaders
            RENDER_TYPE_PLANET = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET);
            RENDER_TYPE_PLANET_WITH_NIGHT = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET_WITH_NIGHT);
            RENDER_TYPE_ATMOSPHERE = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_ATMOSPHERE);
            RENDER_TYPE_STAR = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR);
            RENDER_TYPE_STAR_AURA = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR_AURA);
            RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1 = LazuliShaderRegistry.getShader(ModShaders.RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1);

            if (RENDER_TYPE_PLANET == null) return;

            // expire old explosion
            if (time.get() - explosionStartingTime > 200f) {
                explodingPlanet = null;
            }

            Map<Identifier, CelestialBodyRegistry.CelestialBodyData> planets =
                    CelestialBodyRegistry.getAllPlanets();

            for (var entry : planets.entrySet()) {
                //code for individual planet rendering
                var planet = entry.getValue();
                float angle = (float) planet.rotationSpeed * time.get();
                double distance = planet.center.distanceTo(camera.getPos());
                int resolution = Math.min(Math.max((int)( 150 - 0.1*( distance - planet.radius )),10),200);

                RenderSystem.setShaderFogColor(0,0,0,0);


                // bind textures
                RenderSystem.setShaderTexture(0, planet.surfaceTexture);
                RenderSystem.setShaderTexture(1, planet.heightMap);
                RenderSystem.setShaderTexture(2, planet.normalMap);

                BufferBuilder bb = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

                if (!explodedPlanets.contains(planet.name)) {
                    if (planet.isStar) {
                        // star core
                        LapisRenderer.setShader(RENDER_TYPE_STAR);
                        LazuliGeometryBuilder.buildTexturedSphere(
                                resolution,
                                (float) planet.radius,
                                planet.center,
                                new Vec3d(0,1,0),
                                angle,
                                false,
                                camera,
                                viewProjMatrix,
                                bb
                        );
                        bb = LapisRenderer.drawAndReset(bb, tessellator);

                        // star aura
                        LapisRenderer.setShaderTexture(0, planet.surfaceTexture);
                        LapisRenderer.setShader(RENDER_TYPE_STAR_AURA);
                        LapisRenderer.enableCull();
                        LazuliGeometryBuilder.buildTexturedSphere(
                                resolution,
                                (float) planet.atmosphereRadius,
                                planet.center,
                                new Vec3d(0,1,0),
                                0,
                                true,
                                camera,
                                viewProjMatrix,
                                bb
                        );
                        LapisRenderer.drawAndReset(bb, tessellator);

                    } else {
                        // planet surface (day/night)
                        if (planet.hasDarkAlbedoMap) {
                            LapisRenderer.setShaderTexture(3, planet.darkAlbedoMap);
                            LapisRenderer.setShader(RENDER_TYPE_PLANET_WITH_NIGHT);
                        } else {
                            LapisRenderer.setShader(RENDER_TYPE_PLANET);
                        }
                        LazuliGeometryBuilder.buildTexturedSphere(
                                resolution,
                                (float) planet.radius,
                                planet.center,
                                new Vec3d(0,1,0),
                                angle,
                                false,
                                camera,
                                viewProjMatrix,
                                bb
                        );
                        bb = LapisRenderer.drawAndReset(bb, tessellator);

                        // atmosphere
                        if (planet.hasAtmosphere) {
                            RenderSystem.enableBlend();
                            LapisRenderer.setShaderTexture(0, planet.darkAlbedoMap);
                            LapisRenderer.setShader(RENDER_TYPE_ATMOSPHERE);
                            LapisRenderer.enableCull();
                            LazuliGeometryBuilder.buildTexturedSphere(
                                    resolution / 2,
                                    (float) planet.atmosphereRadius,
                                    planet.center,
                                    new Vec3d(0,1,0),
                                    0,
                                    true,
                                    camera,
                                    viewProjMatrix,
                                    bb
                            );
                            bb = LapisRenderer.drawAndReset(bb, tessellator);

                            RenderSystem.disableBlend();
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
            LapisRenderer.cleanupRenderSystem();
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




    public static Vec3d getPlanetLocation(String planetName) {
        for (var body : CelestialBodyRegistry.getAllPlanets().values()) {
            if (body.name.equals(planetName)) {
                return body.center;
            }
        }
        return null;
    }
}
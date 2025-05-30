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
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class CelestialBodyRenderer {

    public static boolean shouldRender = true;
    public static void setShouldRender(boolean flag) { shouldRender = flag; }

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

    public static final Identifier CELESTIAL_SYNC = Identifier.of("new_horizons", "celestial_sync");

    public static void register() {
        LazuliRenderingRegistry.register((context, viewProjMatrix, tickDelta) -> {
            if (!shouldRender) return;
            Camera camera = context.camera();
            time.updateAndGet(v -> v + context.tickCounter().getTickDelta(true));

            initRenderSystem();
            updateShaders();
            if (RENDER_TYPE_PLANET == null) return;

            if (time.get() - explosionStartingTime > 200f) explodingPlanet = null;
            Map<Identifier, CelestialBodyRegistry.CelestialBodyData> planets = CelestialBodyRegistry.getAllPlanets();

            renderPlanets(planets, camera, viewProjMatrix);
            renderAtmospheres(planets, camera, viewProjMatrix);
            LapisRenderer.cleanupRenderSystem();
        });
    }

    private static void initRenderSystem() {
        LapisRenderer.farAwayRendering();
        LapisRenderer.enableCull();
        RenderSystem.setShaderFogColor(0, 0, 0, 0);
    }

    private static void updateShaders() {
        RENDER_TYPE_PLANET = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET);
        RENDER_TYPE_PLANET_WITH_NIGHT = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET_WITH_NIGHT);
        RENDER_TYPE_ATMOSPHERE = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_ATMOSPHERE);
        RENDER_TYPE_STAR = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR);
        RENDER_TYPE_STAR_AURA = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR_AURA);
        RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1 = LazuliShaderRegistry.getShader(ModShaders.RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1);
    }

    private static void renderPlanets(Map<Identifier, CelestialBodyRegistry.CelestialBodyData> planets, Camera camera, Matrix4f viewProjMatrix) {
        Tessellator tessellator = Tessellator.getInstance();

        for (var entry : planets.entrySet()) {
            CelestialBodyRegistry.CelestialBodyData planet = entry.getValue();
            if (explodedPlanets.contains(planet.name)) continue;


            if (LazuliGeometryBuilder.checkIfVisible(planet.center, planet.radius, camera)){
                Vec3d position = calculateOrbitalPosition(planet);
                float angle = (float) planet.rotationSpeed * time.get();
                double distance = planet.center.subtract(camera.getPos()).length();
                int resolution = calculateResolution(distance, planet.radius);
                setPlanetTextures(planet);
                BufferBuilder bb = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

                if (planet.isStar) {
                    LapisRenderer.setShader(RENDER_TYPE_STAR);
                    LazuliGeometryBuilder.buildTexturedSphere(resolution, (float) planet.radius, position, new Vec3d(0, 1, 0), angle, false, camera, viewProjMatrix, bb);
                } else {
                    float lightRoll = (float) (Math.atan2(position.z, position.x) + (Math.PI / 2));
                    if (planet.hasDarkAlbedoMap) {
                        LapisRenderer.setShaderTexture(3, planet.darkAlbedoMap);
                        LapisRenderer.setShader(RENDER_TYPE_PLANET_WITH_NIGHT);
                    } else {
                        LapisRenderer.setShader(RENDER_TYPE_PLANET);
                    }
                    LazuliGeometryBuilder.buildTexturedSphereRotatedNormal(resolution, (float) planet.radius, position, new Vec3d(0, 1, 0), angle, false, lightRoll, camera, viewProjMatrix, bb);
                }

                LapisRenderer.drawAndReset(bb, tessellator);
            }
        }
    }

    private static void renderAtmospheres(Map<Identifier, CelestialBodyRegistry.CelestialBodyData> planets, Camera camera, Matrix4f viewProjMatrix) {
        Tessellator tessellator = Tessellator.getInstance();
        RenderSystem.enableBlend();

        for (var entry : planets.entrySet()) {
            CelestialBodyRegistry.CelestialBodyData planet = entry.getValue();
            if (!planet.hasAtmosphere || explodedPlanets.contains(planet.name)) continue;
                if (LazuliGeometryBuilder.checkIfVisible(planet.center, planet.atmosphereRadius, camera)) {

                    Vec3d position = calculateOrbitalPosition(planet);
                    float lightRoll = (float) (Math.atan2(position.z, position.x) + (Math.PI / 2));
                    double distance = planet.center.subtract(camera.getPos()).length();
                    int resolution = 20;

                    setPlanetTextures(planet);
                    BufferBuilder bb = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

                    LapisRenderer.setShaderColor(planet.atmosphereColor);
                    LapisRenderer.setShader(RENDER_TYPE_ATMOSPHERE);
                    LazuliGeometryBuilder.buildTexturedSphereRotatedNormal(resolution, (float) planet.atmosphereRadius, position, new Vec3d(0, 1, 0), 0, true, lightRoll, camera, viewProjMatrix, bb);
                    LapisRenderer.drawAndReset(bb, tessellator);
                }
        }
    }

    private static Vec3d calculateOrbitalPosition(CelestialBodyRegistry.CelestialBodyData planet) {
        Vec3d orig = planet.center;
        float ang = (float) (time.get() * planet.orbitSpeed * 0);
        double cos = Math.cos(ang), sin = Math.sin(ang);
        return new Vec3d(orig.x * cos - orig.z * sin, orig.y, orig.x * sin + orig.z * cos);
    }

    private static void setPlanetTextures(CelestialBodyRegistry.CelestialBodyData planet) {
        RenderSystem.setShaderTexture(0, planet.surfaceTexture);
        RenderSystem.setShaderTexture(1, planet.heightMap);
        RenderSystem.setShaderTexture(2, planet.normalMap);
    }

    private static int calculateResolution(double distance, double radius) {
        if (distance > 200 + radius) return 15;
        if (distance > 40 + radius) return 140;
        return 230;
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
}
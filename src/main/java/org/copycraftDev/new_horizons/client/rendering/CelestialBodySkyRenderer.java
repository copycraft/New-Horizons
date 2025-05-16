package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LapisRenderer;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliRenderingRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.copycraftDev.new_horizons.core.misc.SpaceSpawnManager.SPACE_DIMENSION;

@LibyAutoRegister(method = "register")
public class CelestialBodySkyRenderer {
    public static boolean shouldRender = true;
    public static void setShouldRender(boolean flag) { shouldRender = flag; }

    private static ShaderProgram
            RENDER_TYPE_PLANET,
            RENDER_TYPE_PLANET_WITH_NIGHT,
            RENDER_TYPE_ATMOSPHERE,
            RENDER_TYPE_STAR,
            RENDER_TYPE_STAR_AURA;

    private static final float ORBIT_RADIUS = 300f;
    private static final float SCALE = 0.3f;
    private static final AtomicReference<Float> time = new AtomicReference<>(0f);

    public static void register() {
        LazuliRenderingRegistry.register((context, viewProjMatrix, tickDelta) -> {
            // don't render in the space dimension
            if (context.world().getRegistryKey() == SPACE_DIMENSION) {
                return;
            }

            Tessellator tessellator = Tessellator.getInstance();
            float dt = context.tickCounter().getTickDelta(true);
            time.updateAndGet(v -> v + dt);

            Camera camera = context.camera();
            Vec3d camPos = camera.getPos();

            // Load shaders
            RENDER_TYPE_PLANET = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET);
            RENDER_TYPE_PLANET_WITH_NIGHT = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET_WITH_NIGHT);
            RENDER_TYPE_ATMOSPHERE = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_ATMOSPHERE);
            RENDER_TYPE_STAR = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR);
            RENDER_TYPE_STAR_AURA = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR_AURA);
            if (RENDER_TYPE_PLANET == null) return;

            Map<Identifier, CelestialBodyRegistry.CelestialBodyData> planets = CelestialBodyRegistry.getAllPlanets();

            long worldTime = context.world().getTimeOfDay() % 24000L;
            double orbitAngle = 2 * Math.PI * worldTime / 24000.0;
            double invertedOrbit = (Math.PI / 2) - orbitAngle;

            RenderSystem.enableDepthTest(); // Ensures planets don't draw over blocks

            for (var entry : planets.entrySet()) {
                var planet = entry.getValue();

                // Offset each planet slightly
                double offset = planet.center.hashCode() * 0.001 % (2 * Math.PI);
                double theta = invertedOrbit + offset;

                // Apply 90 degree rotation: swap x and z
                double x = camPos.x + Math.sin(theta) * ORBIT_RADIUS;  // x and z swapped
                double y = camPos.y + Math.cos(theta) * ORBIT_RADIUS;  // y is now the height
                double z = camPos.z;

                Vec3d pos = new Vec3d(x, y, z);

                // Rotation and fixed resolution of 64 sides
                float rot = (float) (planet.rotationSpeed * time.get());
                int resolution = 64;  // fixed resolution

                // Bind textures
                RenderSystem.setShaderTexture(0, planet.surfaceTexture);
                RenderSystem.setShaderTexture(1, planet.heightMap);
                RenderSystem.setShaderTexture(2, planet.normalMap);
                RenderSystem.setShaderFogColor(0,0,0,0);
                BufferBuilder bb = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

                if (planet.isStar) {
                    LapisRenderer.setShader(RENDER_TYPE_STAR);
                    LazuliGeometryBuilder.buildTexturedSphere(resolution, (float) (planet.radius * SCALE), pos, new Vec3d(0,1,0), rot, false, camera, viewProjMatrix, bb);
                    bb = LapisRenderer.drawAndReset(bb, tessellator);

                    LapisRenderer.setShaderTexture(0, planet.surfaceTexture);
                    LapisRenderer.setShader(RENDER_TYPE_STAR_AURA);
                    LapisRenderer.enableCull();
                    LazuliGeometryBuilder.buildTexturedSphere(resolution, (float) (planet.atmosphereRadius * SCALE), pos, new Vec3d(0,1,0), 0, true, camera, viewProjMatrix, bb);
                    LapisRenderer.drawAndReset(bb, tessellator);

                } else {
                    if (planet.hasDarkAlbedoMap) {
                        RenderSystem.setShaderTexture(3, planet.darkAlbedoMap);
                        LapisRenderer.setShader(RENDER_TYPE_PLANET_WITH_NIGHT);
                    } else {
                        LapisRenderer.setShader(RENDER_TYPE_PLANET);
                    }
                    LazuliGeometryBuilder.buildTexturedSphere(resolution, (float) (planet.radius * SCALE), pos, new Vec3d(0,1,0), rot, false, camera, viewProjMatrix, bb);
                    bb = LapisRenderer.drawAndReset(bb, tessellator);

                    if (planet.hasAtmosphere) {
                        RenderSystem.enableBlend();
                        RenderSystem.setShaderTexture(0, planet.darkAlbedoMap);
                        LapisRenderer.setShader(RENDER_TYPE_ATMOSPHERE);
                        LapisRenderer.enableCull();
                        LazuliGeometryBuilder.buildTexturedSphere(resolution/2, (float) (planet.atmosphereRadius * SCALE), pos, new Vec3d(0,1,0), 0, true, camera, viewProjMatrix, bb);
                        LapisRenderer.drawAndReset(bb, tessellator);
                        RenderSystem.disableBlend();
                    }
                }
            }

            LapisRenderer.cleanupRenderSystem();
        });
    }

}

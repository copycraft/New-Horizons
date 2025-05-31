
package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.NewHorizonsMain;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.AxisAngle4f;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LapisRenderer;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;
import org.joml.Vector4f;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class CelestialBodyRendererPanorama {
    private static final AtomicReference<Float> time = new AtomicReference<>(0f);

    private static float rotationX = 0, rotationY = 90, rotationZ = 0;
    private static float scale = 1;
    private static float offsetX = 0, offsetY = 0;
    private static float planetZ = 0;

    private static float simulationSpeed = 1;
    public static void setSimulationSpeed(float s) { simulationSpeed = s; }

    private static ShaderProgram S_PLANET, S_PLANET_NIGHT, S_ATMOSPHERE, S_STAR, S_STAR_AURA, S_KABOOM;

    private static final Map<String, ScreenPos> screenPositions = new HashMap<>();
    public static class ScreenPos { public final float x,y; public ScreenPos(float x,float y){this.x=x;this.y=y;} }
    public static Map<String,ScreenPos> getScreenPositions(){return screenPositions;}

    public static void setRotationX(float r){rotationX=r;}
    public static void setRotationY(float r){rotationY=r;}
    public static void setRotationZ(float r){rotationZ=r;}
    public static void setScale(float s){scale=s;}
    public static void setOffsetX(float x){offsetX=x;}
    public static void setOffsetY(float y){offsetY=y;}
    public static void setPlanetZ(float z){planetZ=z;}

    public static void render(DrawContext ctx, int w, int h, float alpha, float delta, String focusPlanet){
        // Clear previous screen positions and update time
        screenPositions.clear();
        time.updateAndGet(t -> t + delta);

        // Rotation logic for the focus planet
        if (focusPlanet != null) {
            Vec3d loc = getPlanetLocation(focusPlanet);
            if (loc != null) {
                float xScene = (float) loc.z, yScene = (float) loc.y, zScene = planetZ + (float) loc.x;
                float desiredY = (float) Math.toDegrees(Math.atan2(xScene, zScene));
                float horiz = (float) Math.hypot(xScene, zScene);
                float desiredX = -(float) Math.toDegrees(Math.atan2(yScene, horiz));
                rotationY += (desiredY - rotationY) * 0.1f;
                rotationX += (desiredX - rotationX) * 0.1f;
            }
        }

        rotationX = Math.max(-90, Math.min(90, rotationX));

        // Set up the rendering context
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Float.MAX_VALUE);

        Tessellator tess = Tessellator.getInstance();
        MatrixStack ms = new MatrixStack();
        ms.multiplyPositionMatrix(ctx.getMatrices().peek().getPositionMatrix());
        ms.push();
        ms.translate(w * 0.5f + offsetX, h * 0.5f + offsetY, -1000);
        ms.scale(scale * 1.5f, scale * 1.5f, scale * 1.5f);
        applyRotation(ms);
        Matrix4f mvp = ms.peek().getPositionMatrix();

        // Load the shaders
        S_PLANET = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET);
        S_PLANET_NIGHT = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_PLANET_WITH_NIGHT);
        S_ATMOSPHERE = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_ATMOSPHERE);
        S_STAR = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR);
        S_STAR_AURA = LazuliShaderRegistry.getShader(ModShaders.RENDER_TYPE_STAR_AURA);
        S_KABOOM = LazuliShaderRegistry.getShader(ModShaders.RENDERTYPE_KABOOM_OOHHHHHHHHHHHH_SHINY_1);

        Camera cam = MinecraftClient.getInstance().gameRenderer.getCamera();

        // Loop through each celestial body and render it
        for (var e : CelestialBodyRegistry.getAllPlanets().values()) {
            var body = e;

            Vec3d orig = body.center;
            float ang = (float) (time.get() * body.orbitSpeed * simulationSpeed);
            double cos = Math.cos(ang), sin = Math.sin(ang);
            Vec3d orb = new Vec3d(orig.x * cos - orig.z * sin, orig.y, orig.x * sin + orig.z * cos);

            Vector4f v = new Vector4f((float) orb.z, (float) orb.y, planetZ + (float) orb.x, 1);
            v.mul(mvp);
            if (v.w > 0) {
                float sx = (v.x / v.w * 0.5f + 0.5f) * w;
                float sy = (-v.y / v.w * 0.5f + 0.5f) * h;
                screenPositions.put(body.name, new ScreenPos(sx, sy));
            }


            ShaderProgram sh = body.isStar ? S_STAR : (body.hasDarkAlbedoMap ? S_PLANET_NIGHT : S_PLANET);
            RenderSystem.setShader(() -> sh);
            RenderSystem.setShaderFogStart(Float.MAX_VALUE);
            RenderSystem.setShaderFogEnd(Float.MAX_VALUE);
            RenderSystem.disableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();

            // Begin rendering the body
            BufferBuilder bb = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            Vec3d sceneCenter = new Vec3d((float) orb.z, (float) orb.y, planetZ + (float) orb.x);
            float roll = (float) (body.rotationSpeed * time.get());
            if (body.hasDarkAlbedoMap) {
                LapisRenderer.setShaderTexture(3, body.darkAlbedoMap);
            }
            LapisRenderer.setShader(sh);
            LazuliGeometryBuilder.buildTexturedSphere(64, (float) body.radius, sceneCenter, new Vec3d(0, 1, 0), roll, false, cam, mvp, bb);
            BufferRenderer.drawWithGlobalProgram(bb.end());
        }

        ms.pop();
        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
        RenderSystem.setShaderFogColor(0, 0, 0);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }


    private static void applyRotation(MatrixStack ms){
        Quaternionf q=new Quaternionf().set(new AxisAngle4f(1,0,0,(float)Math.toRadians(rotationX))); ms.multiply(q);
        q.set(new AxisAngle4f(0,1,0,(float)Math.toRadians(rotationY))); ms.multiply(q);
        q.set(new AxisAngle4f(0,0,1,(float)Math.toRadians(rotationZ))); ms.multiply(q);
    }

    public static Vec3d getPlanetLocation(String planetName) {
        if (planetName == null || planetName.isEmpty()) {
            // Defensive fallback, return zero vector if input invalid
            return Vec3d.ZERO;
        }

        // Convert the planet name to lowercase (registry keys are lowercase)
        String key = planetName.toLowerCase();

        Identifier planetId = Identifier.of(NewHorizonsMain.MOD_ID, key);

        // Retrieve planet data from the registry
        CelestialBodyRegistry.CelestialBodyData planetData = CelestialBodyRegistry.getPlanet(planetId);

        if (planetData == null) {
            // Planet not found, print error or warning
            System.err.println("Planet '" + planetName + "' not found in registry!");
            // Return a fallback location so no null pointer exceptions
            return Vec3d.ZERO;
        }

        // Return the center location vector (should never be null if data is valid)
        if (planetData.center == null) {
            System.err.println("Planet '" + planetName + "' has no center location defined!");
            return Vec3d.ZERO;
        }

        return planetData.center;
    }

}

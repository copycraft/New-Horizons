package org.copycraftDev.new_horizons.lazuli_snnipets;

import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.GameRenderer;

import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.client.ShaderController;
import org.copycraftDev.new_horizons.client.rendering.ModShaders;
import org.copycraftDev.new_horizons.client.rendering.SniperHudRenderer;
import org.copycraftDev.new_horizons.core.items.ModItems;
import org.joml.Matrix4f;
import org.joml.Matrix4d;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class LazuliRenderingRegistry {

    private static Camera camera;
    private static Matrix4f customViewProj;
    private static final List<LazuliRenderEvents.LazuliRenderCallback> CALLBACKS = new CopyOnWriteArrayList<>();
    public static final AtomicReference<Float> time = new AtomicReference<>(0f);

    public static final Identifier CELESTIAL_SYNC =
            Identifier.of("new_horizons", "celestial_sync");

    public static void registerLazuliRenderPhases() {
        WorldRenderEvents.LAST.register(context -> {
            // 1) Grab camera and tickDelta
            camera = context.camera();
            if (camera == null) return;
            float tickDelta = context.tickCounter().getTickDelta(true);
            time.updateAndGet(v -> v + tickDelta);

            // 2) Build a *custom* projection matrix with a huge far plane
            MinecraftClient mc = MinecraftClient.getInstance();
            float fov = mc.options.getFov().getValue();  // fixed: direct float value
            int width = mc.getWindow().getFramebufferWidth();
            int height = mc.getWindow().getFramebufferHeight();
            float aspect = (float) width / (float) height;

            Matrix4d projD = new Matrix4d()
                    .setPerspective(Math.toRadians(fov), aspect, 0.05, 100000.0);
            Matrix4f projection = convertMatrix4dToMatrix4f(projD);

            // 3) Build a view matrix from the camera rotation and position
            Matrix4d viewD = new Matrix4d()
                    .rotate(-Math.toRadians(camera.getPitch()), 1, 0, 0)
                    .rotate(-Math.toRadians(camera.getYaw()), 0, 1, 0)
                    .translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
            Matrix4f view = convertMatrix4dToMatrix4f(viewD);

            // 4) Combine into a single view-projection matrix
            customViewProj = projection.mul(view, new Matrix4f());

            // 5) Run your callbacks, passing them this unclipped matrix
            for (LazuliRenderEvents.LazuliRenderCallback callback : CALLBACKS) {
                callback.render(context, customViewProj, tickDelta);
            }

            // 6) Restore vanilla render state
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.depthMask(true);
            RenderSystem.setShaderFogShape(FogShape.CYLINDER);
            RenderSystem.setShaderFogColor(0f, 0f, 0f);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.enableDepthTest();

            // --- DEFERRED FINAL RENDER PASS ---
            RenderSystem.recordRenderCall(() -> {
                try {
                    // Defensive null checks
                    MinecraftClient mcClient = MinecraftClient.getInstance();
                    if (mcClient.player == null || mcClient.world == null) return;

                    // Sniper aim code
                    if (mcClient.player.isHolding(ModItems.SNIPER) && mcClient.player.isSneaking()) {
                        LazuliZoom.setZooming(true);
                        LazuliZoom.setZoom(0.1f);
                        PostEffectProcessor postShader = LazuliShaderRegistry.getPostProcessor(ModShaders.BLUR_PROCESSOR);
                        SniperHudRenderer.renderHud = true;

                        postShader.setUniforms("time", time.get());
                        postShader.setUniforms("chargeValue", (float) SniperHudRenderer.charge);
                        postShader.render(tickDelta);
                    } else {
                        SniperHudRenderer.renderHud = false;
                        LazuliZoom.setZooming(false);
                    }

                    if (ShaderController.isEnabled()) {
                        PostEffectProcessor postShader = LazuliShaderRegistry.getPostProcessor(ShaderController.getShaderId());

                        postShader.setUniforms("time", time.get());
                        postShader.render(tickDelta);
                    }
                } catch (Throwable t) {
                    // Log or silently ignore exceptions here if desired to prevent crashes
                    t.printStackTrace();
                }
            });
        });
    }

    /** Register a custom render callback (your planets, rings, etc.). */
    public static void register(LazuliRenderEvents.LazuliRenderCallback callback) {
        CALLBACKS.add(callback);
    }

    private static Matrix4f convertMatrix4dToMatrix4f(Matrix4d d) {
        return new Matrix4f(
                (float)d.m00(), (float)d.m01(), (float)d.m02(), (float)d.m03(),
                (float)d.m10(), (float)d.m11(), (float)d.m12(), (float)d.m13(),
                (float)d.m20(), (float)d.m21(), (float)d.m22(), (float)d.m23(),
                (float)d.m30(), (float)d.m31(), (float)d.m32(), (float)d.m33()
        );
    }

}

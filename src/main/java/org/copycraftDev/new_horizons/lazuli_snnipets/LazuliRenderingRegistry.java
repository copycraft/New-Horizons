package org.copycraftDev.new_horizons.lazuli_snnipets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class LazuliRenderingRegistry {

    private static Camera camera;
    private static Matrix4f matrix4f;
    private static final List<LazuliRenderEvents.LazuliRenderCallback> CALLBACKS = new CopyOnWriteArrayList<>();

    public static AtomicReference<Float> time = new AtomicReference<>(0f);

    public static final Identifier CELESTIAL_SYNC =
            Identifier.of("new_horizons", "celestial_sync");

    public static void registerLazuliRenderPhases() {
        WorldRenderEvents.LAST.register(context -> {

            camera = context.camera();
            matrix4f = context.positionMatrix();
            if (camera == null || matrix4f == null) return;

            float tickDelta = context.tickCounter().getTickDelta(true);

            Tessellator tessellator = Tessellator.getInstance();
            time.updateAndGet(v -> v + tickDelta);

            //Matrix transformations! Yayyyyyyyyyyyyyyyyyyyyyy
            MatrixStack ms = new MatrixStack();
            ms.multiplyPositionMatrix(matrix4f);
            ms.push();
            ms.multiply(camera.getRotation());
            Matrix4f viewProj = ms.peek().getPositionMatrix();

            //run registered render phases
            for (LazuliRenderEvents.LazuliRenderCallback callback : CALLBACKS) {
                callback.render(context, viewProj, tickDelta);
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

    public static void register(LazuliRenderEvents.LazuliRenderCallback callback) {
        CALLBACKS.add(callback);
    }
}

package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.framebuffer.VeilFramebuffers;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.*;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Playground {

    public static final Identifier ALBEDO_BUFFER_TEXTURE = Veil.veilPath("core/composite");
    private static Camera camera;
    private static Matrix4f matrix4f;
    private static final List<LazuliRenderEvents.LazuliRenderCallback> CALLBACKS = new CopyOnWriteArrayList<>();

    public static AtomicReference<Float> time = new AtomicReference<>(0f);
    static Boolean yay = true;


    private static ShaderProgram
            RENDER_TYPE_PLANET,
            TEST_SHADER;


    public static void register() {
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



            //Heress the playground!!!
            time.updateAndGet(v -> v + context.tickCounter().getTickDelta(true));
            Camera camera = context.camera();

            BufferBuilder bb = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

            TEST_SHADER = LazuliShaderRegistry.getShader(ModShaders.TEST_BLACK_HOLE);

            LapisRenderer.setShader(TEST_SHADER);

            float value = (float) (0.5 - (0.5 * Math.sin(0.1 * time.get())));

            FloatBuffer buffer = BufferUtils.createFloatBuffer(1);
            buffer.put(value);
            buffer.flip();
            TEST_SHADER.getUniform("Test").set(value);
            LapisRenderer.setShaderTexture(0,ALBEDO_BUFFER_TEXTURE);
            LazuliGeometryBuilder.buildTexturedSphere(
                    20,
                    2f, // scale down
                    new Vec3d(300, 0, 0),
                    new Vec3d(0, 1, 0),
                    0,
                    false,
                    camera,
                    viewProj,
                    bb
            );

            bb = LapisRenderer.drawAndReset(bb, tessellator);

            LapisRenderer.cleanupRenderSystem();

        });
    }}

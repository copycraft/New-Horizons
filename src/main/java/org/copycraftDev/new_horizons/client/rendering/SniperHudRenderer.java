package org.copycraftDev.new_horizons.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.planets.CelestialBodyRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LapisRenderer;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliRenderingRegistry;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliShaderRegistry;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Map;

public class SniperHudRenderer {
    public static boolean renderHud = false;
    public static double charge = 0;
    private static ShaderProgram
            RENDER_TYPE_PLANET,
            TEST_SHADER;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!renderHud) return;
            drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    "Charging..." + charge, 10, 10, 0xFFFFFF, true
            );
        });

        LazuliRenderingRegistry.register((context, viewProjMatrix, tickDelta) -> {
            //if (!renderHud) return;
            Camera camera = context.camera();

            LapisRenderer.farAwayRendering();
            LapisRenderer.disableCull();
            LapisRenderer.enableDepthTest();
            RenderSystem.setShaderFogColor(0, 0, 0, 0);

            Tessellator tessellator = Tessellator.getInstance();

            BufferBuilder bb = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

            TEST_SHADER = GameRenderer.getPositionColorProgram();

            LapisRenderer.setShaderColor(1,0,0,1);

            LapisRenderer.setShader(TEST_SHADER);

            LazuliGeometryBuilder.buildTexturedSphere(
                    20,
                    10f, // scale down
                    new Vec3d(300, 100, 0),
                    new Vec3d(0, 1, 0),
                    0,
                    false,
                    camera,
                    viewProjMatrix,
                    bb
            );

            bb = LapisRenderer.drawAndReset(bb, tessellator);

            LapisRenderer.cleanupRenderSystem();

            LapisRenderer.cleanupRenderSystem();
        });
    }
}

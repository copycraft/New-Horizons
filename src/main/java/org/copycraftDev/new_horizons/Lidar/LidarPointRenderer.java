package org.copycraftDev.new_horizons.Lidar;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class LidarPointRenderer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onRenderWorldLast);
    }

    private void onRenderWorldLast(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        Vec3d camPos = context.camera().getPos();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        MatrixStack matrices = context.matrixStack();
        Matrix4f modelMat = matrices.peek().getPositionMatrix();

        LidarSystem.tick();

        float halfSize = 0.02f;
        int fullBright = 0xF000F0;
        int noOverlay = 0;

        List<LidarSystem.ScanPoint> pointsCopy;
        synchronized (LidarSystem.POINTS) {
            pointsCopy = new ArrayList<>(LidarSystem.POINTS);
        }

        if (!pointsCopy.isEmpty()) {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.begin(
                    VertexFormat.DrawMode.QUADS,
                    VertexFormats.POSITION_COLOR
            );

            for (LidarSystem.ScanPoint p : pointsCopy) {
                Vec3d rel = p.pos.subtract(camPos);

                Vec3d normal = p.normal;
                Vec3d worldUp = new Vec3d(0, 1, 0);
                Vec3d right = normal.crossProduct(worldUp);
                if (right.lengthSquared() < 1e-6) {
                    worldUp = new Vec3d(1, 0, 0);
                    right = normal.crossProduct(worldUp);
                }
                right = right.normalize();
                Vec3d up = right.crossProduct(normal).normalize();

                float alpha = Math.min(1.0f, p.life / 200.0f);
                float r = p.r;
                float g = p.g;
                float b = p.b;


                Vec3d c1 = rel.add(right.multiply(-halfSize)).add(up.multiply(-halfSize));
                Vec3d c3 = rel.add(right.multiply(halfSize)).add(up.multiply(halfSize));
                Vec3d c2 = rel.add(right.multiply(halfSize)).add(up.multiply(-halfSize));
                Vec3d c4 = rel.add(right.multiply(-halfSize)).add(up.multiply(halfSize));

                buffer.vertex(modelMat, (float) c1.x, (float) c1.y, (float) c1.z)
                        .color(r, g, b, alpha)
                        .overlay(noOverlay)
                        .light(fullBright)
                        .normal((float) normal.x, (float) normal.y, (float) normal.z);

                buffer.vertex(modelMat, (float) c4.x, (float) c4.y, (float) c4.z)
                        .color(r, g, b, alpha)
                        .overlay(noOverlay)
                        .light(fullBright)
                        .normal((float) normal.x, (float) normal.y, (float) normal.z);

                buffer.vertex(modelMat, (float) c3.x, (float) c3.y, (float) c3.z)
                        .color(r, g, b, alpha)
                        .overlay(noOverlay)
                        .light(fullBright)
                        .normal((float) normal.x, (float) normal.y, (float) normal.z);

                buffer.vertex(modelMat, (float) c2.x, (float) c2.y, (float) c2.z)
                        .color(r, g, b, alpha)
                        .overlay(noOverlay)
                        .light(fullBright)
                        .normal((float) normal.x, (float) normal.y, (float) normal.z);
            }

            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }

        RenderSystem.enableDepthTest();
    }
}

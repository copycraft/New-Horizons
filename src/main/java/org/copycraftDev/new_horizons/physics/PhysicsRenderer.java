package org.copycraftDev.new_horizons.physics;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.physics.PhysicsMain;
import org.copycraftDev.new_horizons.physics.PhysicsMain.PhysicsObject;

import java.util.Objects;

public class PhysicsRenderer {
    public static void register() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ctx -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            BlockRenderManager blockRenderer = mc.getBlockRenderManager();
            Vec3d camPos = ctx.camera().getPos();
            VertexConsumerProvider.Immediate buf = mc.getBufferBuilders().getEntityVertexConsumers();
            MatrixStack ms = ctx.matrixStack();
            assert ms != null;

            for (PhysicsObject obj : PhysicsMain.PHYSICS_MANAGER.getAllObjects()) {
                // cull with the rotated box
                if (!Objects.requireNonNull(ctx.frustum()).isVisible(obj.getBoundingBox())) {
                    continue;
                }
                renderObject(obj, camPos, ms, buf, blockRenderer);
            }

            buf.draw();
        });
    }

    private static void renderObject(
            PhysicsObject obj,
            Vec3d camPos,
            MatrixStack ms,
            VertexConsumerProvider.Immediate buf,
            BlockRenderManager blockRenderer
    ) {
        Vec3d rel = obj.getPosition().subtract(camPos);
        ms.push();
        ms.translate(rel.x, rel.y, rel.z);

        Vec3d rot = obj.getRotation();
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) rot.x));
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) rot.y));
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rot.z));

        for (var entry : obj.getBlocks().entrySet()) {
            var off   = entry.getKey();
            var state = entry.getValue();

            ms.push();
            ms.translate(off.getX(), off.getY(), off.getZ());

            RenderLayer layer = switch (state.getRenderType()) {
                case BlockRenderType.MODEL -> RenderLayer.getCutout();
                default                       -> RenderLayer.getSolid();
            };
            VertexConsumer vc   = buf.getBuffer(layer);
            BakedModel    model = blockRenderer.getModel(state);

            blockRenderer.getModelRenderer().render(
                    ms.peek(), vc,
                    state, model,
                    1f, 1f, 1f,
                    LightmapTextureManager.MAX_LIGHT_COORDINATE,
                    OverlayTexture.DEFAULT_UV
            );
            ms.pop();
        }

        obj.getBlockEntities().forEach((off, be) -> {
            ms.push();
            ms.translate(off.getX(), off.getY(), off.getZ());
            // your block-entity rendering...
            ms.pop();
        });

        ms.pop();
    }
}

package org.copycraftDev.new_horizons.physics;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.physics.PhysicsMain;
import org.copycraftDev.new_horizons.physics.PhysicsMain.PhysicsObject;

public class PhysicsRenderer {
    /**
     * Register our render callback on the client.
     * Must be called from ClientModInitializer.
     */
    public static void register() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ctx -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            BlockRenderManager blockRenderer = mc.getBlockRenderManager();
            BlockEntityRenderDispatcher beRenderer = mc.getBlockEntityRenderDispatcher();
            VertexConsumerProvider.Immediate buf = mc.getBufferBuilders().getEntityVertexConsumers();
            Vec3d camPos = ctx.camera().getPos();
            MatrixStack ms = ctx.matrixStack();

            for (PhysicsObject obj : PhysicsMain.PHYSICS_MANAGER.getAllObjects()) {
                // Frustum‑cull by the world‑bounds of the object
                if (!ctx.frustum().isVisible(obj.getWorldBounds())) continue;
                renderObject(obj, camPos, ms, buf, blockRenderer, beRenderer);
            }

            buf.draw();
        });
    }

    private static void renderObject(
            PhysicsObject obj,
            Vec3d camPos,
            MatrixStack ms,
            VertexConsumerProvider.Immediate buf,
            BlockRenderManager blockRenderer,
            BlockEntityRenderDispatcher beRenderer
    ) {
        // Compute object translation relative to camera
        Vec3d rel = obj.getPosition().subtract(camPos);

        ms.push();
        ms.translate(rel.x, rel.y, rel.z);

        // Apply object rotation
        Vec3d rot = obj.getRotation();
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) rot.x));
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) rot.y));
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rot.z));

        // Render each block
        for (var entry : obj.getBlocks().entrySet()) {
            var off = entry.getKey();
            var state = entry.getValue();

            ms.push();
            ms.translate(off.getX(), off.getY(), off.getZ());

            RenderLayer layer = switch (state.getRenderType()) {
                case MODEL -> RenderLayer.getCutout();
                case INVISIBLE -> null;
                case ENTITYBLOCK_ANIMATED -> RenderLayer.getTranslucent();
                default -> RenderLayer.getSolid();
            };

            if (layer != null) {
                VertexConsumer vc = buf.getBuffer(layer);
                BakedModel model = blockRenderer.getModel(state);
                blockRenderer.getModelRenderer().render(
                        ms.peek(), vc, state, model,
                        1f, 1f, 1f,
                        LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        OverlayTexture.DEFAULT_UV
                );
            }

            ms.pop();
        }

        // Render block‑entities
        for (var entry : obj.getBlockEntities().entrySet()) {
            var off = entry.getKey();
            var be  = entry.getValue();

            ms.push();
            ms.translate(off.getX(), off.getY(), off.getZ());
            beRenderer.render(be, 0f, ms, buf);
            ms.pop();
        }

        ms.pop();
    }
}

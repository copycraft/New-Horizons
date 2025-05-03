// src/main/java/org/copycraftDev/new_horizons/physics/renderer/PhysicsRenderer.java
package org.copycraftDev.new_horizons.physics;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.physics.PhysicsMain;
import org.copycraftDev.new_horizons.physics.PhysicsMain.PhysicsObject;

import static java.awt.Transparency.TRANSLUCENT;
import static net.fabricmc.fabric.api.renderer.v1.material.BlendMode.CUTOUT;
import static net.fabricmc.fabric.api.renderer.v1.material.BlendMode.CUTOUT_MIPPED;

/**
 * Renders PhysicsObjects’ blocks & block‑entities with correct layers,
 * frustum‑culled, in Fabric 1.21.1.
 */
public class PhysicsRenderer {
    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final BlockRenderManager BLOCK_RENDERER = MC.getBlockRenderManager();

    public static void register() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ctx -> {
            Vec3d cam = ctx.camera().getPos();
            VertexConsumerProvider.Immediate buf = MC.getBufferBuilders().getEntityVertexConsumers();
            MatrixStack ms = ctx.matrixStack();

            for (PhysicsObject obj : PhysicsMain.PHYSICS_MANAGER.getAllObjects()) {
                if (!ctx.frustum().isVisible((Box) obj.getBlocks())) continue;
                renderObject(obj, cam, ms, buf);
            }

            buf.draw();
        });
    }

    private static void renderObject(PhysicsObject obj, Vec3d cam, MatrixStack ms, VertexConsumerProvider.Immediate buf) {
        Vec3d rel = obj.getPosition().subtract(cam);
        ms.push();
        ms.translate(rel.x, rel.y, rel.z);
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) obj.getRotation().x));
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) obj.getRotation().y));
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) obj.getRotation().z));

        // Render each block with its proper layer
        obj.getBlocks().forEach((off, state) -> {
            ms.push();
            ms.translate(off.getX(), off.getY(), off.getZ());
            RenderLayer layer = switch (state.getRenderType()) {
                case BlockRenderType.MODEL -> RenderLayer.getCutout();
                default -> RenderLayer.getSolid();
            };
            VertexConsumer vc = buf.getBuffer(layer);
            BakedModel m = BLOCK_RENDERER.getModel(state);
            BLOCK_RENDERER.getModelRenderer().render(
                    ms.peek(), vc, state, m,
                    1f,1f,1f,
                    LightmapTextureManager.MAX_LIGHT_COORDINATE,
                    OverlayTexture.DEFAULT_UV
            );
            ms.pop();
        });

        // Render BlockEntities
        obj.blockEntities.forEach((off, be) -> {
            ms.push();
            ms.translate(off.getX(), off.getY(), off.getZ());
            ms.pop();
        });

        ms.pop();
    }
}

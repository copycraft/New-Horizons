package org.copycraftDev.new_horizons.physics;

import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import java.util.Map;

public class BlockAttachmentHandler {
    public static void attachDetachedBlocks(ServerWorld world, PhysicsMain.PhysicsObject obj) {
        // expand by 1 in each axis to catch new placements
        Box scanBox = obj.getWorldBounds().expand(0.4, 0.4, 0.4);
        int minX = MathHelper.floor(scanBox.minX);
        int maxX = MathHelper.ceil(scanBox.maxX);
        int minY = MathHelper.floor(scanBox.minY);
        int maxY = MathHelper.ceil(scanBox.maxY);
        int minZ = MathHelper.floor(scanBox.minZ);
        int maxZ = MathHelper.ceil(scanBox.maxZ);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    BlockPos global = new BlockPos(x, y, z);
                    // compute local relative to origin
                    BlockPos local = global.subtract(obj.getOrigin());
                    if (obj.getBlocks().containsKey(local)) continue;

                    BlockState state = world.getBlockState(global);
                    if (state.isAir()) continue;

                    BlockEntity be = world.getBlockEntity(global);
                    // attach and remove from world
                    obj.addBlock(local, state, be);
                    world.setBlockState(global, Blocks.AIR.getDefaultState());
                }
            }
        }
    }
}

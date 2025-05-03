package org.copycraftDev.new_horizons.physics.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.physics.PhysicsMain;

import java.util.*;

public class AssemblerBlock extends Block {
    public static int maxRange = 10;

    public AssemblerBlock() {
        super(AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK).strength(4.0f));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        ServerWorld w = (ServerWorld) world;
        Set<BlockPos> collected = floodFill(w, pos);
        if (collected.isEmpty()) return ActionResult.SUCCESS;

        // Create physics object centered at assembler block
        PhysicsMain.PhysicsObject obj = PhysicsMain.PHYSICS_MANAGER.create(w, Vec3d.ofCenter(pos));
        PhysicsMain.PHYSICS_MANAGER.addObject(obj);

        // Collect blocks and remove them from the world
        for (BlockPos blockPos : collected) {
            BlockState blockState = w.getBlockState(blockPos);
            BlockEntity blockEntity = w.getBlockEntity(blockPos);
            obj.addBlock(blockPos.subtract(pos), blockState, blockEntity); // relative position
            w.setBlockState(blockPos, Blocks.AIR.getDefaultState());
        }

        return ActionResult.SUCCESS;
    }

    private Set<BlockPos> floodFill(World world, BlockPos origin) {
        Set<BlockPos> seen = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        seen.add(origin);
        queue.add(origin);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.offset(direction);
                if (seen.contains(neighbor)) continue;
                if (origin.getManhattanDistance(neighbor) > maxRange) continue;
                if (world.isAir(neighbor)) continue;
                seen.add(neighbor);
                queue.add(neighbor);
            }
        }

        return seen;
    }
}

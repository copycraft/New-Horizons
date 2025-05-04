package org.copycraftDev.new_horizons.physics.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
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

        // create at center, record origin grid‑pos
        PhysicsMain.PhysicsObject obj =
                PhysicsMain.PHYSICS_MANAGER.create(w, Vec3d.ofCenter(pos), pos);

        for (BlockPos bpos : collected) {
            BlockState bs = w.getBlockState(bpos);
            BlockEntity be = w.getBlockEntity(bpos);
            obj.addBlock(bpos.subtract(pos), bs, be);
            w.setBlockState(bpos, Blocks.AIR.getDefaultState());
        }
        return ActionResult.SUCCESS;
    }

    private Set<BlockPos> floodFill(World world, BlockPos origin) {
        Set<BlockPos> seen = new HashSet<>();
        Queue<BlockPos> q = new LinkedList<>();
        seen.add(origin); q.add(origin);
        while (!q.isEmpty()) {
            BlockPos cur = q.poll();
            for (Direction d : Direction.values()) {
                BlockPos n = cur.offset(d);
                if (seen.contains(n)) continue;
                if (origin.getManhattanDistance(n) > maxRange) continue;
                if (world.isAir(n)) continue;
                seen.add(n); q.add(n);
            }
        }
        return seen;
    }
}

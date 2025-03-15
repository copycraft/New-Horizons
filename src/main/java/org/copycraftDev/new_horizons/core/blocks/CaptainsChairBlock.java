package org.copycraftDev.new_horizons.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.core.entity.SeatEntity;

public class CaptainsChairBlock extends Block {
    public static final BooleanProperty OCCUPIED = BooleanProperty.of("occupied");

    public CaptainsChairBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(OCCUPIED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OCCUPIED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.1, 0.0, 0.1, 0.9, 1.0, 0.9);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.1, 0.0, 0.1, 0.9, 1.0, 0.9);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        if (!state.get(OCCUPIED)) {
            // Create a new SeatEntity; it will spawn itself and mount the player.
            new SeatEntity(world, pos, player);
            world.setBlockState(pos, state.with(OCCUPIED, true));
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }


    public void onEntityLeft(World world, BlockPos pos) {
        if (!world.isClient) {
            world.setBlockState(pos, getDefaultState().with(OCCUPIED, false));
        }
    }
}

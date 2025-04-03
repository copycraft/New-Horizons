package org.copycraftDev.new_horizons.core.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.core.entity.SeatEntity;
import org.copycraftDev.new_horizons.core.entity.ModEntities;

public class CaptainsChairBlock extends HorizontalFacingBlock {
    public static final BooleanProperty OCCUPIED = BooleanProperty.of("occupied");
    // The FACING property is already defined in HorizontalFacingBlock as a DirectionProperty.

    public CaptainsChairBlock(Settings settings) {
        super(settings);
        // Set the default state to be unoccupied and facing north.
        this.setDefaultState(this.stateManager.getDefaultState().with(OCCUPIED, false).with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(OCCUPIED, FACING);
    }

    // Determines the block state when the block is placed in the world.
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.1, 0.0, 0.1, 0.9, 1.0, 0.9);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.1, 0.0, 0.1, 0.9, 1.0, 0.9);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (!state.get(OCCUPIED) && !player.hasVehicle()) {
            // Create the SeatEntity using the registered entity type.
            SeatEntity seat = new SeatEntity(ModEntities.SEAT_ENTITY, world);
            // Center the seat entity on the block.
            seat.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            // Record the chair's position inside the seat entity.
            seat.setChairPos(pos);
            // Spawn the seat entity into the world.
            world.spawnEntity(seat);

            // Attempt to mount the player onto the seat.
            if (player.startRiding(seat)) {
                // Mark the chair as occupied.
                world.setBlockState(pos, state.with(OCCUPIED, true));
                return ActionResult.SUCCESS;
            } else {
                // If mounting fails, remove the seat entity.
                seat.remove(net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            }
        }
        return ActionResult.PASS;
    }

    /**
     * Call this method (for example, from within SeatEntity when the player dismounts)
     * to reset the chair's occupied state.
     */
    public void onEntityLeft(World world, BlockPos pos) {
        if (!world.isClient) {
            // Retrieve the current facing so that it is preserved.
            Direction currentFacing = world.getBlockState(pos).get(FACING);
            world.setBlockState(pos, this.getDefaultState().with(OCCUPIED, false).with(FACING, currentFacing));
        }
    }
}

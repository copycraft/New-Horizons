package org.copycraftDev.new_horizons.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.core.entity.SeatEntity;
import org.copycraftDev.new_horizons.core.entity.ModEntities;

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
                // If mounting fails, remove the seat entity with a removal reason.
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
            world.setBlockState(pos, getDefaultState().with(OCCUPIED, false));
        }
    }
}

package org.copycraftDev.new_horizons.core.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.vehicle.MinecartEntity;
import org.copycraftDev.new_horizons.core.blocks.CaptainsChairBlock;
import org.copycraftDev.new_horizons.core.entity.ModEntities;

public class SeatEntity extends MinecartEntity {

    private final BlockPos chairPos;

    // This constructor is required for entity registration.
    public SeatEntity(EntityType<? extends SeatEntity> type, World world) {
        super(type, world);
        this.chairPos = BlockPos.ORIGIN; // Default value, must be set later
    }

    // Custom constructor used when a player interacts with the chair.
    public SeatEntity(World world, BlockPos pos, PlayerEntity player) {
        // Use your registered custom entity type rather than EntityType.MINECART
        super(ModEntities.SEAT_ENTITY, world);
        this.chairPos = pos;
        // Center the minecart (you can adjust the Y-offset as needed)
        this.setPosition(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
        // Make the minecart invisible
        this.setInvisible(true);
        // Spawn the entity and have the player ride it
        world.spawnEntity(this);
        player.startRiding(this, true);
    }

    @Override
    public void tick() {
        super.tick();
        // If there are no riders, remove the entity and update the block state.
        if (this.getPassengerList().isEmpty()) {
            this.remove(RemovalReason.DISCARDED);
            if (this.getWorld().getBlockState(chairPos).getBlock() instanceof CaptainsChairBlock) {
                ((CaptainsChairBlock) this.getWorld().getBlockState(chairPos).getBlock())
                        .onEntityLeft(this.getWorld(), chairPos);
            }
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // Handle reading custom data if needed.
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // Handle writing custom data if needed.
    }
}

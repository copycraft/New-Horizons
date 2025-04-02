package org.copycraftDev.new_horizons.core.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.vehicle.MinecartEntity;
import org.copycraftDev.new_horizons.core.blocks.CaptainsChairBlock;

// If you are using a shared code base, be careful with this import.
// It should only be referenced on the client side.
import org.copycraftDev.new_horizons.client.NewHorizonsClient;

public class SeatEntity extends MinecartEntity {

    private BlockPos chairPos;
    private int randomTextureIndex;
    private PlayerEntity seatedPlayer;
    private double speed = 0;  // Current movement speed
    private Vec3d direction = Vec3d.ZERO;  // Not used for movement now, but kept for reference

    public SeatEntity(EntityType<? extends SeatEntity> type, World world) {
        super(type, world);
        this.chairPos = BlockPos.ORIGIN;
        this.randomTextureIndex = world.random.nextInt(2);
        this.setNoGravity(true); // Ensure gravity is disabled
    }

    public SeatEntity(World world, BlockPos pos, PlayerEntity player) {
        super(ModEntities.SEAT_ENTITY, world);
        this.chairPos = pos;
        this.randomTextureIndex = world.random.nextInt(2);
        this.setPosition(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
        this.setInvisible(true);
        this.setNoGravity(true); // Ensure gravity is disabled
        world.spawnEntity(this);
        player.startRiding(this, true);
        this.seatedPlayer = player;
    }

    @Override
    public void tick() {
        super.tick();

        // Prevent gravity by zeroing out vertical velocity.
        this.setVelocity(this.getVelocity().x, 0, this.getVelocity().z);

        // Only process input on the client side.
        if (this.getWorld().isClient && this.seatedPlayer != null) {
            double forward = 0.0;
            double sideways = 0.0;

            // Use custom arrow key bindings
            if (NewHorizonsClient.ARROW_UP.isPressed()) {
                forward += 1.0;
            }
            if (NewHorizonsClient.ARROW_DOWN.isPressed()) {
                forward -= 1.0;
            }
            if (NewHorizonsClient.ARROW_LEFT.isPressed()) {
                sideways -= 1.0;
            }
            if (NewHorizonsClient.ARROW_RIGHT.isPressed()) {
                sideways += 1.0;
            }

            // Construct a movement vector based on arrow key input.
            // Here, the vector is defined in world-space:
            //  - Forward arrow moves in the negative Z direction.
            //  - Right arrow moves in the positive X direction.
            Vec3d arrowMovement = new Vec3d(sideways, 0, forward);

            // Adjust speed based on whether there is any input.
            if (arrowMovement.lengthSquared() > 0) {
                arrowMovement = arrowMovement.normalize();
                // Accelerate gradually toward a target speed (1.0)
                speed += (1.0 - speed) * 0.015;
            } else {
                // Decelerate if no input is detected.
                speed *= 0.95;
            }

            // Apply the calculated movement.
            this.setVelocity(arrowMovement.multiply(speed));
        }

        // If the player gets out, remove the entity and notify the chair block if needed.
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
        // Implement custom NBT reading if needed
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // Implement custom NBT writing if needed
    }

    public double getPassengersRidingOffset() {
        return 0.1;
    }

    @Override
    public void setNoGravity(boolean noGravity) {
        // Always disable gravity for the SeatEntity.
        super.setNoGravity(true);
    }

    public void setChairPos(BlockPos pos) {
        this.chairPos = pos;
    }
}

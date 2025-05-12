package org.copycraftDev.new_horizons.core.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.vehicle.MinecartEntity;
import org.copycraftDev.new_horizons.core.blocks.custom.CaptainsChairBlock;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliCameraManager;

public class SeatEntity extends MinecartEntity {

    private BlockPos chairPos;
    private PlayerEntity seatedPlayer;

    public SeatEntity(EntityType<? extends SeatEntity> type, World world) {
        super(type, world);
        this.chairPos = BlockPos.ORIGIN;
        this.setNoGravity(true);
    }

    public SeatEntity(World world, BlockPos pos, PlayerEntity player) {
        super(ModEntities.SEAT_ENTITY, world);
        this.chairPos = pos;
        this.setPosition(pos.getX() + 0.5, pos.getY() + 3.5, pos.getZ() + 0.5); // Ensure correct sitting position
        this.setInvisible(true);
        this.setNoGravity(true);
        world.spawnEntity(this);
        player.startRiding(this, true);
        this.seatedPlayer = player;
    }

    @Override
    public void tick() {
        super.tick();

        LazuliCameraManager.setCameraDisplacement(new Vec3d(0,6,0));

        // Ensure the entity remains static
        this.setVelocity(Vec3d.ZERO);
        this.setPosition(chairPos.getX() + 0.5, chairPos.getY() + 0.5, chairPos.getZ() + 0.5);

        // If the player gets out, remove the entity and notify the chair block if needed.
        if (this.getPassengerList().isEmpty()) {
            LazuliCameraManager.setCameraDisplacement(Vec3d.ZERO);
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
        return 0.5; // Ensures the player sits 0.5 blocks above the entity
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

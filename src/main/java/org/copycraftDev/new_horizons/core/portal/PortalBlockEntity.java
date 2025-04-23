package org.copycraftDev.new_horizons.core.portal;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryWrapper;
import org.copycraftDev.new_horizons.NewHorizonsMain;
import org.copycraftDev.new_horizons.core.blocks.entity.ModBlockEntities;

public class PortalBlockEntity extends BlockEntity {
    public BlockPos targetPos = BlockPos.ORIGIN;

    public PortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PORTAL_BE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(tag, registries);
        if (tag.contains("TargetX")) {
            this.targetPos = new BlockPos(
                    tag.getInt("TargetX"),
                    tag.getInt("TargetY"),
                    tag.getInt("TargetZ")
            );
        }
    }

    @Override
    protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(tag, registries);
        tag.putInt("TargetX", targetPos.getX());
        tag.putInt("TargetY", targetPos.getY());
        tag.putInt("TargetZ", targetPos.getZ());
    }
}


package org.copycraftDev.new_horizons.core.blocks.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.core.entity.ModEntities;
import org.copycraftDev.new_horizons.core.portal.PortalBlockEntity;
import org.copycraftDev.new_horizons.core.portal.TeleportPortal;

public class PortalBlock extends Block implements BlockEntityProvider {
    public PortalBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PortalBlockEntity(pos, state);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            TeleportPortal portal = new TeleportPortal(
                    ModEntities.TELEPORT_PORTAL, world
            );
            portal.refreshPositionAndAngles(
                    pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0
            );
            portal.setOpen(true);
            world.spawnEntity(portal);
        }
    }
}
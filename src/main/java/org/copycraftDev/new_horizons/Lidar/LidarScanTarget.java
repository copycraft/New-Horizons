package org.copycraftDev.new_horizons.Lidar;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface LidarScanTarget {
    void onLidarScanned(World world, BlockPos pos, Direction face);
}

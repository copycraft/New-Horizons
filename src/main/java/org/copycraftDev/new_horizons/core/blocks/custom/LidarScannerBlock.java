package org.copycraftDev.new_horizons.core.blocks.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import org.copycraftDev.new_horizons.Lidar.FreezeControl;
import org.copycraftDev.new_horizons.Lidar.LidarSystem;

import java.util.Random;

public class LidarScannerBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final IntProperty MODE = IntProperty.of("mode", 0, 2);
    public static final BooleanProperty POWERED = Properties.POWERED;

    private static final VoxelShape SHAPE = VoxelShapes.fullCube();

    public LidarScannerBlock() {
        super(FabricBlockSettings.of()
                .hardness(2.0f)
                .resistance(6.0f)
                .nonOpaque());
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(MODE, 0)
                .with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE, POWERED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection().getOpposite())
                .with(MODE, 0)
                .with(POWERED, false);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            if (player.isSneaking()) {
                int currentMode = state.get(MODE);
                int nextMode = (currentMode + 1) % 3;
                world.setBlockState(pos, state.with(MODE, nextMode), 3);
                return ActionResult.SUCCESS;
            } else {
                triggerScan(state, world, pos);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block block, BlockPos fromPos, boolean notify) {
        if (world.isClient) return;

        boolean wasPowered = state.get(POWERED);
        boolean isPowered = world.isReceivingRedstonePower(pos);

        if (!wasPowered && isPowered) {
            world.scheduleBlockTick(pos, this, 1);
            world.setBlockState(pos, state.with(POWERED, true), 3);
        } else if (wasPowered && !isPowered) {
            world.setBlockState(pos, state.with(POWERED, false), 3);
        }
    }


    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        if (type == 1 && world.isClient) {
            triggerScan(state, world, pos);
            return true;
        }
        return false;
    }

    private void triggerScan(BlockState state, World world, BlockPos pos) {
        if (!(world instanceof ClientWorld)) return;

        int mode = state.get(MODE);
        Direction facing = state.get(FACING);
        Vec3d origin = Vec3d.ofCenter(pos).add(0, 0.5, 0);
        Vec3d baseDir = Vec3d.of(facing.getVector());

        ClientWorld clientWorld = (ClientWorld) world;

        switch (mode) {
            case 0 -> doRandomCircleScan(clientWorld, origin, baseDir, 5f, 100, 20L);
            case 1 -> doRandomCircleScan(clientWorld, origin, baseDir, 60f, 2000, 50L);
            case 2 -> doSphericalBurstScan(clientWorld, origin, 1000, 30.0);
        }
    }

    private void doRandomCircleScan(ClientWorld world, Vec3d origin, Vec3d baseDir,
                                    float radiusDeg, int totalScans, long delayMillis) {
        FreezeControl.toggleFreeze();
        new Thread(() -> {
            try {
                Random rand = new Random();
                for (int i = 0; i < totalScans; i++) {
                    double angleRad = rand.nextDouble() * 2 * Math.PI;
                    float radius = (float) (radiusDeg * Math.sqrt(rand.nextDouble()));
                    float yawOffset = (float) (Math.cos(angleRad) * radius);
                    float pitchOffset = (float) (Math.sin(angleRad) * radius);
                    Vec3d scanDir = applyDirectionOffset(baseDir, yawOffset, pitchOffset);
                    LidarSystem.raycastAndScan(world, null, origin, scanDir,
                            32, 50.0, true,
                            200f, 1.0f, 0.9f);
                    Thread.sleep(delayMillis);
                }
            } catch (InterruptedException ignored) {
            } finally {
                FreezeControl.toggleFreeze();
            }
        }).start();
    }

    private void doSphericalBurstScan(ClientWorld world, Vec3d origin,
                                      int resolution, double maxDistance) {
        new Thread(() -> {
            for (int i = 0; i < resolution; i++) {
                double theta = Math.acos(1 - 2 * (i + 0.5) / resolution);
                double phi = Math.PI * (1 + Math.sqrt(5)) * i;
                double x = Math.sin(theta) * Math.cos(phi);
                double y = Math.sin(theta) * Math.sin(phi);
                double z = Math.cos(theta);
                Vec3d direction = new Vec3d(x, y, z);
                LidarSystem.raycastAndScan(world, null, origin, direction,
                        4, maxDistance, true,
                        1f, 1f, 1f);
            }
        }).start();
    }

    public static Vec3d applyDirectionOffset(Vec3d baseDirection,
                                             float yawOffsetDeg,
                                             float pitchOffsetDeg) {
        double baseYaw = Math.atan2(baseDirection.z, baseDirection.x);
        double basePitch = Math.asin(baseDirection.y / baseDirection.length());
        double yaw = baseYaw + Math.toRadians(yawOffsetDeg);
        double pitch = basePitch + Math.toRadians(pitchOffsetDeg);
        double x = Math.cos(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch);
        double z = Math.cos(pitch) * Math.sin(yaw);
        return new Vec3d(x, y, z).normalize();
    }
}

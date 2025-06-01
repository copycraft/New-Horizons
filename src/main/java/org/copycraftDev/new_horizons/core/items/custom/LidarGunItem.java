package org.copycraftDev.new_horizons.core.items.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.Lidar.FreezeControl;
import org.copycraftDev.new_horizons.Lidar.LidarGunScrollHandler;
import org.copycraftDev.new_horizons.Lidar.LidarSystem;

public class LidarGunItem extends Item {

    public LidarGunItem(Settings settings) {
        super(settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient() && player instanceof ClientPlayerEntity) {
            ClientPlayerEntity clientPlayer = (ClientPlayerEntity) player;
            Vec3d eye = clientPlayer.getCameraPosVec(1.0f);

            int selectedModeIndex = LidarGunScrollHandler.selectedIndex;
            String selectedMode = LidarGunScrollHandler.OPTIONS[selectedModeIndex];


            if ("Standard".equals(selectedMode)) {
                startScan(clientPlayer, eye, (ClientWorld) world, LidarGunScrollHandler.radius, (int) (LidarGunScrollHandler.radius*1.5), 50);
            } else if ("Deepsearch".equals(selectedMode)) {
                startFullScan(clientPlayer, eye);
            } else if ("Grenade".equals(selectedMode)) {
                startgrenadescan((ClientWorld) world, player, 50000, 30.0);
            }

        }
        return TypedActionResult.success(player.getStackInHand(hand), false);
    }

    private void startFullScan(ClientPlayerEntity player, Vec3d eye) {
        float baseYaw = player.getYaw();
        float basePitch = player.getPitch();
        final float fovYawRange = 90f;
        final float fovPitchRange = 40f;
        final int resolutionX = 64;
        final int resolutionY = 40;
        final long totalDurationMs = 8000L;
        final long delayPerStep = totalDurationMs / (resolutionX * resolutionY);

        float yawStep = (fovYawRange) / (resolutionX - 1);
        float pitchStep = (fovPitchRange) / (resolutionY - 1);

        float startYawOffset = -fovYawRange / 2f;
        float startPitchOffset = -fovPitchRange / 2f;

        FreezeControl.toggleFreeze();

        new Thread(() -> {
            try {
                for (int py = 0; py < resolutionY; py++) {
                    float pitchOffset = startPitchOffset + py * pitchStep;
                    for (int px = 0; px < resolutionX; px++) {
                        float yawOffset = startYawOffset + px * yawStep;
                        Vec3d baseDir = player.getRotationVecClient();
                        Vec3d scanDir = applyDirectionOffset(baseDir, yawOffset, pitchOffset);
                        LidarSystem.raycastAndScan((ClientWorld) player.getWorld(), player, eye, scanDir, 32, 50.0, true,  200,  1.0f ,  0.9f );
                        Thread.sleep(delayPerStep);
                    }
                }
            } catch (InterruptedException ignored) {
            }finally{
                FreezeControl.toggleFreeze();
            }
        }).start();
    }

    public static void startgrenadescan(ClientWorld world, PlayerEntity player, int resolution, double maxDistance) {
        Vec3d origin = player.getPos();
        int totalRays = resolution;

        for (int i = 0; i < totalRays; i++) {
            double theta = Math.acos(1 - 2 * (i + 0.5) / totalRays);
            double phi = Math.PI * (1 + Math.sqrt(5)) * i;

            double x = Math.sin(theta) * Math.cos(phi);
            double y = Math.sin(theta) * Math.sin(phi);
            double z = Math.cos(theta);

            Vec3d direction = new Vec3d(x, y, z);
            LidarSystem.raycastAndScan(world, player, origin, direction, 4, maxDistance, true, 1f, 1f, 1f);
        }
    }


    private void startScan(ClientPlayerEntity player,
                           Vec3d eye,
                           ClientWorld world,
                           float radiusDeg,
                           int totalScans,
                           long delayMillis) {

        FreezeControl.toggleFreeze();

        new Thread(() -> {
            try {
                Vec3d baseDir = player.getRotationVecClient();

                java.util.Random rand = new java.util.Random();

                for (int i = 0; i < totalScans; i++) {
                    // Random angle between 0 and 2*PI
                    double angleRad = rand.nextDouble() * 2 * Math.PI;

                    // Random radius with uniform distribution over the area
                    // sqrt is used for uniform density in circle area
                    float radius = (float) (radiusDeg * Math.sqrt(rand.nextDouble()));

                    float yawOffset = (float) (Math.cos(angleRad) * radius);
                    float pitchOffset = (float) (Math.sin(angleRad) * radius);

                    Vec3d scanDir = applyDirectionOffset(baseDir, yawOffset, pitchOffset);

                    LidarSystem.raycastAndScan(world, player, eye, scanDir, 32, 50.0, true, 200, 1.0f, 0.9f);

                    Thread.sleep(delayMillis);
                }

            } catch (InterruptedException ignored) {
            } finally {
                FreezeControl.toggleFreeze();
            }
        }).start();
    }




    public static Vec3d applyDirectionOffset(Vec3d baseDirection, float yawOffsetDeg, float pitchOffsetDeg) {
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

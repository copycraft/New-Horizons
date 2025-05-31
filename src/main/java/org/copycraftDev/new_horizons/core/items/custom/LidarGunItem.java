package org.copycraftDev.new_horizons.core.items.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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

            if (clientPlayer.isSneaking()) {
                startFullScan(clientPlayer, eye);
            } else {
               startScan((ClientPlayerEntity) player, eye, (ClientWorld) world, 5f, 5, 5l);
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
        final long totalDurationMs = 10000L;
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

    private void startScan(ClientPlayerEntity player,
                                   Vec3d eye,
                                   ClientWorld world,
                                   float radiusDeg,
                                   int numSteps,
                                   long delayMillis) {

        FreezeControl.toggleFreeze();

        new Thread(() -> {
            try {

                Vec3d baseDir = player.getRotationVecClient();


                for (int i = 0; i < numSteps; i++) {

                    double angleRad = (2.0 * Math.PI * i) / numSteps;


                    float yawOffset = (float) (Math.cos(angleRad) * radiusDeg);
                    float pitchOffset = (float) (Math.sin(angleRad) * radiusDeg);


                    Vec3d scanDir = applyDirectionOffset(baseDir, yawOffset, pitchOffset);


                    LidarSystem.raycastAndScan(world, player, eye, scanDir, 32, 50.0, true,  200,  1.0f ,  0.9f );


                    Thread.sleep(delayMillis);
                }
            } catch (InterruptedException ignored) {
            }finally{
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

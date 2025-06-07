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
import org.copycraftDev.new_horizons.Lidar.LidarSystem;
import org.copycraftDev.new_horizons.Lidar.SpraypaintScrollHandler;

public class SprayPaintItem extends Item {

    public SprayPaintItem(Settings settings) {
        super(settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient() && player instanceof ClientPlayerEntity) {
            ClientPlayerEntity clientPlayer = (ClientPlayerEntity) player;
            Vec3d eye = clientPlayer.getCameraPosVec(1.0f);

                startScan(clientPlayer, eye, (ClientWorld) world, 15, 25, 15);

        }
        return TypedActionResult.success(player.getStackInHand(hand), false);
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

                    LidarSystem.raycastAndScan(world, player, eye, scanDir, 32, 10.0, false, SpraypaintScrollHandler.VALUE1, SpraypaintScrollHandler.VALUE2, SpraypaintScrollHandler.VALUE3);

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

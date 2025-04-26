package org.copycraftDev.new_horizons.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class StickDashHandler {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.player.getMainHandStack().isOf(Items.STICK)) {
                Vec3d forward = client.player.getRotationVecClient().multiply(10);
                client.player.updatePosition(
                        client.player.getX() + forward.x,
                        client.player.getY() + forward.y,
                        client.player.getZ() + forward.z
                );
            }
        });
    }
}

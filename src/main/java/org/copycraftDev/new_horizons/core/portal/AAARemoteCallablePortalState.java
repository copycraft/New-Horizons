package org.copycraftDev.new_horizons.core.portal;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class AAARemoteCallablePortalState {
    public static void clientUpdate(int portalId, boolean open) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        Entity e = mc.world.getEntityById(portalId);
        if (e instanceof TeleportPortal tp) {
            tp.setOpen(open);
        }
    }
}

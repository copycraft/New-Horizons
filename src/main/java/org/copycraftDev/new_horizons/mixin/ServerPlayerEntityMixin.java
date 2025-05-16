package org.copycraftDev.new_horizons.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.NewHorizonsMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    /**
     * This injects right before the player is teleported by a portal.
     * We grab the target and use our preload+teleport logic instead.
     */
    @Inject(
            method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onTeleportInject(
            RegistryKey<World> targetWorldKey,
            double x, double y, double z,
            float yaw, float pitch,
            CallbackInfo ci
    ) {
        ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
        // cancel the vanilla teleport
        ci.cancel();
        // use our preload+teleport instead
        NewHorizonsMain.teleportPlayerWithPreload(
                self,
                targetWorldKey,
                new BlockPos((int)x, (int)y, (int)z)
        );
    }
}

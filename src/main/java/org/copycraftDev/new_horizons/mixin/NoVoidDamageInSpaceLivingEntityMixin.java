package org.copycraftDev.new_horizons.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.core.world.dimension.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class NoVoidDamageInSpaceLivingEntityMixin extends Entity {

    public NoVoidDamageInSpaceLivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "tickInVoid",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void tickInVoid(CallbackInfo ci) {
        if (this.getWorld().getRegistryKey().equals(ModDimensions.SPACE_LEVEL_KEY)) {
            ci.cancel();
        }
    }
}


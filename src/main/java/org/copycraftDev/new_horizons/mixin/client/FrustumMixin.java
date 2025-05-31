package org.copycraftDev.new_horizons.mixin.client;

import net.minecraft.client.render.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Frustum.class)
public class FrustumMixin {
    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    private void alwaysVisible(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}

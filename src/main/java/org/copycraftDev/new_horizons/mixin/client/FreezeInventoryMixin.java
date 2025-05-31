package org.copycraftDev.new_horizons.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.copycraftDev.new_horizons.Lidar.FreezeControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class FreezeInventoryMixin {
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void freezeInventory(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (FreezeControl.isFrozen) {
            cir.setReturnValue(false);
        }
    }
}

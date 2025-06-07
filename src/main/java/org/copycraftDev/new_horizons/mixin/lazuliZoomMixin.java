package org.copycraftDev.new_horizons.mixin;


import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliZoom;


@Mixin(GameRenderer.class)

public class lazuliZoomMixin {
	@Inject(at = @At("RETURN"), method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D",cancellable = true)
	public void getZoomLevel(CallbackInfoReturnable<Double> callbackInfo) {
		double fov = callbackInfo.getReturnValue();
		if (LazuliZoom.ZOOMING) {
			callbackInfo.setReturnValue(fov * LazuliZoom.ZOOM);
		} else {
			callbackInfo.setReturnValue(fov);
		}
	}
}
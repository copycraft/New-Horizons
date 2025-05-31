package org.copycraftDev.new_horizons.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import org.copycraftDev.new_horizons.client.DimensionSwapUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * Cancels only the reloadResources + its ProgressScreen
 * when DimensionSwapUtil.skipNextReload == true.
 */
@Mixin(MinecraftClient.class)
public class InstantDimensionClientMixin {

    private boolean suppressScreen = false;

    @Inject(
            method = "setWorld",
            at = @At("HEAD")
    )
    private void beforeWorldChange(CallbackInfo ci) {
        if (DimensionSwapUtil.skipNextReload) {
            suppressScreen = true;
        }
    }

    @Inject(
            method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void suppressProgressScreen(Screen screen, CallbackInfo ci) {
        if (suppressScreen && screen instanceof ProgressScreen) {
            ci.cancel();
        }
    }


    @Inject(
            method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
            at = @At("TAIL"),
            cancellable = true
    )
    private void afterSetScreen(Screen screen, CallbackInfo ci) {
        if (DimensionSwapUtil.skipNextReload && screen instanceof ProgressScreen) {
        DimensionSwapUtil.skipNextReload = false;
    }}

    // Also clear on disconnect
    @Inject(
            method = "onDisconnected()V",
            at = @At("HEAD")
    )
    private void onDisconnected(CallbackInfo ci) {
        DimensionSwapUtil.skipNextReload = false;
    }
}

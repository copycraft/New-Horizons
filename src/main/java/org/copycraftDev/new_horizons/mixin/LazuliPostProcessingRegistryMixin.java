package org.copycraftDev.new_horizons.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GameRenderer.class)
public class LazuliPostProcessingRegistryMixin {
    @Inject(method = "loadPrograms",
            at = @At("HEAD"),
            cancellable = true)
    void loadCustomPrograms(ResourceFactory factory, CallbackInfo ci) throws IOException {
        MinecraftClient client = MinecraftClient.getInstance();
        org.copycraftDev.new_horizons.lazuli_snnipets.LazuliPostProcessingRegistry.runCallbacks(client, factory);
    }
}

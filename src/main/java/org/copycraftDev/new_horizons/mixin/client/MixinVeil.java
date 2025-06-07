package org.copycraftDev.new_horizons.mixin.client;

import foundry.veil.impl.client.imgui.VeilImGuiImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.copycraftDev.new_horizons.extrastuff.ShaderNodeEditor.drawShaderGraphWindow;
import static org.copycraftDev.new_horizons.extrastuff.ShaderNodeEditor.isOpen;

@Mixin(value = VeilImGuiImpl.class, remap = false)
public class MixinVeil {
    /**
     * Inject at @At("TAIL") of beginFrame(). By this point, Veil has already:
     *   ImGui.newFrame();
     *   Veil’s own EditorManager.render() → ImGui rendering.
     * So our ImGui calls are safely “in‐frame,” and we can open our floats.
     */
    @Inject(method = "beginFrame", at = @At("TAIL"))
    private void onBeginFrame(CallbackInfo ci) {
        if (isOpen) {
            drawShaderGraphWindow();
        }
    }
}
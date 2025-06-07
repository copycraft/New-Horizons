package org.copycraftDev.new_horizons.mixin.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.copycraftDev.new_horizons.client.InventoryButtonHelper;
import org.copycraftDev.new_horizons.extrastuff.ShaderNodeEditor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenButtonMixin extends Screen {

    protected InventoryScreenButtonMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addCustomButtons(CallbackInfo ci) {
        HandledScreenAccessor<?> accessor = (HandledScreenAccessor<?>) (Object) this;

        int left = accessor.getX(); // GUI left edge
        int top = accessor.getY();  // GUI top edge

        for (InventoryButtonHelper.Button buttonDef : InventoryButtonHelper.getButtons()) {
            int buttonX = left + (int) buttonDef.getX();
            int buttonY = top + (int) buttonDef.getY();
            int width = (int) buttonDef.getSX();
            int height = (int) buttonDef.getSY();

            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(buttonDef.gettext()),
                    button -> ShaderNodeEditor.openEditor()
            ).dimensions(buttonX, buttonY, width, height).build());
        }
    }
}

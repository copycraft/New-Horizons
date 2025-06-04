package org.copycraftDev.new_horizons.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.client.rendering.postHelpers.CustomInventoryProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin extends Screen {

    protected InventoryScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    private void drawCustomSlots(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        // Cast handler to our interface to retrieve the custom inventory array
        // Cast this to HandledScreenAccessor to access handler, x, and y fields
        HandledScreenAccessor<PlayerScreenHandler> accessor = (HandledScreenAccessor<PlayerScreenHandler>) (Object) this;

        PlayerScreenHandler handler = accessor.getHandler();
        int left = accessor.getX();
        int top = accessor.getY();


        if (!(handler instanceof CustomInventoryProvider customProvider)) return;

        ItemStack[] custom = customProvider.getCustomInventory();

// Your custom slot positions must match PlayerScreenHandlerMixin slot positions
        int[][] positions = {
                {77, 44}  // Adjust this to your custom slot position(s)
        };


        Identifier ICON_TEXTURE = Identifier.of("new_horizons", "textures/gui/container/oxygen_slot_icon.png");

        int iconU = 0;
        int iconV = 94;
        int iconWidth = 16;
        int iconHeight = 16;

        int slotSize = 18;

        for (int i = 0; i < custom.length; i++) {
            int slotX = left + positions[i][0];
            int slotY = top + positions[i][1];
            ItemStack stack = custom[i];

            if (stack.isEmpty()) {
                // Center the icon inside the slot by offsetting 1 pixel
                int iconX = slotX + (slotSize - iconWidth) / 2;
                int iconY = slotY + (slotSize - iconHeight) / 2;
                context.drawTexture(ICON_TEXTURE, iconX, iconY, iconU, iconV, iconWidth, iconHeight);
            } else {
                context.drawItem(stack, slotX, slotY);
            }
        }

    }
}

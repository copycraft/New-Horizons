package org.copycraftDev.new_horizons.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.copycraftDev.new_horizons.client.rendering.postHelpers.MyItemOnlySlot;
import org.copycraftDev.new_horizons.client.rendering.postHelpers.SlotData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;



@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends ScreenHandler {

    @Unique
    private final ItemStack[] customInventory = new ItemStack[] {
            ItemStack.EMPTY
    };

    // Provide a getter so InventoryScreenMixin can fetch these ItemStacks.
    @Unique
    public ItemStack[] getCustomInventory() {
        return customInventory;
    }


    // --------------------------------------------------------------------------------------------
    // 3) INJECT INTO THE CONSTRUCTOR TO APPEND OUR CUSTOM SLOTS
    // --------------------------------------------------------------------------------------------
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCustomSlots(PlayerInventory playerInventory, boolean isServer, PlayerEntity player, CallbackInfo ci) {
        // Define your custom slots by (indexInCustomInventory, xOffset, yOffset, allowedItem).
        List<SlotData> slotsToAdd = List.of(
                new SlotData(0, 77, 44, Items.DIAMOND)
        );

        for (SlotData data : slotsToAdd) {
            // Instantiate our custom Slot that only accepts data.allowedItem,
            // backed by the `customInventory` array at position data.index.
            addSlot(new MyItemOnlySlot(data.index, data.x, data.y, data.allowedItem, customInventory));
        }
    }

    // --------------------------------------------------------------------------------------------
    // 4) REQUIRED “DUMMY” CONSTRUCTOR FOR SCREENHANDLER
    // --------------------------------------------------------------------------------------------
    // Mixins extending ScreenHandler must define a constructor matching super(null, 0).
    protected PlayerScreenHandlerMixin() {
        super((ScreenHandlerType<?>) null, 0);
    }
}
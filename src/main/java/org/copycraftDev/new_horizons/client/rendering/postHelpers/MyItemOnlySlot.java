package org.copycraftDev.new_horizons.client.rendering.postHelpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class MyItemOnlySlot extends Slot {
    private final int customIndex;
    private final Item allowedItem;
    private final ItemStack[] backingInventory;

    public MyItemOnlySlot(int customIndex, int x, int y, Item allowedItem, ItemStack[] backingInventory) {
        // We pass a minimal Inventory implementation (SimpleInventory) to satisfy the super constructor.
        super(new SimpleInventory(backingInventory.length), customIndex, x, y);
        this.customIndex = customIndex;
        this.allowedItem = allowedItem;
        this.backingInventory = backingInventory;
    }

    @Override
    public ItemStack getStack() {
        return backingInventory[customIndex];
    }

    @Override
    public void setStack(ItemStack stack) {
        backingInventory[customIndex] = stack;
        this.markDirty();
    }

    @Override
    public ItemStack takeStack(int amount) {
        // “takeStack” is the correct override in 1.21.1 for removing up to `amount`.
        ItemStack stack = backingInventory[customIndex];
        if (!stack.isEmpty()) {
            if (stack.getCount() <= amount) {
                backingInventory[customIndex] = ItemStack.EMPTY;
                this.markDirty();
                return stack;
            } else {
                ItemStack result = stack.split(amount);
                if (stack.isEmpty()) {
                    backingInventory[customIndex] = ItemStack.EMPTY;
                }
                this.markDirty();
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() == allowedItem;
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return true;
    }
}
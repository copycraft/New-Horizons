package org.copycraftDev.new_horizons.client.rendering.postHelpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

// ================================================================================================
// 6) SIMPLE INVENTORY HELPER (USED ONLY FOR THE SUPER CONSTRUCTOR)
//    - Basic Inventory with fixed size; never persisted.
// ================================================================================================
public class SimpleInventory implements Inventory {
    private final ItemStack[] items;

    public SimpleInventory(int size) {
        items = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            items[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int size() {
        return items.length;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= items.length) return ItemStack.EMPTY;
        return items[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < 0 || slot >= items.length) return ItemStack.EMPTY;
        ItemStack stack = items[slot];
        if (!stack.isEmpty()) {
            if (stack.getCount() <= amount) {
                items[slot] = ItemStack.EMPTY;
                return stack;
            } else {
                ItemStack result = stack.split(amount);
                if (stack.isEmpty()) {
                    items[slot] = ItemStack.EMPTY;
                }
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= items.length) return ItemStack.EMPTY;
        ItemStack stack = items[slot];
        items[slot] = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < items.length) {
            items[slot] = stack;
        }
    }

    @Override
    public void markDirty() { }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < items.length; i++) {
            items[i] = ItemStack.EMPTY;
        }
    }
}

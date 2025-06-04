package org.copycraftDev.new_horizons.client.rendering.postHelpers;

import net.minecraft.item.Item;

public class SlotData {
    public final int index;
    public final int x;
    public final int y;
    public final Item allowedItem;

    public SlotData(int index, int x, int y, Item allowedItem) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.allowedItem = allowedItem;
    }
}
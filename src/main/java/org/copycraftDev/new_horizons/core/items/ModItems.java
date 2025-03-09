package org.copycraftDev.new_horizons.core.items;


import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static org.copycraftDev.new_horizons.New_horizons.MOD_ID;


public class ModItems {
    private ModItems() {}

    // an instance of our new item
    public static final Item CUSTOM_ITEM = register("flashlight", new Item(new Item.Settings()));

    public static <T extends Item> T register(String path, T item) {
        // For versions below 1.21, please replace ''Identifier.of'' with ''new Identifier''
        return Registry.register(Registries.ITEM, Identifier.of(MOD_ID, path), item);
    }

    public static void initialize() {
    }
}

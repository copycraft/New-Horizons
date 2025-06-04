package org.copycraftDev.new_horizons.core.items;


import nazario.liby.api.registry.auto.LibyAutoRegister;
import nazario.liby.api.registry.auto.LibyEntrypoints;
import nazario.liby.api.registry.helper.LibyItemRegister;
import net.minecraft.item.Item;
import org.copycraftDev.new_horizons.core.items.custom.CameraItem;
import org.copycraftDev.new_horizons.core.items.custom.FlashlightItem;
import org.copycraftDev.new_horizons.core.items.custom.LidarGunItem;

import static org.copycraftDev.new_horizons.NewHorizonsMain.MOD_ID;


@LibyAutoRegister(method = "initialize", entrypoints = LibyEntrypoints.MAIN)
public class ModItems {
    private static final LibyItemRegister REGISTER = new LibyItemRegister(MOD_ID);

    public static final Item FLASHLIGHT_ITEM = REGISTER.registerItem("flashlight", new FlashlightItem(new Item.Settings()));
    public static final Item LIDAR_GUN_ITEM = REGISTER.registerItem("lidargun", new LidarGunItem(new Item.Settings()));
    public static final Item CAMERA_ITEM = REGISTER.registerItem("camera", new CameraItem(new Item.Settings()));
    public static final Item SPRAYPAINT = REGISTER.registerItem("spraypaint", new CameraItem(new Item.Settings()));

    public static void initialize() {

    }
}

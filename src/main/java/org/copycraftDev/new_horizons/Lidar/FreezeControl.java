package org.copycraftDev.new_horizons.Lidar;

public class FreezeControl {
    public static boolean isFrozen = false;

    public static void toggleFreeze() {
        isFrozen = !isFrozen;
    }
}

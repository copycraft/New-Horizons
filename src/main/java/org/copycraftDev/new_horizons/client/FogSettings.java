package org.copycraftDev.new_horizons.client;

public class FogSettings {
    private static float fogStart = 0.0F;
    private static float fogEnd = 192.0F;

    public static float getFogStart() {
        return fogStart;
    }

    public static void setFogStart(float start) {
        fogStart = start;
    }

    public static float getFogEnd() {
        return fogEnd;
    }

    public static void setFogEnd(float end) {
        fogEnd = end;
    }
}


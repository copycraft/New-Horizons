package org.copycraftDev.new_horizons.Lidar;

import java.awt.Color;

/**
 * Provides a method to shift a given RGB (floats 0→1) by an extra hue offset,
 * returning both a float[] (for renderer tint) and an int (for Lidar color).
 */
public class ColorUtils {
    /**
     * Given r,g,b in [0..1] and a hue‐offset in degrees, returns a float[3] {r',g',b'} in [0..1].
     */
    public static float[] shiftHueFloat(float r, float g, float b, float hueOffsetDegrees) {
        // Convert input to a java.awt.Color
        Color base = new Color(r, g, b);
        float[] hsb = Color.RGBtoHSB(
                base.getRed(), base.getGreen(), base.getBlue(),
                null
        );
        hsb[0] = (hsb[0] + (hueOffsetDegrees / 360f)) % 1.0f;
        int rgbInt = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);

        return new float[] {
                ((rgbInt >> 16) & 0xFF) / 255f,
                ((rgbInt >> 8)  & 0xFF) / 255f,
                ( rgbInt        & 0xFF) / 255f
        };
    }

    /**
     * Given r,g,b in [0..1], returns a single 0xRRGGBB int (used by LidarSystem).
     */
    public static int toRGBInt(float r, float g, float b) {
        int R = Math.round(r * 255f) & 0xFF;
        int G = Math.round(g * 255f) & 0xFF;
        int B = Math.round(b * 255f) & 0xFF;
        return (R << 16) | (G << 8) | B;
    }
}

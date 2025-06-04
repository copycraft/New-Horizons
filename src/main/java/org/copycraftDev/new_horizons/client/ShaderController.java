package org.copycraftDev.new_horizons.client;

import org.copycraftDev.new_horizons.client.rendering.ModShaders;

public class ShaderController {
    public static String shader;
    private static String currentShaderId = ModShaders.BLUR_PROCESSOR;
    private static boolean enabled = true;

    private static float time = 0f;

    public static void loadShader(String shaderId) {
        shader = shaderId;
        currentShaderId = shaderId;
    }


    public static void toggle() {
        enabled = !enabled;
        if (enabled && shader == null) {
            loadShader(currentShaderId);
        }
    }

    public static void enable() {
        enabled = true;
        if (shader == null) {
            loadShader(currentShaderId);
        }
    }

    public static void disable() {
        enabled = false;
    }

    public static boolean isEnabled() {
        return enabled;
    }


    public static void setShader(String newshader) {
        if (enabled) {
        disable();
        currentShaderId = newshader;
            loadShader(newshader);
            enable();
        }
        else {
                currentShaderId = newshader;
            }
        }


    public static String getShaderId() {
        return shader;
    }
}

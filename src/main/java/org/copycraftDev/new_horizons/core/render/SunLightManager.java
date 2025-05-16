package org.copycraftDev.new_horizons.core.render;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.DirectionalLight;
import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Vector3f;

@LibyAutoRegister(method = "initialize")
public class SunLightManager {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static DirectionalLight sunLight;

    // Default colours (unchanged)
    private static final Vector3f dayColor    = new Vector3f(1.0f, 1.0f, 0.9f);
    private static final Vector3f sunsetColor = new Vector3f(1.0f, 0.4f, 0.2f);
    private static final Vector3f nightColor  = new Vector3f(0.1f, 0.1f, 0.3f);

    // Offsets for dawn and sunrise
    private static long dawnStartOffset = 1800L;
    private static long sunriseOffset    = 1000L;

    // Custom space dimension key
    private static final RegistryKey<World> SPACE_DIMENSION =
            RegistryKey.of(RegistryKeys.WORLD, Identifier.of("new_horizons", "space"));

    public static void setDawnStartOffset(long offsetTicks) {
        dawnStartOffset = offsetTicks;
    }

    public static void setSunriseOffset(long offsetTicks) {
        sunriseOffset = offsetTicks;
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.world == null || VeilRenderSystem.renderer() == null) return;
            // Skip in space dimension
            if (mc.world.getRegistryKey().equals(SPACE_DIMENSION)) return;

            if (sunLight == null) createSunLight();

            long t = mc.world.getTimeOfDay() % 24000L;
            updateLightProperties(t);
        });
    }

    private static void createSunLight() {
        sunLight = (DirectionalLight) new DirectionalLight()
                .setDirection(0.0f, -1.0f, 0.0f)
                .setColor(dayColor)
                .setBrightness(2.0f);
        VeilRenderSystem.renderer().getLightRenderer().addLight(sunLight);
    }

    private static void updateLightProperties(long timeOfDay) {
        // shift time context
        long shifted = (timeOfDay - dawnStartOffset - sunriseOffset) % 24000L;
        if (shifted < 0) shifted += 24000L;

        // compute normalized time & angle
        float timeNorm = (shifted + dawnStartOffset + sunriseOffset) / 24000f;
        float angle    = timeNorm * (float)(2 * Math.PI);

        // compute daylight strength
        float daylightStrength = 1.0f - MathHelper.cos(angle) * 2.0f + 0.2f;
        daylightStrength = MathHelper.clamp(daylightStrength, 0.0f, 1.0f);

        // apply smoothstep to full range
        float smoothDS = daylightStrength * daylightStrength * (3 - 2 * daylightStrength);

        // compute brightness mapping [-0.4, 1.2]
        float computedBrightness = -0.4f + smoothDS * 1.6f;

        float brightness;
        if (shifted < dawnStartOffset) {
            brightness = computedBrightness; // now negative smoothly
        } else if (shifted < dawnStartOffset + sunriseOffset) {
            // smooth dawn ramp
            float progress = (shifted - dawnStartOffset) / (float) sunriseOffset;
            float smooth = progress * progress * (3 - 2 * progress);
            brightness = computedBrightness * smooth;
        } else {
            brightness = computedBrightness;
        }

        // compute colour as before
        Vector3f white  = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f yellow = new Vector3f(1.0f, 1.0f, 0.85f);
        Vector3f color  = new Vector3f(white).lerp(yellow, daylightStrength);
        if (daylightStrength == 0f) {
            color.set(1.0f, 1.0f, 1.0f);
        }

        sunLight.setColor(color);
        sunLight.setBrightness(brightness);
    }
}
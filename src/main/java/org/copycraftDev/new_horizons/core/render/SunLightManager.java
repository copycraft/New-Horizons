package org.copycraftDev.new_horizons.core.render;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.DirectionalLight;
import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

@LibyAutoRegister(method = "initialize")
public class SunLightManager {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static DirectionalLight sunLight;
    private static final Vector3f dayColor = new Vector3f(1.0f, 1.0f, 0.9f);
    private static final Vector3f sunsetColor = new Vector3f(1.0f, 0.4f, 0.2f);
    private static final Vector3f nightColor = new Vector3f(0.1f, 0.1f, 0.3f);

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.world == null || VeilRenderSystem.renderer() == null) return;

            if (sunLight == null) {
                createSunLight();
            }

            updateLightProperties(mc.world.getTimeOfDay() % 24000L);
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
        float timeNorm = (timeOfDay % 24000L) / 24000f;
        float angle    = timeNorm * (float)(2 * Math.PI);

        // 1) Compute daylightStrength ∈ [0,1]
        float daylightStrength = 1.0f - MathHelper.cos(angle) * 2.0f + 0.2f;
        daylightStrength = MathHelper.clamp(daylightStrength, 0.0f, 1.0f);

        // 2) Map to brightness ∈ [–0.4, 1.2]
        //    brightness = -0.4 + daylightStrength * (1.2 + 0.4) = -0.4 + daylightStrength * 1.6
        float brightness = -0.4f + daylightStrength * 1.6f;

        // 3) Compute tint from white → yellow
        Vector3f white  = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f yellow = new Vector3f(1.0f, 1.0f, 0.85f);
        Vector3f color  = new Vector3f(white).lerp(yellow, daylightStrength);

        // 4) At true midnight, force pure white
        if (daylightStrength == 0f) {
            color.set(1.0f, 1.0f, 1.0f);
        }

        // 5) Apply
        sunLight.setColor(color);
        sunLight.setBrightness(brightness);
    }
}

package org.copycraftDev.new_horizons.core.render;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.AreaLight;
import foundry.veil.api.client.render.light.PointLight;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LightingBlockRenderer {

    private final PointLight pointLight;
    private boolean isLightOn = false;

    public LightingBlockRenderer() {
        // Initializing the AreaLight
        this.pointLight = new PointLight();
        this.pointLight.setColor(1.0f, 0.9f, 0.647f);
        this.pointLight.setBrightness(1.5f);
    }

    public void addLight() {
        if (!isLightOn) {
            // Add the light to the renderer
            VeilRenderSystem.renderer().getLightRenderer().addLight(pointLight);
            isLightOn = true;
        }
    }

    public void disableLight() {
        if (isLightOn) {
            // Remove the light from the renderer
            VeilRenderSystem.renderer().getLightRenderer().removeLight(pointLight);
            isLightOn = false;
        }
    }
}

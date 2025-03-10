package org.copycraftDev.new_horizons.core.render;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.AreaLight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.network.ClientPlayerEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FlashlightRenderer extends AreaLight {

    private boolean isOn = false;
    private Vec3d lastPosition = new Vec3d(0, 0, 0);
    private Quaternionf lastRotation = new Quaternionf();
    private static final float SMOOTH_FACTOR = 0.6f;

    public FlashlightRenderer() {
        super();
        this.setDistance(20);
        this.setSize(1.5, 1.5);
        this.setColor(1.0f, 0.9f, 0.6f);
        this.setBrightness(6.0f);
    }

    public void toggle() {
        isOn = !isOn;
        if (isOn) {
            this.setDistance(13.0f);
            this.setSize(0.8, 0.8);
            this.setColor(1.0f, 0.9f, 0.6f);
            this.setBrightness(4.0f);
            this.setAngle((float) Math.toRadians(40));
            VeilRenderSystem.renderer().getLightRenderer().addLight(this);
        } else {
            VeilRenderSystem.renderer().getLightRenderer().removeLight(this);
            this.setDistance(0);
            this.setSize(0.0, 0.0);
        }
    }

    public boolean isOn() {
        return isOn;
    }

    public void updateFromCamera(MinecraftClient client) {
        if (!isOn || client == null || client.gameRenderer == null) return;
        Camera camera = client.gameRenderer.getCamera();
        if (camera == null) return;

        ClientPlayerEntity player = client.player;
        if (player != null && player.getMainHandStack().isEmpty()) {
            toggle();
            return;
        }

        Vec3d pos = camera.getPos();
        Quaternionf cameraRotation = camera.getRotation();

        lastPosition = lastPosition.lerp(pos, SMOOTH_FACTOR);
        lastRotation.slerp(cameraRotation, SMOOTH_FACTOR);

        Vector3f forward = new Vector3f(0, 0, 1).rotate(lastRotation);
        Vector3f up = new Vector3f(0, 1, 0).rotate(lastRotation);

        this.setPosition(lastPosition.x, lastPosition.y, lastPosition.z);
        this.setOrientation(new Quaternionf().lookAlong(forward, up));
    }
}

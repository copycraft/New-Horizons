package org.copycraftDev.new_horizons.core.render;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.AreaLight;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FlashlightRenderer extends AreaLight {

    private final AreaLight areaLight2 = new AreaLight();
    private boolean isOn = false;
    private Vec3d lastPosition = new Vec3d(0, 0, 0);
    private Quaternionf lastRotation = new Quaternionf();
    private static final float SMOOTH_FACTOR = 0.2f;
    private final MinecraftClient client = MinecraftClient.getInstance();

    public FlashlightRenderer() {
        super();
        this.setDistance(50.0f);
        this.setSize(1.0, 1.0);
        this.setColor(1.0f, 0.9f, 0.647f);
        this.setBrightness(1.5f);
        areaLight2.setDistance(50f);
        areaLight2.setSize(0.15, 0.15);
        areaLight2.setBrightness(2.0f);
        areaLight2.setAngle(0.25f);
        areaLight2.setColor(1.0f, 0.9f, 0.641f);

        // Register update callback for smooth rendering
        WorldRenderEvents.AFTER_ENTITIES.register(context -> updateFromCamera(client));
    }

    public void toggle() {
        isOn = !isOn;

        if (isOn) {
            this.setAngle((float) Math.toRadians(35));
            VeilRenderSystem.renderer().getLightRenderer().addLight(this);
            VeilRenderSystem.renderer().getLightRenderer().addLight(areaLight2);
            playToggleSound();
        } else {
            VeilRenderSystem.renderer().getLightRenderer().removeLight(this);
            VeilRenderSystem.renderer().getLightRenderer().removeLight(areaLight2);
        }
    }

    private void playToggleSound() {
        if (client.player != null) {
            client.player.getWorld().playSound(
                    null,
                    client.player.getBlockPos(),
                    SoundEvents.BLOCK_LEVER_CLICK,
                    SoundCategory.PLAYERS,
                    1f,
                    1f
            );
        }
    }

    public boolean isOn() {
        return isOn;
    }

    public void updateFromCamera(MinecraftClient client) {
        if (!isOn || client == null || client.gameRenderer == null) return;
        Camera camera = client.gameRenderer.getCamera();
        if (camera == null) return;

        Vec3d pos = camera.getPos();
        Quaternionf cameraRotation = camera.getRotation();

        lastPosition = lastPosition.lerp(pos, SMOOTH_FACTOR);
        lastRotation.slerp(cameraRotation, SMOOTH_FACTOR);

        Vector3f forward = new Vector3f(0, 0, 1).rotate(lastRotation);
        Vector3f up = new Vector3f(0, 1, 0).rotate(lastRotation);

        Quaternionf orientation = new Quaternionf().lookAlong(forward, up);

        this.setPosition(lastPosition.x, lastPosition.y + 0.35, lastPosition.z);
        this.setOrientation(orientation);
        areaLight2.setPosition(lastPosition.x, lastPosition.y + 0.35, lastPosition.z);
        areaLight2.setOrientation(orientation);
    }
}

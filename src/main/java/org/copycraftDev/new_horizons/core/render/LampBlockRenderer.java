package org.copycraftDev.new_horizons.core.render;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.PointLight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LampBlockRenderer extends PointLight {

    private boolean isOn = false;
    private Vec3d position;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(LampBlockRenderer.class);

    public LampBlockRenderer(BlockPos lampPosition) {
        super();

        this.position = new Vec3d(
                lampPosition.getX() + 0.001,
                lampPosition.getY() + 0.001,
                lampPosition.getZ() + 0.001
        );
        this.setColor(1.0f, 0.9f, 0.647f);
        this.setBrightness(1.5f);
        this.setRadius(5.0f);
        this.setPosition(position.x, position.y, position.z);

        LOGGER.info("LampBlockRenderer initialized at position: {}", lampPosition);
    }

    public void addLight() {
        if (!isOn) {
            if (VeilRenderSystem.renderer() == null || VeilRenderSystem.renderer().getLightRenderer() == null) {
                LOGGER.error("VeilRenderSystem is not initialized properly!");
                return;
            }
            VeilRenderSystem.renderer().getLightRenderer().addLight(this);
            playToggleSound();
            isOn = true;
            LOGGER.info("PointLight added to renderer at position: {}", this.position);
        }
    }

    public void removeLight() {
        if (isOn) {
            if (VeilRenderSystem.renderer() == null || VeilRenderSystem.renderer().getLightRenderer() == null) {
                LOGGER.error("VeilRenderSystem is not initialized properly!");
                return;
            }
            VeilRenderSystem.renderer().getLightRenderer().removeLight(this);
            isOn = false;
            LOGGER.info("PointLight removed from renderer at position: {}", this.position);
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

    public void setLampPosition(BlockPos lampPosition) {
        this.position = new Vec3d(
                lampPosition.getX()  -0.5f,
                lampPosition.getY()  -0.5f,
                lampPosition.getZ() -0.5f
        );
        this.setPosition(this.position.x, this.position.y, this.position.z);
        LOGGER.info("Lamp position set to: {}", this.position);
    }

    @Override
    public Vector3dc getPosition() {
        return new Vector3d(position.x, position.y, position.z);
    }

    public Vec3d getPositionVec3d() {
        return this.position;
    }
}

package org.copycraftDev.new_horizons.core.render;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.PointLight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LampBlockRenderer extends PointLight {

    private boolean isOn = false;  // Track if the lamp is on or off
    private Vec3d position;  // Position of the lamp (static, set dynamically)
    private final MinecraftClient client = MinecraftClient.getInstance();  // Get Minecraft client instance

    private static final Logger LOGGER = LoggerFactory.getLogger(LampBlockRenderer.class);  // Logger instance

    public LampBlockRenderer(BlockPos lampPosition) {
        super();  // Call the constructor of PointLight

        this.position = new Vec3d(lampPosition.getX(), lampPosition.getY(), lampPosition.getZ());  // Set the static position of the lamp
        this.setColor(1.0f, 0.9f, 0.647f);  // Set the light color (yellowish, similar to the original)
        this.setBrightness(1.5f);  // Set the brightness of the light
        this.setRadius(5.0f);  // Set the radius of the light

        LOGGER.info("LampBlockRenderer initialized at position: {}", lampPosition);  // Log the initialization
    }

    public void toggle() {
        // Toggle the lamp light on or off
        if (isOn) {
            removeLight();  // Turn off the light
        } else {
            addLight();  // Turn on the light
        }

        // Toggle the state after operation
        isOn = !isOn;

        LOGGER.info("Lamp state: {}", isOn ? "ON" : "OFF");  // Log the light toggling
    }

    private void addLight() {
        // Add the PointLight to the renderer
        VeilRenderSystem.renderer().getLightRenderer().addLight(this);  // Add the light to the renderer
        LOGGER.info("PointLight added to renderer at position: {}", this.position);  // Log when light is added
        playToggleSound();  // Play a toggle sound when the lamp is turned on
    }

    private void removeLight() {
        // Remove the PointLight from the renderer
        VeilRenderSystem.renderer().getLightRenderer().removeLight(this);  // Remove the light from the renderer
        LOGGER.info("PointLight removed from renderer at position: {}", this.position);  // Log when light is removed
    }

    private void playToggleSound() {
        // Play a sound when toggling the lamp
        if (client.player != null) {
            client.player.getWorld().playSound(
                    null,
                    client.player.getBlockPos(),
                    net.minecraft.sound.SoundEvents.BLOCK_LEVER_CLICK,
                    net.minecraft.sound.SoundCategory.PLAYERS,
                    1f,
                    1f
            );
        }
    }

    public boolean isOn() {
        return isOn;  // Return the current state of the lamp (on or off)
    }

    public void setLampPosition(BlockPos lampPosition) {
        // Manually set the position of the lamp
        this.position = new Vec3d(lampPosition.getX(), lampPosition.getY(), lampPosition.getZ());
        // Update the light's position immediately
        this.setPosition(this.position.x, this.position.y + 0.35, this.position.z);  // Offset by 0.35 on the Y-axis if desired

        LOGGER.info("Lamp position set to: {}", this.position);  // Log the new position
    }

    @Override
    public Vector3dc getPosition() {
        return new Vector3d(position.x, position.y, position.z);  // Return the position of the light
    }

    public Vec3d getPositionVec3d() {
        return this.position;  // Return the position as a Vec3d
    }

    public static LampBlockRenderer createLampRenderer(BlockPos lampPosition) {
        // This method can be used to create multiple LampBlockRenderers dynamically
        LampBlockRenderer renderer = new LampBlockRenderer(lampPosition);
        return renderer;
    }
}

package org.copycraftDev.new_horizons.core.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EntityPose;
import foundry.veil.api.client.render.light.AreaLight;

public class VeilLighting {
    private static AreaLight flashlightLight;

    /**
     * Adds a dynamic AreaLight for the player.
     *
     * @param player    The player for whom the flashlight is being created.
     * @param brightness The brightness of the light.
     * @param color      The color of the light (0xRRGGBB format).
     */
    public static void addDynamicLight(PlayerEntity player, int brightness, int color) {
        if (flashlightLight == null) {
            // Create the AreaLight for the flashlight
            flashlightLight = new AreaLight()
                    .setColor(((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f)
                    .setBrightness(brightness)
                    .setSize(1.0, 1.0)  // Adjust size if necessary
                    .setDistance(5.0f)  // Set a reasonable distance for the flashlight
                    .setAngle((float) Math.toRadians(45));  // Set the light's angle

            // Set the initial position of the light based on the player's eye position (using STANDING pose)
            flashlightLight.setPosition(player.getX(), player.getY() + player.getEyeHeight(EntityPose.STANDING), player.getZ());
        }
    }

    /**
     * Removes the dynamic light associated with the player.
     *
     * @param player The player for whom the flashlight light is being removed.
     */
    public static void removeDynamicLight(PlayerEntity player) {
        if (flashlightLight != null) {
            // Here we assume the system cleans up the light manually when removed.
            flashlightLight = null;
        }
    }

    /**
     * Updates the position of the dynamic light to follow the player's eye position.
     *
     * @param player The player whose flashlight light position is being updated.
     * @param x      The x-coordinate to set for the light's position.
     * @param y      The y-coordinate to set for the light's position.
     * @param z      The z-coordinate to set for the light's position.
     */
    public static void updateDynamicLight(PlayerEntity player, double x, double y, double z) {
        if (flashlightLight != null) {
            // Update the position of the light
            flashlightLight.setPosition(x, y, z);
        }
    }
}

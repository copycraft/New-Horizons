package org.copycraftDev.new_horizons.core.items;

import foundry.veil.api.client.render.light.AreaLight;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FlashlightManager {

    private static AreaLight flashlight;
    private static boolean flashlightOn = false;

    public static void initialize(MinecraftClient client, KeyBinding keyBinding) {
        // Initialize flashlight
        flashlight = new AreaLight();

        // Register client tick event
        ClientTickEvents.END_CLIENT_TICK.register(clientInstance -> {
            if (keyBinding.isPressed()) {
                toggleFlashlight(); // Toggle flashlight state when key is pressed
            }
            updateFlashlight(clientInstance); // Continuously update flashlight properties
        });
    }

    private static void toggleFlashlight() {
        flashlightOn = !flashlightOn; // Toggle flashlight state
        if (flashlightOn) {
            flashlight.setDistance(10.0f); // Set flashlight range when turned on
        } else {
            flashlight.setDistance(0); // Turn off flashlight by setting distance to 0
            flashlight.setSize(0.0, 0.0); // Optionally reduce size to zero when off
        }
    }

    private static void updateFlashlight(MinecraftClient client) {
        if (!flashlightOn) return; // Only update flashlight if it's turned on

        // Get the camera's position and rotation
        var cameraPos = client.gameRenderer.getCamera().getPos();
        Quaternionf cameraRotation = client.gameRenderer.getCamera().getRotation();

        // Calculate forward (look) and up vectors using camera's rotation
        Vector3f cameraLook = new Vector3f(0, 0, 1).rotate(cameraRotation);  // Forward direction (Z axis)
        Vector3f cameraUp = new Vector3f(0, 1, 0).rotate(cameraRotation);     // Up direction (Y axis)

        // Set the flashlight's position and orientation to match the camera's
        flashlight.setPosition(cameraPos.x, cameraPos.y, cameraPos.z);
        flashlight.setOrientation(new Quaternionf().lookAlong(cameraLook, cameraUp));

        // Adjust flashlight's beam size, angle, and distance
        flashlight.setSize(0.5, 0.5); // Narrow beam for flashlight
        flashlight.setAngle((float) Math.toRadians(30)); // Set angle of influence (narrow)
    }
}

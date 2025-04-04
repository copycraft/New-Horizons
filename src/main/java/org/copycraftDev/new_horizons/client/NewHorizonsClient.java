package org.copycraftDev.new_horizons.client;

import foundry.veil.Veil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.particle.ModParticlesClient;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRenderer;
import org.copycraftDev.new_horizons.core.entity.ModEntities;
import org.copycraftDev.new_horizons.core.particle.FogParticle;
import org.copycraftDev.new_horizons.core.particle.ModParticles;
import org.copycraftDev.new_horizons.client.render.entity.SeatEntityRenderer;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.lwjgl.glfw.GLFW;

import static org.copycraftDev.new_horizons.NewHorizonsMain.LOGGER;
import static qouteall.imm_ptl.core.render.MyRenderHelper.client;

public class NewHorizonsClient implements ClientModInitializer {
    // Static field to store the current tick delta
    private static float currentTickDelta = 0.0F;
    static double speed = 0;
    static Vec3d direction = Vec3d.ZERO;
    static Vec3d speed3d = Vec3d.ZERO;

    public static float getCurrentTickDelta() {
        return currentTickDelta;
    }
    public static KeyBinding ARROW_UP;
    public static KeyBinding ARROW_DOWN;
    public static KeyBinding ARROW_LEFT;
    public static KeyBinding ARROW_RIGHT;
    private static final GameOptions options = client.options;
    private static Vec3d movementDirection = Vec3d.ZERO;



    @Override
    public void onInitializeClient() {
        Veil.init();

        CelestialBodyRenderer.register();

        ParticleFactoryRegistry.getInstance().register(ModParticles.FOG_PARTICLE, spriteProvider ->
                new ModParticlesClient.FogParticle.Factory(spriteProvider)
        );

        // Register a keybinding
        KeyBinding flashlightKeyBinding = new KeyBinding(
                "key.new_horizons.override_my_ass",  // Translation key
                GLFW.GLFW_KEY_F5,                   // Default key
                "category.new_horizons"             // Keybinding category
        );


        KeyBinding ARROW_UP = new KeyBinding(
                "key.new_horizons.arrow_up",   // Translation key
                GLFW.GLFW_KEY_UP,              // Default key (Up Arrow)
                "category.new_horizons"        // Keybinding category
        );

        KeyBinding ARROW_DOWN = new KeyBinding(
                "key.new_horizons.arrow_down", // Translation key
                GLFW.GLFW_KEY_DOWN,            // Default key (Down Arrow)
                "category.new_horizons"        // Keybinding category
        );

        KeyBinding ARROW_LEFT = new KeyBinding(
                "key.new_horizons.arrow_left", // Translation key
                GLFW.GLFW_KEY_LEFT,            // Default key (Left Arrow)
                "category.new_horizons"        // Keybinding category
        );

        KeyBinding ARROW_RIGHT = new KeyBinding(
                "key.new_horizons.arrow_right",// Translation key
                GLFW.GLFW_KEY_RIGHT,           // Default key (Right Arrow)
                "category.new_horizons"        // Keybinding category
        );
        KeyBindingHelper.registerKeyBinding(flashlightKeyBinding);
        KeyBindingHelper.registerKeyBinding(ARROW_DOWN);
        KeyBindingHelper.registerKeyBinding(ARROW_LEFT);
        KeyBindingHelper.registerKeyBinding(ARROW_RIGHT);
        KeyBindingHelper.registerKeyBinding(ARROW_UP);


        ParticleFactoryRegistry.getInstance().register(ModParticles.FOG_PARTICLE, FogParticle.Factory::new);
        EntityRendererRegistry.register(ModEntities.SEAT_ENTITY, SeatEntityRenderer::new);
        

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                // --- 1. Update Orientation Based on Look ---
                // Get player's yaw and pitch in radians when seated
                var player = client.player;
                if (player.hasVehicle()) {
                    // Invert yaw and pitch when in seat
                    double playerPitch = Math.toRadians(player.getYaw());  // Invert yaw
                    double playerYaw = -Math.toRadians(player.getPitch()); // Invert pitch
                    LazuliGeometryBuilder.setRenderingSpaceDir(playerPitch, 0, playerYaw);

                } else {

                }


                Vec3d inputVec = Vec3d.ZERO;
                if (ARROW_UP.isPressed()) {
                    inputVec = inputVec.add(0, 0, 1);
                    LOGGER.info("Arrow Up pressed");
                }
                if (ARROW_DOWN.isPressed()) {
                    inputVec = inputVec.add(0, 0, -1);
                    LOGGER.info("Arrow Down pressed");
                }
                if (ARROW_LEFT.isPressed()) {
                    inputVec = inputVec.add(1, 0, 0);
                    LOGGER.info("Arrow Left pressed");
                }
                if (ARROW_RIGHT.isPressed()) {
                    inputVec = inputVec.add(-1, 0, 0);
                    LOGGER.info("Arrow Right pressed");
                }

                Vec3d acceleration = inputVec;

                // Update speed: accelerate if there is input; otherwise, decelerate.
                if (inputVec.lengthSquared() > 0) {
                    inputVec = inputVec.normalize();
                    speed += (1.0 - speed) * 0.015;  // Gradually accelerate toward speed 1.0
                    speed3d = speed3d.add(acceleration.multiply(0.02));
                } else {
                    speed *= 0.95;  // Gradually decelerate when no keys are pressed
                    speed3d = speed3d.multiply(0.92);
                }

                movementDirection = inputVec;
                Vec3d displacement = speed3d;

                // Apply the displacement with rotation applied in the geometry builder.
                LazuliGeometryBuilder.rotatedSpaceDisplaceRenderingSpacePos(displacement);
            }
        });

    };

}


package org.copycraftDev.new_horizons.client;

import foundry.veil.Veil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.misc.StickDashHandler;
import org.copycraftDev.new_horizons.client.particle.ModParticlesClient;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRenderer;
import org.copycraftDev.new_horizons.core.bigbang.BigBangClientManager;
import org.copycraftDev.new_horizons.core.entity.ModEntities;
import org.copycraftDev.new_horizons.core.particle.FogParticle;
import org.copycraftDev.new_horizons.core.particle.ModParticles;
import org.copycraftDev.new_horizons.client.render.entity.SeatEntityRenderer;
import org.copycraftDev.new_horizons.extrastuff.PlanetTextureGenerator;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.core.blocks.ModBlocks;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliRenderingRegistry;
import org.copycraftDev.new_horizons.physics.PhysicsRenderer;
import org.lwjgl.glfw.GLFW;


public class NewHorizonsClient implements ClientModInitializer {
    private static float currentTickDelta = 0.0F;
    static double speed = 0;
    static Vec3d direction = Vec3d.ZERO;
    static Vec3d speed3d = Vec3d.ZERO;

    // Declare key bindings without initializing them statically
    public static KeyBinding ARROW_UP;
    public static KeyBinding ARROW_DOWN;
    public static KeyBinding ARROW_LEFT;
    public static KeyBinding ARROW_RIGHT;

    private static Vec3d movementDirection = Vec3d.ZERO;


    @Override
    public void onInitializeClient() {
        Veil.init();

        PhysicsRenderer.register();
        org.copycraftDev.new_horizons.physics.PhysicsRenderer.register();


        CelestialBodyRenderer.register();
        LazuliRenderingRegistry.registerLazuliRenderPhases();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PRIVACY_GLASS, RenderLayer.getTranslucent());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            BigBangClientManager.tick(client.world);


        });

        // Register particles
        ParticleFactoryRegistry.getInstance().register(ModParticles.FOG_PARTICLE, spriteProvider ->
                new ModParticlesClient.FogParticle.Factory(spriteProvider)
        );

        // Register keybindings
        registerKeyBindings();

        // Register particle and entity renderers
        ParticleFactoryRegistry.getInstance().register(ModParticles.FOG_PARTICLE, FogParticle.Factory::new);
        EntityRendererRegistry.register(ModEntities.SEAT_ENTITY, SeatEntityRenderer::new);

        WorldRenderEvents.BEFORE_ENTITIES.register((context) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null) return;
            var player = client.player;

            // === Invert Pitch/Yaw if Player is in a Vehicle ===
            if (player.hasVehicle()) {
                double playerPitch = Math.toRadians(player.getYaw());   // Invert yaw
                double playerYaw = -Math.toRadians(player.getPitch());  // Invert pitch
                LazuliGeometryBuilder.setRenderingSpaceDir(playerPitch, 0, playerYaw);
            }

            // === Input Detection (per frame!) ===
            Vec3d inputVec = Vec3d.ZERO;

            if (ARROW_UP.isPressed()) {
                inputVec = inputVec.add(0, 0, 1);
            }
            if (ARROW_DOWN.isPressed()) {
                inputVec = inputVec.add(0, 0, -1);
            }
            if (ARROW_LEFT.isPressed()) {
                inputVec = inputVec.add(1, 0, 0);
            }
            if (ARROW_RIGHT.isPressed()) {
                inputVec = inputVec.add(-1, 0, 0);
            }

            Vec3d acceleration = inputVec;

            if (inputVec.lengthSquared() > 0) {
                inputVec = inputVec.normalize();

                // 🐢 SLOWER acceleration
                speed += (0.5 - speed) * 0.005; // Acceleration approaches 0.5 slower

                // 🐌 SLOWER acceleration effect per frame
                speed3d = speed3d.add(acceleration.multiply(0.005));
            } else {
                // 🧊 More subtle deceleration (feels "floaty")
                speed *= 0.98;
                speed3d = speed3d.multiply(0.96);
            }

            // ✋ Optional: Cap max speed
            double maxSpeed = 0.2;
            if (speed3d.length() > maxSpeed) {
                speed3d = speed3d.normalize().multiply(maxSpeed);
            }

            movementDirection = inputVec;
            Vec3d displacement = speed3d;

            // === Apply Movement to Rendering Space ===
            LazuliGeometryBuilder.rotatedSpaceDisplaceRenderingSpacePos(displacement);
        });
    }

    private void registerKeyBindings() {
        // Registering key bindings
        ARROW_UP = new KeyBinding(
                "key.new_horizons.arrow_up",   // Translation key
                GLFW.GLFW_KEY_UP,              // Default key (Up Arrow)
                "category.new_horizons"        // Keybinding category
        );

        ARROW_DOWN = new KeyBinding(
                "key.new_horizons.arrow_down", // Translation key
                GLFW.GLFW_KEY_DOWN,            // Default key (Down Arrow)
                "category.new_horizons"        // Keybinding category
        );

        ARROW_LEFT = new KeyBinding(
                "key.new_horizons.arrow_left", // Translation key
                GLFW.GLFW_KEY_LEFT,            // Default key (Left Arrow)
                "category.new_horizons"        // Keybinding category
        );

        ARROW_RIGHT = new KeyBinding(
                "key.new_horizons.arrow_right",// Translation key
                GLFW.GLFW_KEY_RIGHT,           // Default key (Right Arrow)
                "category.new_horizons"        // Keybinding category
        );

        KeyBindingHelper.registerKeyBinding(ARROW_UP);
        KeyBindingHelper.registerKeyBinding(ARROW_DOWN);
        KeyBindingHelper.registerKeyBinding(ARROW_LEFT);
        KeyBindingHelper.registerKeyBinding(ARROW_RIGHT);
    }
}

package org.copycraftDev.new_horizons.client;

import foundry.veil.Veil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.Lidar.LidarGunScrollHandler;
import org.copycraftDev.new_horizons.Lidar.SpraypaintScrollHandler;
import org.copycraftDev.new_horizons.client.particle.ModParticlesClient;
import org.copycraftDev.new_horizons.client.rendering.CelestialBodyRenderer;
import org.copycraftDev.new_horizons.client.rendering.ModShaders;
import org.copycraftDev.new_horizons.core.bigbang.BigBangClientManager;
import org.copycraftDev.new_horizons.core.entity.BlockColliderEntity;
import org.copycraftDev.new_horizons.core.entity.ModEntities;
import org.copycraftDev.new_horizons.core.items.ModItems;
import org.copycraftDev.new_horizons.core.particle.ModParticles;
import org.copycraftDev.new_horizons.client.render.entity.SeatEntityRenderer;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.copycraftDev.new_horizons.core.blocks.ModBlocks;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliRenderingRegistry;
import org.copycraftDev.new_horizons.physics.PhysicsMain;
import org.copycraftDev.new_horizons.physics.PhysicsRenderer;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;

public class NewHorizonsClient implements ClientModInitializer {
    private static float currentTickDelta = 0.0F;
    static double speed = 0;
    static Vec3d direction = Vec3d.ZERO;
    static Vec3d speed3d = Vec3d.ZERO;
    public static boolean controlling = false;

    public static KeyBinding ARROW_UP;
    public static KeyBinding ARROW_DOWN;
    public static KeyBinding ARROW_LEFT;
    public static KeyBinding ARROW_RIGHT;
    public static KeyBinding FRONT;
    public static KeyBinding RIGHT;
    public static KeyBinding BACK;
    public static KeyBinding LEFT;
    public static KeyBinding YAW_L;
    public static KeyBinding YAW_R;
    public static KeyBinding UP;
    public static KeyBinding DOWN;
    public static KeyBinding PITCH_UP;
    public static KeyBinding PITCH_DOWN;
    public static KeyBinding ROLL_LEFT;
    public static KeyBinding ROLL_RIGHT;

    private static Vec3d movementDirection = Vec3d.ZERO;

    public static void setControlling(boolean x) {
        controlling = x;
    }

    public static boolean getControlling() {
        return controlling;
    }

    @Override
    public void onInitializeClient() {
        ShaderController.loadShader(ModShaders.VOID);

        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SPRAYPAINT, new SprayPaintRenderer());

        SpraypaintScrollHandler handler2 = new SpraypaintScrollHandler();
        handler2.onInitializeClient();

        LidarGunScrollHandler handler = new LidarGunScrollHandler();
        handler.onInitializeClient();

        registerKeyBindings();

        Veil.init();
        PhysicsRenderer.register();
        PhysicsRenderer.register();

        CelestialBodyRenderer.register();
        LazuliRenderingRegistry.registerLazuliRenderPhases();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (controlling) {
                if (client.options.dropKey.isPressed()) {
                    client.options.dropKey.setPressed(false);
                }
                if (client.options.inventoryKey.isPressed()) {
                    client.options.inventoryKey.setPressed(false);
                }
                if (client.currentScreen instanceof InventoryScreen) {
                    client.setScreen(null);
                }
            }
        });

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PRIVACY_GLASS, RenderLayer.getTranslucent());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            BigBangClientManager.tick(client.world);
        });

        ParticleFactoryRegistry.getInstance().register(ModParticles.FOG_PARTICLE, ModParticlesClient.FogParticle.Factory::new);
        EntityRendererRegistry.register(ModEntities.SEAT_ENTITY, SeatEntityRenderer::new);

        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || !controlling) return;

            var player = client.player;
            if (player.hasVehicle()) {
                double playerPitch = Math.toRadians(player.getYaw());
                double playerYaw = -Math.toRadians(player.getPitch());
                LazuliGeometryBuilder.setRenderingSpaceDir(playerPitch, 0, playerYaw);
            }

            Vec3d inputVec = Vec3d.ZERO;
            Vec3d rotationInput = Vec3d.ZERO;

            if (FRONT.isPressed()) inputVec = inputVec.add(0, 0, 1);
            if (BACK.isPressed()) inputVec = inputVec.add(0, 0, -1);
            if (RIGHT.isPressed()) inputVec = inputVec.add(1, 0, 0);
            if (LEFT.isPressed()) inputVec = inputVec.add(-1, 0, 0);
            if (UP.isPressed()) inputVec = inputVec.add(0, 1, 0);
            if (DOWN.isPressed()) inputVec = inputVec.add(0, -1, 0);

            if (YAW_L.isPressed()) rotationInput = rotationInput.add(0, 1, 0);
            if (YAW_R.isPressed()) rotationInput = rotationInput.add(0, -1, 0);
            if (PITCH_UP.isPressed()) rotationInput = rotationInput.add(1, 0, 0);
            if (PITCH_DOWN.isPressed()) rotationInput = rotationInput.add(-1, 0, 0);
            if (ROLL_LEFT.isPressed()) rotationInput = rotationInput.add(0, 0, 1);
            if (ROLL_RIGHT.isPressed()) rotationInput = rotationInput.add(0, 0, -1);

            if (inputVec.lengthSquared() > 0) {
                inputVec = inputVec.normalize();
                speed += (0.5 - speed) * 0.005;
                speed3d = speed3d.add(inputVec.multiply(0.005));
            } else {
                speed *= 0.98;
                speed3d = speed3d.multiply(0.96);
            }

            double maxSpeed = 0.2;
            if (speed3d.length() > maxSpeed) {
                speed3d = speed3d.normalize().multiply(maxSpeed);
            }

            movementDirection = inputVec;
            Vec3d displacement = speed3d;
            LazuliGeometryBuilder.rotatedSpaceDisplaceRenderingSpacePos(displacement);

            Collection<PhysicsMain.PhysicsObject> allObjects = PhysicsMain.PHYSICS_MANAGER.getAllObjects();
            if (!allObjects.isEmpty()) {
                PhysicsMain.PhysicsObject activeShip = allObjects.iterator().next();
                activeShip.addVelocity(displacement);
                if (rotationInput.lengthSquared() > 0) {
                    activeShip.addRotation(rotationInput.multiply(0.5));
                }
            }
        });

        EntityRendererRegistry.register(ModEntities.BLOCK_COLLIDER,
                ctx -> new EntityRenderer<BlockColliderEntity>(ctx) {
                    @Override
                    public void render(BlockColliderEntity entity, float yaw, float tickDelta,
                                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {}
                    @Override
                    public Identifier getTexture(BlockColliderEntity entity) { return null; }
                });
    }

    private void registerKeyBindings() {
        ARROW_UP = new KeyBinding("key.new_horizons.arrow_up", GLFW.GLFW_KEY_UP, "category.new_horizons");
        ARROW_DOWN = new KeyBinding("key.new_horizons.arrow_down", GLFW.GLFW_KEY_DOWN, "category.new_horizons");
        ARROW_LEFT = new KeyBinding("key.new_horizons.arrow_left", GLFW.GLFW_KEY_LEFT, "category.new_horizons");
        ARROW_RIGHT = new KeyBinding("key.new_horizons.arrow_right", GLFW.GLFW_KEY_RIGHT, "category.new_horizons");
        FRONT = new KeyBinding("key.new_horizons.w", GLFW.GLFW_KEY_W, "category.new_horizons");
        RIGHT = new KeyBinding("key.new_horizons.d", GLFW.GLFW_KEY_D, "category.new_horizons");
        BACK = new KeyBinding("key.new_horizons.s", GLFW.GLFW_KEY_S, "category.new_horizons");
        LEFT = new KeyBinding("key.new_horizons.a", GLFW.GLFW_KEY_A, "category.new_horizons");
        YAW_L = new KeyBinding("key.new_horizons.q", GLFW.GLFW_KEY_Q, "category.new_horizons");
        YAW_R = new KeyBinding("key.new_horizons.e", GLFW.GLFW_KEY_E, "category.new_horizons");
        UP = new KeyBinding("key.new_horizons.up", GLFW.GLFW_KEY_KP_9, "category.new_horizons");
        DOWN = new KeyBinding("key.new_horizons.down", GLFW.GLFW_KEY_KP_3, "category.new_horizons");
        PITCH_UP = new KeyBinding("key.new_horizons.pitch_up", GLFW.GLFW_KEY_KP_8, "category.new_horizons");
        PITCH_DOWN = new KeyBinding("key.new_horizons.pitch_down", GLFW.GLFW_KEY_KP_2, "category.new_horizons");
        ROLL_LEFT = new KeyBinding("key.new_horizons.roll_left", GLFW.GLFW_KEY_KP_4, "category.new_horizons");
        ROLL_RIGHT = new KeyBinding("key.new_horizons.roll_right", GLFW.GLFW_KEY_KP_6, "category.new_horizons");

        KeyBindingHelper.registerKeyBinding(ARROW_UP);
        KeyBindingHelper.registerKeyBinding(ARROW_DOWN);
        KeyBindingHelper.registerKeyBinding(ARROW_LEFT);
        KeyBindingHelper.registerKeyBinding(ARROW_RIGHT);
        KeyBindingHelper.registerKeyBinding(FRONT);
        KeyBindingHelper.registerKeyBinding(RIGHT);
        KeyBindingHelper.registerKeyBinding(BACK);
        KeyBindingHelper.registerKeyBinding(LEFT);
        KeyBindingHelper.registerKeyBinding(YAW_L);
        KeyBindingHelper.registerKeyBinding(YAW_R);
        KeyBindingHelper.registerKeyBinding(UP);
        KeyBindingHelper.registerKeyBinding(DOWN);
        KeyBindingHelper.registerKeyBinding(PITCH_UP);
        KeyBindingHelper.registerKeyBinding(PITCH_DOWN);
        KeyBindingHelper.registerKeyBinding(ROLL_LEFT);
        KeyBindingHelper.registerKeyBinding(ROLL_RIGHT);
    }
}

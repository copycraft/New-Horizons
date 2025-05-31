package org.copycraftDev.new_horizons;

import com.mojang.brigadier.CommandDispatcher;
import foundry.veil.Veil;
import nazario.liby.api.registry.auto.LibyEntrypoints;
import nazario.liby.api.registry.auto.LibyRegistryLoader;
import nazario.liby.api.registry.runtime.recipe.LibyIngredient;
import nazario.liby.api.registry.runtime.recipe.LibyRecipeRegistry;
import nazario.liby.api.registry.runtime.recipe.types.LibyShapelessCraftingRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.client.planets.MeteorCommand;
import org.copycraftDev.new_horizons.client.planets.MeteorScheduler;
import org.copycraftDev.new_horizons.core.bigbang.BigBangCutsceneManager;
import org.copycraftDev.new_horizons.core.blocks.ModBlocks;
import org.copycraftDev.new_horizons.core.entity.ModEntities;
import org.copycraftDev.new_horizons.core.entity.BlockColliderEntity;
import org.copycraftDev.new_horizons.physics.PhysicsMain;
import org.copycraftDev.new_horizons.physics.PhysicsRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewHorizonsMain implements ModInitializer {

    public static final String MOD_ID = "new_horizons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final SoundEvent ENGINE_AMBIANCE = register("engine_ambiance");
    public static final SoundEvent ENGINE_BROKEN   = register("engine_broken");
    public static final SoundEvent ENGINE_POWERUP  = register("engine_powerup");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.of("new_horizons", name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }


    @Override
    public void onInitialize() {
        // Intercept placements on blocks adjacent to your collider
        UseBlockCallback.EVENT.register(this::onUseBlock);

        // Intercept right-click on the collider entity itself
        UseEntityCallback.EVENT.register(this::onUseEntity);
        BlockColliderEntity.createAttributes();
        ModEntities.registerAttributes();
        LibyRegistryLoader.load("org.copycraftDev.new_horizons", LOGGER, LibyEntrypoints.MAIN);
        Veil.init();
        ServerTickEvents.END_SERVER_TICK.register(MeteorScheduler::onServerTick);
        ServerTickEvents.END_SERVER_TICK.register(PhysicsMain.PhysicsManager::tickAll);

        PhysicsRenderer.register();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
            MeteorCommand.register(dispatcher);  // Register the custom command
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            server.execute(() -> {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    BigBangCutsceneManager.execute(player);
                }
            });
        });
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClient) return ActionResult.PASS;

            // the exact click‐position as a Vec3d
            Vec3d clickPos = hit.getPos();

            for (PhysicsMain.PhysicsObject obj : PhysicsMain.PHYSICS_MANAGER.getAllObjects()) {
                // is the click on the surface of this moving object?
                if (obj.getWorldBounds().expand(1e-6).contains(clickPos)) {
                    // wrap the usage into a placement context
                    ItemUsageContext usageCtx = new ItemUsageContext(player, hand, hit);
                    ItemPlacementContext placeCtx = new ItemPlacementContext(usageCtx);

                    // ask the context what BlockState it would place
                    BlockState toPlace = placeCtx.getWorld().getBlockState(BlockPos.ofFloored(clickPos));
                    if (toPlace != null) {
                        // compute local coordinate relative to this object's origin
                        BlockPos base = new BlockPos(
                                (int)Math.floor(obj.getPosition().x),
                                (int)Math.floor(obj.getPosition().y),
                                (int)Math.floor(obj.getPosition().z)
                        );
                        BlockPos local = hit.getBlockPos().subtract(base);

                        // attach into the physics object
                        obj.addBlock(local, toPlace, null);

                        // consume one item if not in creative
                        if (!player.isCreative()) {
                            player.getStackInHand(hand).decrement(1);
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }

            return ActionResult.PASS;
        });
        createRecipes();


    }
    public void createRecipes() {
        LibyRecipeRegistry.addRecipe(
                new LibyShapelessCraftingRecipe(
                        Identifier.of(MOD_ID, "wood_top_planks"),
                        new LibyIngredient[]{
                                LibyIngredient.createItem(ModBlocks.REDWOOD_LOGS)
                        },
                        ModBlocks.REDWOOD_PLANKS.liby$getId(),
                        4
                )
        );
    }
    private ActionResult onUseBlock(
            PlayerEntity player,
            World world,
            Hand hand,
            BlockHitResult hit
    ) {
        if (world.isClient) return ActionResult.PASS;

        // Find any collider one block out in the clicked direction
        BlockPos clicked = hit.getBlockPos();
        Direction face = hit.getSide();
        Box checkBox = new Box(clicked.offset(face));
        var list = world.getEntitiesByClass(BlockColliderEntity.class, checkBox, e -> true);
        if (list.isEmpty()) return ActionResult.PASS;

        return interceptPlacement(player, hand, world, list.get(0), face, clicked);
    }

    private ActionResult onUseEntity(
            PlayerEntity player,
            World world,
            Hand hand,
            net.minecraft.entity.Entity entity,
            EntityHitResult hitResult
    ) {
        if (world.isClient) return ActionResult.PASS;
        if (!(entity instanceof BlockColliderEntity collider)) return ActionResult.PASS;
        if (hitResult == null) return ActionResult.PASS;

        // Determine which face was clicked via the hit vector
        Vec3d hitPos = hitResult.getPos();
        Direction face = getClickedFace(collider.calculateBoundingBox(), hitPos);
        BlockPos basePos = collider.getBlockPos();

        return interceptPlacement(player, hand, world, collider, face, basePos);
    }

    private ActionResult interceptPlacement(
            PlayerEntity player,
            Hand hand,
            World world,
            BlockColliderEntity collider,
            Direction face,
            BlockPos basePos
    ) {
        ItemStack stack = player.getStackInHand(hand);
        if (!(stack.getItem() instanceof BlockItem bi)) return ActionResult.PASS;

        BlockState toPlace = bi.getBlock().getDefaultState();
        BlockPos placePos = basePos.offset(face);

        onBlockPlacementIntercepted(toPlace, placePos, player, world);
        return ActionResult.SUCCESS;
    }

    private void onBlockPlacementIntercepted(
            BlockState state,
            BlockPos pos,
            PlayerEntity player,
            World world
    ) {
        // Your custom logic here:
        System.out.println("Intercepted placement of "
                + state.getBlock().getTranslationKey()
                + " at " + pos
        );
    }

    private static Direction getClickedFace(Box box, Vec3d hitVec) {
        double x = hitVec.x, y = hitVec.y, z = hitVec.z;
        double dxMin = Math.abs(x - box.minX), dxMax = Math.abs(x - box.maxX);
        double dyMin = Math.abs(y - box.minY), dyMax = Math.abs(y - box.maxY);
        double dzMin = Math.abs(z - box.minZ), dzMax = Math.abs(z - box.maxZ);

        double min = dxMin; Direction face = Direction.WEST;
        if (dxMax < min) { min = dxMax; face = Direction.EAST; }
        if (dyMin < min) { min = dyMin; face = Direction.DOWN; }
        if (dyMax < min) { min = dyMax; face = Direction.UP; }
        if (dzMin < min) { min = dzMin; face = Direction.NORTH; }
        if (dzMax < min) { face = Direction.SOUTH; }
        return face;
    }


    public static Identifier id(String name){
        return Identifier.of(MOD_ID, name);
    }
}



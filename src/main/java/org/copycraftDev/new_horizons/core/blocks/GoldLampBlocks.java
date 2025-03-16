package org.copycraftDev.new_horizons.core.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.core.render.LampBlockRenderer;

import java.util.HashMap;
import java.util.Map;

public class GoldLampBlocks extends Block {
    public static final BooleanProperty LIT = Properties.LIT;
    private static final Map<BlockPos, LampBlockRenderer> lampRenderers = new HashMap<>();  // Map to track renderers by position

    public GoldLampBlocks() {
        super(FabricBlockSettings.of().mapColor(MapColor.GOLD).strength(3.0F));  // No luminance here
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(LIT, false);
    }

    // Method to handle redstone signal
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);

        // Check the redstone power from neighbors
        int power = world.getReceivedRedstonePower(pos);

        boolean isLit = state.get(LIT);
        boolean shouldBeLit = power > 0;

        if (shouldBeLit && !isLit) {
            // Turn on the lamp and light
            world.setBlockState(pos, state.with(LIT, true), Block.NOTIFY_ALL);
            toggleLamp(pos, true);
        } else if (!shouldBeLit && isLit) {
            // Turn off the lamp and remove the light
            world.setBlockState(pos, state.with(LIT, false), Block.NOTIFY_ALL);
            toggleLamp(pos, false);
        }
    }

    // Toggle the lamp's light on or off based on the redstone signal
    // Toggle the lamp's light on or off based on the redstone signal
    private void toggleLamp(BlockPos pos, boolean turnOn) {
        LampBlockRenderer lampRenderer = lampRenderers.get(pos);  // Get the renderer for the current lamp position
        if (lampRenderer == null) {
            // Create a new LampBlockRenderer if it doesn't exist, using the correct position
            lampRenderer = new LampBlockRenderer(pos);  // Initialize with the correct block position
            lampRenderers.put(pos, lampRenderer);  // Store the renderer in the map
        }

        // Always schedule the toggle action to run on the main thread (render thread)
        final LampBlockRenderer finalLampRenderer = lampRenderer;  // Make the lampRenderer final
        MinecraftClient.getInstance().execute(() -> {
            if (turnOn) {
                finalLampRenderer.toggle();  // Turn on the lamp
            } else {
                finalLampRenderer.toggle();  // Turn off the lamp
            }
        });
    }


    // This method will handle block interactions (player right-click)
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            boolean isLit = state.get(LIT);
            world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_ALL);

            LampBlockRenderer lampRenderer = lampRenderers.get(pos);  // Get the renderer for the current lamp position
            if (lampRenderer == null) {
                // Create a new LampBlockRenderer and pass the position of the block
                lampRenderer = new LampBlockRenderer(pos);  // Pass the position of the lamp block
                lampRenderers.put(pos, lampRenderer);  // Store the renderer in the map
            } else {
                // Update the position of the lamp renderer
                lampRenderer.setLampPosition(pos);  // Update the lamp's position to the current block's position
            }

            if (isLit) {
                // Turn off the lamp and remove the light
                lampRenderer.toggle();  // Turns off the light if it's on
            } else {
                // Turn on the lamp and add the light
                lampRenderer.toggle();  // Turns on the light if it's off
            }
        }
        return ActionResult.SUCCESS;
    }
}

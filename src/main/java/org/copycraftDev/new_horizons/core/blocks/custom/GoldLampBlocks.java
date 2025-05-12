package org.copycraftDev.new_horizons.core.blocks.custom;

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
    private static final Map<BlockPos, LampBlockRenderer> lampRenderers = new HashMap<>();

    public GoldLampBlocks() {
        super(FabricBlockSettings.of()
                .mapColor(MapColor.GOLD)
                .strength(3.0F)
                .luminance(state -> state.get(LIT) ? 15 : 0));
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

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);

        boolean powered = world.isReceivingRedstonePower(pos);
        boolean isLit = state.get(LIT);

        if (powered && !isLit) {
            world.setBlockState(pos, state.with(LIT, true), Block.NOTIFY_ALL);
            toggleLamp(pos, true);
        } else if (!powered && isLit) {
            world.setBlockState(pos, state.with(LIT, false), Block.NOTIFY_ALL);
            toggleLamp(pos, false);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            removeLamp(pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private void toggleLamp(BlockPos pos, boolean turnOn) {
        LampBlockRenderer lampRenderer = lampRenderers.get(pos);

        if (lampRenderer == null) {
            lampRenderer = new LampBlockRenderer(pos);
            lampRenderers.put(pos, lampRenderer);
        }

        final LampBlockRenderer finalLampRenderer = lampRenderer;
        MinecraftClient.getInstance().execute(() -> {
            if (turnOn) {
                finalLampRenderer.addLight();
            } else {
                finalLampRenderer.removeLight();
            }
        });
    }

    private void removeLamp(BlockPos pos) {
        LampBlockRenderer lampRenderer = lampRenderers.remove(pos);
        if (lampRenderer != null) {
            MinecraftClient.getInstance().execute(lampRenderer::removeLight);
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            boolean isLit = state.get(LIT);
            world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_ALL);

            LampBlockRenderer lampRenderer = lampRenderers.computeIfAbsent(pos, LampBlockRenderer::new);

            if (isLit) {
                lampRenderer.removeLight();
            } else {
                lampRenderer.addLight();
            }
        }
        return ActionResult.SUCCESS;
    }
}

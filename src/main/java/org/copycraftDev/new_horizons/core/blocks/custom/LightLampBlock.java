package org.copycraftDev.new_horizons.core.blocks.custom;

import com.google.common.collect.Maps;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.PointLight;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Map;

public class LightLampBlock extends Block {

    public static final BooleanProperty LIGHT_ON = BooleanProperty.of("light_on");
    // A map to store active PointLight instances keyed by their block position.
    private static final Map<BlockPos, PointLight> ACTIVE_LIGHTS = Maps.newHashMap();

    public LightLampBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(LIGHT_ON, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIGHT_ON);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Place with the light off by default.
        return this.getDefaultState().with(LIGHT_ON, false);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              net.minecraft.entity.player.PlayerEntity player,
                              Hand hand, BlockHitResult hit) {
        // Perform the toggle only on the client side since the light is client-side.
        if (world.isClient) {
            boolean currentlyOn = state.get(LIGHT_ON);
            boolean newState = !currentlyOn;
            world.setBlockState(pos, state.with(LIGHT_ON, newState));

            if (newState) {
                // Create and configure a new PointLight
                PointLight pointLight = new PointLight();
                pointLight.setBrightness(1.5f);
                pointLight.setColor(1.0f, 0.9f, 0.647f);
                // Position the light at the center of the block.
                pointLight.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                // Add it to the renderer.
                VeilRenderSystem.renderer().getLightRenderer().addLight(pointLight);
                ACTIVE_LIGHTS.put(pos.toImmutable(), pointLight);
            } else {
                // Remove the point light if it exists.
                PointLight pointLight = ACTIVE_LIGHTS.remove(pos.toImmutable());
                if (pointLight != null) {
                    VeilRenderSystem.renderer().getLightRenderer().removeLight(pointLight);
                }
            }
        }
        return ActionResult.SUCCESS;
    }
}

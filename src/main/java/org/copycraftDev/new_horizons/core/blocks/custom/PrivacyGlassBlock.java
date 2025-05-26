package org.copycraftDev.new_horizons.core.blocks.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;


public class PrivacyGlassBlock extends TransparentBlock {
    public static final BooleanProperty PRIVACY = BooleanProperty.of("privacy");

    public PrivacyGlassBlock() {
        super(FabricBlockSettings
                .of()
                .strength(0.3f)
                .sounds(BlockSoundGroup.GLASS)
                .nonOpaque()   // allow transparency
                .requiresTool());


        // default to clear
        this.setDefaultState(this.stateManager.getDefaultState().with(PRIVACY, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PRIVACY);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            boolean current = state.get(PRIVACY);
            BlockState newState = state.with(PRIVACY, !current);
            world.setBlockState(pos, newState, 3);

            // Sound and particles
            SoundEvent sound = current ? SoundEvents.BLOCK_GLASS_BREAK : SoundEvents.BLOCK_GLASS_PLACE;
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, 0.5f, 1.2f);
            ((ServerWorld) world).spawnParticles(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    5, 0.2, 0.2, 0.2, 0.01);

            // Propagate
            world.scheduleBlockTick(pos, this, 1);
        }
        return ActionResult.SUCCESS;
    }
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean turningOn = state.get(PRIVACY);

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof PrivacyGlassBlock && neighborState.get(PRIVACY) != turningOn) {
                BlockState toggled = neighborState.with(PRIVACY, turningOn);
                world.setBlockState(neighborPos, toggled, 3);

                // Sound and particles
                SoundEvent sound = turningOn ? SoundEvents.BLOCK_GLASS_PLACE : SoundEvents.BLOCK_GLASS_BREAK;
                world.playSound(null, neighborPos, sound, SoundCategory.BLOCKS, 0.5f, 1.2f);
                world.spawnParticles(ParticleTypes.ENCHANT,
                        neighborPos.getX() + 0.5, neighborPos.getY() + 0.5, neighborPos.getZ() + 0.5,
                        5, 0.2, 0.2, 0.2, 0.01);

                // Propagate
                int delay = 1;// + world.getRandom().nextInt(3);
                world.scheduleBlockTick(neighborPos, this, delay);
            }
        }
    }
}

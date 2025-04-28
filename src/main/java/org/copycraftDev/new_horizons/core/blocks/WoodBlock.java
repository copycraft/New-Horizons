package org.copycraftDev.new_horizons.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.block.AbstractBlock;

public class WoodBlock extends Block {
    public WoodBlock() {
        super(AbstractBlock.Settings.create()

                .sounds(BlockSoundGroup.WOOD) // Wood sound group
                .requiresTool()); // Optionally require tool to harvest)
    }
}

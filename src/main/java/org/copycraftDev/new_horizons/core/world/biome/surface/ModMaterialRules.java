package org.copycraftDev.new_horizons.core.world.biome.surface;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.copycraftDev.new_horizons.core.world.biome.ModBiomes;

public class ModMaterialRules {
    // Define material rules for specific blocks
    private static final MaterialRules.MaterialRule DIRT = makeStateRule(Blocks.DIRT);
    private static final MaterialRules.MaterialRule GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final MaterialRules.MaterialRule CALCITE = makeStateRule(Blocks.CALCITE);
    private static final MaterialRules.MaterialRule NETHERRACK = makeStateRule(Blocks.NETHERRACK);

    public static MaterialRules.MaterialRule makeRules() {
        // Condition to check if the block is at or above water level
        MaterialRules.MaterialCondition isAtOrAboveWaterLevel = MaterialRules.water(-1, 0);

        // Default surface rule: grass on top, dirt below
        MaterialRules.MaterialRule defaultSurface = MaterialRules.sequence(
                MaterialRules.condition(isAtOrAboveWaterLevel, GRASS_BLOCK),
                DIRT
        );

        // Venus-specific surface rule: calcite on top, netherrack below
        MaterialRules.MaterialRule venusSurface = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.biome(ModBiomes.VENUS),
                        MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, CALCITE)),
                MaterialRules.condition(MaterialRules.STONE_DEPTH_CEILING, NETHERRACK)
        );

        // Combine Venus-specific rules with the default surface rule
        return MaterialRules.sequence(
                venusSurface,
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, defaultSurface)
        );
    }

    // Helper method to create a material rule for a specific block
    private static MaterialRules.MaterialRule makeStateRule(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }
}
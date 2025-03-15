package org.copycraftDev.new_horizons.core.blocks;

import nazario.liby.registry.auto.LibyAutoRegister;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static org.copycraftDev.new_horizons.New_horizons.MOD_ID;

@LibyAutoRegister
public class ModBlocks {
    private ModBlocks() {}

    // Instances of our new blocks
    public static final Block CAPTAINS_CHAIR = register("captains_chair", new CaptainsChairBlock(AbstractBlock.Settings.create()));
    public static final Block LIGHT_LAMP = register("light_lamp", new LightLampBlock(AbstractBlock.Settings.create()));

    private static Block register(String path, Block block) {
        Identifier id = Identifier.of(MOD_ID, path);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize() {

    }
}

package org.copycraftDev.new_horizons.core.blocks;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.NewHorizonsMain;

@LibyAutoRegister(method = "initialize")
public class ModBlocks {
    private ModBlocks() {}

    // Instances of our new blocks
    public static final Block CAPTAINS_CHAIR = register("captains_chair", new CaptainsChairBlock(AbstractBlock.Settings.create()));
    public static final Block GOLD_LAMP = register("gold_lamp", new GoldLampBlock());
    public static final Block GOLD_TILE_LAMP = register("gold_tile_lamp", new GoldTileLampBlock());
    public static final Block GOLD_FLOWER_LAMP = register("gold_flower_lamp", new GoldFlowerLampBlock());
    public static final Block PRIVACY_GLASS = register("privacy_glass", new PrivacyGlassBlock());

    private static Block register(String path, Block block) {
        Identifier id = Identifier.of(NewHorizonsMain.MOD_ID, path);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize() {

    }
}

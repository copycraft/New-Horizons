package org.copycraftDev.new_horizons.core.blocks;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.NewHorizonsMain;
import org.copycraftDev.new_horizons.core.blocks.custom.*;
import org.copycraftDev.new_horizons.physics.block.AssemblerBlock;

@LibyAutoRegister(method = "initialize")
public class ModBlocks {
    private ModBlocks() {}

    // Instances of our new blocks
    public static final Block CAPTAINS_CHAIR = register("captains_chair", new CaptainsChairBlock(AbstractBlock.Settings.create()));
    public static final Block GOLD_LAMP = register("gold_lamp", new GoldLampBlock());
    public static final Block GOLD_TILE_LAMP = register("gold_tile_lamp", new GoldTileLampBlock());
    public static final Block GOLD_FLOWER_LAMP = register("gold_flower_lamp", new GoldFlowerLampBlock());
    public static final Block PRIVACY_GLASS = register("privacy_glass", new PrivacyGlassBlock());
    public static final Block ASSEMBLER_BLOCK = register("assembler_block", new AssemblerBlock());

    public static final Block REDWOOD_LOGS = register("redwood_logs", new WoodBlock());
    public static final Block REDWOOD_PLANKS = register("redwood_planks", new WoodBlock());
    public static final Block REDWOOD_LOG_STRIPPED = register("redwood_logs_stripped", new WoodBlock());

    public static final Block LIDAR_SCANNER = register("lidar_scanner_block", new LidarScannerBlock());

    private static Block register(String path, Block block) {
        Identifier id = Identifier.of(NewHorizonsMain.MOD_ID, path);
        // register the Block
        Registry.register(Registries.BLOCK, id, block);
        // register the BlockItem without grouping
        Registry.register(
                Registries.ITEM,
                id,
                new BlockItem(block, new Item.Settings())
        );
        return block;
    }

    public static void initialize() {
        // Inject our blocks into the custom ItemGroup after registration
        Identifier groupId = Identifier.of(NewHorizonsMain.MOD_ID, "new_horizons_group");
        RegistryKey<ItemGroup> groupKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), groupId);
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> {
            entries.add(CAPTAINS_CHAIR.asItem());
            entries.add(GOLD_LAMP.asItem());
            entries.add(GOLD_TILE_LAMP.asItem());
            entries.add(GOLD_FLOWER_LAMP.asItem());
            entries.add(PRIVACY_GLASS.asItem());
            entries.add(REDWOOD_LOGS.asItem());
            entries.add(REDWOOD_PLANKS.asItem());
            entries.add(REDWOOD_LOG_STRIPPED.asItem());
            entries.add(ASSEMBLER_BLOCK.asItem());
            entries.add(LIDAR_SCANNER.asItem());
        });
    }
}

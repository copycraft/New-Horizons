package org.copycraftDev.new_horizons.core.blocks.entity;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.NewHorizonsMain;
import org.copycraftDev.new_horizons.core.blocks.ModBlocks;
import org.copycraftDev.new_horizons.core.portal.PortalBlockEntity;

@LibyAutoRegister(method = "register")
public class ModBlockEntities {
    public static BlockEntityType<PortalBlockEntity> PORTAL_BE;

    public static void register() {
        PORTAL_BE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(NewHorizonsMain.MOD_ID, "portal_be"),
                FabricBlockEntityTypeBuilder
                        .create(PortalBlockEntity::new, ModBlocks.PORTAL_BLOCK)
                        .build(null)
        );
    }
}
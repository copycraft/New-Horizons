package org.copycraftDev.new_horizons.core.entity;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import org.copycraftDev.new_horizons.NewHorizonsMain;

@LibyAutoRegister(method = "initialize")
public class ModEntities {
    public static final EntityType<SeatEntity> SEAT_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(NewHorizonsMain.MOD_ID, "seat_entity"),
            FabricEntityTypeBuilder.<SeatEntity>create(SpawnGroup.MISC, SeatEntity::new)
                    .dimensions(EntityDimensions.fixed(0.1f, 0.1f)) // Small hitbox
                    .trackRangeBlocks(10) // Important for syncing
                    .trackedUpdateRate(1)  // Important for data syncing
                    .build()
    );

    public static void initialize() {}
}

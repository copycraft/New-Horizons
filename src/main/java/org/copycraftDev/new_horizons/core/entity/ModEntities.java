package org.copycraftDev.new_horizons.core.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import org.copycraftDev.new_horizons.NewHorizonsMain;

public class ModEntities {
    public static final EntityType<SeatEntity> SEAT_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(NewHorizonsMain.MOD_ID, "seat_entity"),
            FabricEntityTypeBuilder.<SeatEntity>create()
                    .dimensions(EntityDimensions.fixed(0.1f, 0.1f))
                    .build()
    );
    public static void initialize() {}
}

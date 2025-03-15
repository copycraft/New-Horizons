package org.copycraftDev.new_horizons.core.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import static org.copycraftDev.new_horizons.New_horizons.MOD_ID;

public class ModEntities {
    public static final EntityType<SeatEntity> SEAT_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "seat_entity"),
            FabricEntityTypeBuilder.<SeatEntity>create()
                    .dimensions(EntityDimensions.fixed(0.1f, 0.1f))
                    .build()
    );


    public static void initialize() {}
}

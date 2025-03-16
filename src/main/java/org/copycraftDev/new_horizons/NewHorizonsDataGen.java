package org.copycraftDev.new_horizons;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import org.copycraftDev.new_horizons.core.world.biome.ModBiomes;
import org.copycraftDev.new_horizons.core.world.dimension.ModDimensions;

public class NewHorizonsDataGen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        // Add any additional data providers here if needed.
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        // This registers all dynamic dimension types so JSON is generated.
        registryBuilder.addRegistry(RegistryKeys.DIMENSION_TYPE, ModDimensions::bootstrapType);
        registryBuilder.addRegistry(RegistryKeys.BIOME, ModBiomes::boostrap);
    }
}


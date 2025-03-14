package org.copycraftDev.new_horizons.core.world.dimension;

import qouteall.dimlib.api.DimensionAPI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.copycraftDev.new_horizons.New_horizons;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public class ModDimensions {
    // List of dynamic dimension names.
    private static final List<String> DIMENSION_NAMES = List.of("deep_void");

    /**
     * This method is intended for use during data generation.
     * It dynamically registers each custom dimension type, outputting JSON files.
     *
     * If you don’t need JSON output (data generation), you can omit calling this.
     */
    public static void bootstrapTypes(Registerable<DimensionType> context) {
        for (String name : DIMENSION_NAMES) {
            RegistryKey<DimensionType> typeKey = RegistryKey.of(
                    RegistryKeys.DIMENSION_TYPE, Identifier.of(New_horizons.MOD_ID, name + "_type"));
            context.register(typeKey, new DimensionType(
                    OptionalLong.of(12000), // fixedTime
                    false,                  // hasSkylight
                    false,                  // hasCeiling
                    false,                  // ultraWarm
                    false,                  // natural
                    1.0,                    // coordinateScale
                    false,                  // bedWorks
                    false,                  // respawnAnchorWorks
                    0,                      // minY
                    1024,                   // height
                    1024,                   // logicalHeight
                    BlockTags.INFINIBURN_OVERWORLD, // infiniburn tag
                    DimensionTypes.OVERWORLD_ID,      // effects location
                    0.0f,                   // ambient light
                    new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 0), 0)
            ));
        }
    }

    /**
     * This method registers dimensions at runtime.
     * It dynamically creates DimensionOptions using the registered DimensionType and a chunk generator.
     * Call this during server startup when a MinecraftServer instance is available.
     */
    public static void registerDimensions(MinecraftServer server, Registerable<?> registryContext) {
        for (String name : DIMENSION_NAMES) {
            Identifier dimensionId = Identifier.of(New_horizons.MOD_ID, name);
            // Build a RegistryKey for the corresponding dimension type.
            RegistryKey<DimensionType> typeKey = RegistryKey.of(
                    RegistryKeys.DIMENSION_TYPE, Identifier.of(New_horizons.MOD_ID, name + "_type"));

            // Create DimensionOptions (which pairs a DimensionType with a ChunkGenerator)
            DimensionOptions options = createDimensionOptions(registryContext, typeKey);

            // Register the dimension using DimensionAPI.
            DimensionAPI.addDimension(server, dimensionId, options);

            System.out.println("Registered dimension: " + name);
        }
    }

    /**
     * Creates a DimensionOptions instance for the given dimension type key.
     * Uses a simple flat chunk generator based on the plains biome.
     */
    private static DimensionOptions createDimensionOptions(Registerable<?> context, RegistryKey<DimensionType> typeKey) {
        // Retrieve the DimensionType entry.
        RegistryEntryLookup<DimensionType> typeLookup = context.getRegistryLookup(RegistryKeys.DIMENSION_TYPE);
        var dimensionTypeEntry = typeLookup.getOrThrow(typeKey);

        // Create a chunk generator. Here we use a flat generator based on the plains biome.
        RegistryEntryLookup<Biome> biomeLookup = context.getRegistryLookup(RegistryKeys.BIOME);
        RegistryKey<Biome> plainsKey = RegistryKey.of(RegistryKeys.BIOME, Identifier.of("minecraft", "plains"));
        var plainsBiome = biomeLookup.getOrThrow(plainsKey);
        ChunkGenerator generator = new FlatChunkGenerator(new FlatChunkGeneratorConfig(Optional.empty(), plainsBiome, List.of()));

        return new DimensionOptions(dimensionTypeEntry, generator);
    }
}

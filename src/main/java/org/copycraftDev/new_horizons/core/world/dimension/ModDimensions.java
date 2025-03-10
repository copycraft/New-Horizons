package org.copycraftDev.new_horizons.core.world.dimension;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.copycraftDev.new_horizons.New_horizons;

import java.util.OptionalLong;

public class ModDimensions {
    public static final RegistryKey<DimensionOptions> SPACE_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            Identifier.of(New_horizons.MOD_ID, "space"));
    public static final RegistryKey<World> SPACE_LEVEL_KEY = RegistryKey.of(RegistryKeys.WORLD,
            Identifier.of(New_horizons.MOD_ID, "space"));
    public static final RegistryKey<DimensionType> SPACE_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            Identifier.of(New_horizons.MOD_ID, "space_type"));

    public static void bootstrapType(Registerable<DimensionType> context) {
        context.register(SPACE_TYPE, new DimensionType(
                OptionalLong.of(12000), // fixedTime
                false, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                1.0, // coordinateScale
                false, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                1024, // height
                1024, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                DimensionTypes.OVERWORLD_ID, // effectsLocation
                0.0f, // ambientLight
                new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 0), 0)));
    }
}
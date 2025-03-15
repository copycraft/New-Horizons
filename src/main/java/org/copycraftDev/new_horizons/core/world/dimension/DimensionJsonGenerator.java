package org.copycraftDev.new_horizons.core.world.dimension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.copycraftDev.new_horizons.NewHorizonsMain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DimensionJsonGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Generates a dimension JSON file inside `resources/data/new_horizons/dimension/`
     * and `resources/data/new_horizons/dimension_type/` at runtime.
     */
    public static void createDimensionJson(String modId, String dimensionName) {
        // Get the `resources/data/new_horizons/` directory
        Path resourcesPath = FabricLoader.getInstance().getGameDir().resolve("src/main/resources/data/" + modId);

        // Define the paths for dimension and dimension_type JSONs
        File dimensionDir = resourcesPath.resolve("dimension").toFile();
        File dimensionTypeDir = resourcesPath.resolve("dimension_type").toFile();

        // Ensure directories exist
        if (!dimensionDir.exists()) {
            dimensionDir.mkdirs();
        }
        if (!dimensionTypeDir.exists()) {
            dimensionTypeDir.mkdirs();
        }

        // Create file references
        File dimensionFile = new File(dimensionDir, dimensionName + ".json");
        File dimensionTypeFile = new File(dimensionTypeDir, dimensionName + "_type.json");

        // Generate files if they don't already exist
        if (!dimensionFile.exists()) {
            writeJsonToFile(dimensionFile, getDimensionJson(modId, dimensionName));
        }
        if (!dimensionTypeFile.exists()) {
            writeJsonToFile(dimensionTypeFile, getDimensionTypeJson(modId, dimensionName));
        }
    }

    private static void writeJsonToFile(File file, Map<String, Object> jsonMap) {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(jsonMap, writer);
            NewHorizonsMain.LOGGER.info("Generated JSON: " + file.getPath());
        } catch (IOException e) {
            NewHorizonsMain.LOGGER.error("Failed to write JSON file: " + file.getPath(), e);
        }
    }

    private static Map<String, Object> getDimensionJson(String modId, String dimensionName) {
        return Map.of(
                "type", "minecraft:the_end",
                "generator", Map.of(
                        "type", "minecraft:flat",
                        "settings", Map.of(
                                "biome", "minecraft:the_void",
                                "lakes", false,
                                "features", true,
                                "layers", new Object[]{
                                        Map.of("height", 1, "block", "minecraft:air")
                                },
                                "structure_overrides", new Object[]{}
                        )
                ),
                "effects", modId + ":" + dimensionName
        );
    }

    private static Map<String, Object> getDimensionTypeJson(String modId, String dimensionName) {
        Map<String, Object> jsonMap = new HashMap<>(); // Correctly initializing a HashMap

        // Now using put() to insert key-value pairs
        jsonMap.put("ambient_light", 0.1);
        jsonMap.put("bed_works", false);
        jsonMap.put("coordinate_scale", 12.0);
        jsonMap.put("fixed_time", 18000);
        jsonMap.put("has_ceiling", false);
        jsonMap.put("has_raids", false);
        jsonMap.put("has_skylight", false);
        jsonMap.put("height", 384);
        jsonMap.put("infiniburn", "#minecraft:infiniburn_overworld");
        jsonMap.put("logical_height", 384);
        jsonMap.put("min_y", -64);
        jsonMap.put("natural", false);
        jsonMap.put("respawn_anchor_works", false);
        jsonMap.put("ultrawarm", false);
        jsonMap.put("monster_spawn_block_light_limit", 0);
        jsonMap.put("monster_spawn_light_level", 15);

        return jsonMap;
    }

}

package org.copycraftDev.new_horizons.client.planets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.NewHorizonsMain;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.io.Resources.getResource;

/**
 * Handles planet registration and JSON loading.
 */
public class CelestialBodyRegistry {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Vec3d.class, (com.google.gson.JsonDeserializer<Vec3d>)
                    (json, type, context) -> {
                        double[] coords = context.deserialize(json, double[].class);
                        return new Vec3d(coords[0], coords[1], coords[2]);
                    }).create();

    private static final Map<Identifier, CelestialBodyData> REGISTERED_PLANETS = new HashMap<>();

    /**
     * Registers a planet from a JSON file.
     * @param jsonPath The path to the JSON file (e.g., "mymod:planets/earth.json")
     */
    public static void registerPlanet(String jsonPath) {
        try {
            String[] parts = jsonPath.split(":");
            if (parts.length != 2) {
                System.err.println("Invalid JSON path format: " + jsonPath);
                return;
            }

            String namespace = parts[0];
            String path = "assets/" + parts[1];

            InputStream inputStream = CelestialBodyRegistry.class.getClassLoader().getResourceAsStream(path);

            if (inputStream == null) {
                System.err.println("Planet JSON file not found: " + path);
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                Type type = new TypeToken<CelestialBodyData>() {}.getType();
                CelestialBodyData planet = GSON.fromJson(reader, type);

                // Convert texture paths to Identifiers
                planet.surfaceTexture = planet.surfaceTexturePath != null ? NewHorizonsMain.id(planet.surfaceTexturePath) : null;
                planet.cloudsTexture = planet.cloudsTexturePath != null ? NewHorizonsMain.id(planet.cloudsTexturePath) : null;
                planet.atmosphereTexture = planet.atmosphereTexturePath != null ? NewHorizonsMain.id(planet.atmosphereTexturePath) : null;
                planet.darkAlbedoMap = planet.darkAlbedoMapPath != null ? NewHorizonsMain.id(planet.darkAlbedoMapPath) : null;
                planet.heightMap = planet.heightMapPath != null ? NewHorizonsMain.id(planet.heightMapPath) : null;
                planet.normalMap = planet.normalMapPath != null ? NewHorizonsMain.id(planet.normalMapPath) : null;

                Identifier planetId = NewHorizonsMain.id(planet.name.toLowerCase());
                if(REGISTERED_PLANETS.containsKey(planetId)) {
                    REGISTERED_PLANETS.replace(planetId, planet);
                }else{
                    REGISTERED_PLANETS.put(planetId, planet);
                }
                System.out.println("Registered planet: " + planet.name);
            }
        } catch (Exception e) {
            System.err.println("Error loading planet: " + jsonPath);
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a registered planet by its identifier.
     * @param id The planet identifier.
     * @return The corresponding CelestialBodyData or null if not found.
     */
    public static CelestialBodyData getPlanet(Identifier id) {
        return REGISTERED_PLANETS.get(id);
    }

    /**
     * Retrieves all registered planets.
     * @return A map of all registered planets.
     */
    public static Map<Identifier, CelestialBodyData> getAllPlanets() {
        return REGISTERED_PLANETS;
    }

    // 🔹 Planet Data Class (JSON Fields)
    public static class CelestialBodyData {
        public String name;
        public Vec3d center;
        public double radius;
        public double rotationSpeed;

        // Textures (converted to Identifiers after loading)
        public transient Identifier surfaceTexture;
        public transient Identifier cloudsTexture;
        public transient Identifier atmosphereTexture;
        public transient Identifier darkAlbedoMap;
        public transient Identifier heightMap;
        public transient Identifier normalMap;
        public int baseZ;
        public Float orbitSpeed;

        // Texture paths (from JSON, only used for conversion)
        String surfaceTexturePath;
        String cloudsTexturePath;
        String atmosphereTexturePath;
        String darkAlbedoMapPath;
        String normalMapPath;
        String heightMapPath;

        // Atmosphere Properties
        public boolean hasAtmosphere;
        public boolean isStar;
        public int[] atmosphereColor1;
        public int[] atmosphereColor2;
        public double atmosphereRadius;

        // Albedo Map
        public boolean hasDarkAlbedoMap;
    }

    public static void registerAllPlanets(String folderPath, String namespace) {
        String fullPath = "assets/" + folderPath; // Convert folder path to resource path

        try {
            // Get the resource folder as a Path
            Path planetsPath = Paths.get(getResource(fullPath).toURI());

            // Iterate through all JSON files in the folder
            try (Stream<Path> files = Files.list(planetsPath)) {
                files.filter(path -> path.toString().endsWith(".json"))
                        .forEach(path -> {
                            // Convert file path to Identifier format
                            String fileName = path.getFileName().toString();
                            String jsonPath = namespace + ":" + folderPath + "/" + fileName;

                            // Register the planet
                            CelestialBodyRegistry.registerPlanet(jsonPath);
                            System.out.println("✅ Registered planet: " + jsonPath);
                        });
            }
        } catch (IOException | URISyntaxException | NullPointerException e) {
            System.err.println("❌ Failed to load planets from folder: " + fullPath);
            e.printStackTrace();
        }
    }
}
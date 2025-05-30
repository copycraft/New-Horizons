package org.copycraftDev.new_horizons.client.planets;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.NewHorizonsMain;
import org.copycraftDev.new_horizons.extrastuff.PlanetTextureGenerator;
import org.copycraftDev.new_horizons.extrastuff.TextureResizer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CelestialBodyRegistry {
    private static final Map<Identifier, CelestialBodyData> REGISTERED_PLANETS = new HashMap<>();

    static {
        registerDefaults();
    }


    public static void reloadPlanets() {
        REGISTERED_PLANETS.clear();
        registerDefaults();
        System.out.println("🔄 Reloaded all planets");
    }


    private static void registerDefaults() {
        register(createSun());
        register(createMercury());
        register(createVenus());
        register(createEarth());
        register(createMars());
        register(createJupiter());
        register(createSaturn());
        register(createUranus());
        register(createNeptune());
    }

    private static CelestialBodyData createSun() {
        CelestialBodyData p = new CelestialBodyData();
        p.name = "Sun";
        p.center = new Vec3d(0, 100, 0);
        p.radius = 140;
        p.rotationSpeed = 0.001;
        p.orbitSpeed = 0.0;
        p.hasAtmosphere = false;
        p.atmosphereColor = null;
        p.isStar = true;
        p.atmosphereRadius = 150;
        p.hasDarkAlbedoMap = false;
        p.usesGeneratedTexture = false;
        p.surfaceTexturePath = "textures/test_textures/8k_sun.jpg";
        p.heightMapPath       = "textures/test_textures/sun_height_map.png";
        p.normalMapPath       = "textures/test_textures/sun_normal_map.png";

        return p;
    }

    private static CelestialBodyData createMercury() {
        CelestialBodyData p = new CelestialBodyData();
        p.name = "Mercury";
        p.center = new Vec3d(0, 100, -390);
        p.radius = 50;
        p.rotationSpeed = 0.0000512;
        p.orbitSpeed = 0.00833;
        p.hasAtmosphere = false;
        p.isStar = false;
        p.atmosphereColor = null;
        p.atmosphereRadius = 0;
        p.hasDarkAlbedoMap = false;
        p.usesGeneratedTexture = false;
        p.surfaceTexturePath    = "textures/test_textures/8k_mercury.jpg";
        p.heightMapPath         = "textures/test_textures/mercury_height_map.jpg";
        p.normalMapPath         = "textures/test_textures/mercury_normal_map.jpg";

        return p;
    }

    private static CelestialBodyData createVenus() {
        CelestialBodyData p = new CelestialBodyData();
        p.name = "Venus";
        p.center = new Vec3d(10, 100, -720);
        p.radius = 80;
        p.rotationSpeed = 0.0000123;
        p.orbitSpeed = 0.00323;
        p.isStar = false;
        p.hasAtmosphere = true;
        p.atmosphereColor = new int[]{255, 180, 80, 80};
        p.atmosphereRadius = 90;
        p.atmosphereTexturePath = "textures/test_textures/8k_earth_clouds.jpg";
        p.hasDarkAlbedoMap = true;
        p.usesGeneratedTexture = false;
        p.surfaceTexturePath    = "textures/test_textures/8k_venus_surface.jpg";
        p.heightMapPath         = "textures/test_textures/venus_height_map.jpg";
        p.normalMapPath         = "textures/test_textures/venus_normal_map.jpg";
        p.darkAlbedoMapPath     = "textures/test_textures/8k_venus_surface.jpg";

        return p;
    }

    private static CelestialBodyData createEarth() {
        CelestialBodyData p = new CelestialBodyData();
        p.name = "Earth";
        p.center = new Vec3d(20, 100, -1000);
        p.radius = 100;
        p.rotationSpeed = 0.003;
        p.orbitSpeed = 0.002;
        p.hasAtmosphere = true;
        p.isStar = false;
        p.atmosphereColor = new int[]{20, 30, 255, 70};
        p.atmosphereRadius = 110;
        p.hasDarkAlbedoMap = true;
        p.usesGeneratedTexture = false;
        p.surfaceTexturePath    = "textures/test_textures/earth_texture.png";
        p.atmosphereTexturePath = "textures/test_textures/8k_earth_clouds.jpg";
        p.darkAlbedoMapPath     = "textures/test_textures/earth_night.png";
        p.heightMapPath         = "textures/test_textures/earth_height.png";
        p.normalMapPath         = "textures/test_textures/earth_normal.png";

        return p;
    }

    private static CelestialBodyData createMars() {
        CelestialBodyData p = new CelestialBodyData();
        p.name = "Mars";
        p.center = new Vec3d(30, 100, -1520);
        p.radius = 80;
        p.rotationSpeed = 0.00291;
        p.orbitSpeed = 0.00106;
        p.hasAtmosphere = false;
        p.isStar = false;
        p.atmosphereColor = new int[]{255, 30, 0, 70};
        p.atmosphereRadius = 90;
        p.hasDarkAlbedoMap = false;
        p.usesGeneratedTexture = false;
        p.surfaceTexturePath    = "textures/test_textures/8k_mars.jpg";
        p.heightMapPath         = "textures/test_textures/mars_height.jpg";
        p.normalMapPath         = "textures/test_textures/mars_normal.jpg";

        return p;
    }

    private static CelestialBodyData createJupiter() {
        CelestialBodyData p = new CelestialBodyData();
        p.name = "Jupiter";
        p.center = new Vec3d(40, 100, -5200);
        p.radius = 110;
        p.rotationSpeed = 0.00732;
        p.orbitSpeed = 0.000169;
        p.hasAtmosphere = false;
        p.isStar = true;
        p.atmosphereColor = null;
        p.atmosphereRadius = 115;
        p.hasDarkAlbedoMap = false;
        p.usesGeneratedTexture = false;
        p.surfaceTexturePath    = "textures/test_textures/8k_jupiter.jpg";
        p.heightMapPath         = "textures/test_textures/jupiter_height.jpg";
        p.normalMapPath         = "textures/test_textures/jupitertest.jpg";

        return p;
    }

    private static CelestialBodyData createSaturn() {
        CelestialBodyData p = new CelestialBodyData();
        p.name = "Saturn";
        p.center = new Vec3d(50, 100, -9540);
        p.radius = 110;
        p.rotationSpeed = 0.00667;
        p.orbitSpeed = 0.0000679;
        p.hasAtmosphere = false;
        p.isStar = true;
        p.atmosphereColor = null;
        p.atmosphereRadius = 115;
        p.hasDarkAlbedoMap = false;
        p.usesGeneratedTexture = false;
        p.surfaceTexturePath    = "textures/test_textures/8k_saturn.jpg";
        p.heightMapPath         = "textures/test_textures/saturn_height_map.jpg";
        p.normalMapPath         = "textures/test_textures/saturn_normal_map.jpg";
        p.ringsTexturePath      = "textures/test_textures/saturn_ring.jpg";
        p.ringsInnerRadius      = 100;
        p.ringsOuterRadius      = 100;
        return p;
    }

    private static CelestialBodyData createUranus() {
        CelestialBodyData p = new CelestialBodyData();
        p.name = "Uranus";
        p.center = new Vec3d(50, 100, -19200);
        p.radius = 120;
        p.rotationSpeed = 0.00417;
        p.orbitSpeed = 0.0000238;
        p.hasAtmosphere = false;
        p.isStar = true;
        p.atmosphereColor = null;
        p.atmosphereRadius = 0;
        p.hasDarkAlbedoMap = false;
        p.usesGeneratedTexture = false;
        p.surfaceTexturePath    = "textures/test_textures/2k_uranus.jpg";
        p.heightMapPath         = "textures/test_textures/earth_height.png";
        p.normalMapPath         = "textures/test_textures/earth_normal.png";

        return p;
    }

    private static CelestialBodyData createNeptune() {
        CelestialBodyData p = new CelestialBodyData();
        p.name = "Neptune";
        p.center = new Vec3d(60, 100, -30006);
        p.radius = 130;
        p.rotationSpeed = 0.00448;
        p.orbitSpeed = 0.0000121;
        p.hasAtmosphere = true;
        p.isStar = true;
        p.atmosphereColor = new int[]{20, 30, 255, 70};
        p.atmosphereRadius = 135;
        p.hasDarkAlbedoMap = false;
        p.usesGeneratedTexture = false;
        p.surfaceTexturePath    = "textures/test_textures/2k_neptune.jpg";
        p.heightMapPath         = "textures/test_textures/neptune_height.jpg";
        p.normalMapPath         = "textures/test_textures/neptune_normal.jpg";

        return p;
    }

    private static void register(CelestialBodyData planet) {
        String key = planet.name.toLowerCase();

        if (planet.usesGeneratedTexture) {
            String genDir = "assets/" + NewHorizonsMain.MOD_ID + "/textures/generated/" + key;
            File d = new File(genDir);
            if (!d.exists() && !d.mkdirs()) {
                System.err.println("Cannot create " + genDir);
            }
            PlanetTextureGenerator gen = new PlanetTextureGenerator.Builder()
                    .size(2048)
                    .seed(123456L)
                    .octaves(5)
                    .frequency(3f)
                    .build();
            gen.generateAndRegister(NewHorizonsMain.MOD_ID, key);

            planet.surfaceTexture = NewHorizonsMain.id("textures/generated/" + key + "/" + key + "_albedo.png");
            planet.heightMap      = NewHorizonsMain.id("textures/generated/" + key + "/" + key + "_height.png");
            planet.normalMap      = NewHorizonsMain.id("textures/generated/" + key + "/" + key + "_normal.png");

            planet.surfaceTexture = TextureResizer.resizeTexture(
                    NewHorizonsMain.MOD_ID,
                    "textures/generated/" + key + "/" + key + "_albedo.png",
                    4096,2048, key + "_alb_resized", false
            );
            planet.heightMap = TextureResizer.resizeTexture(
                    NewHorizonsMain.MOD_ID,
                    "textures/generated/" + key + "/" + key + "_height.png",
                    4096,2048, key + "_hgt_resized", false
            );
            planet.normalMap = TextureResizer.resizeTexture(
                    NewHorizonsMain.MOD_ID,
                    "textures/generated/" + key + "/" + key + "_normal.png",
                    4096,2048, key + "_nrm_resized", false
            );
        } else {
            planet.surfaceTexture    = planet.surfaceTexturePath    != null ? NewHorizonsMain.id(planet.surfaceTexturePath)    : null;
            planet.cloudsTexture     = planet.cloudsTexturePath     != null ? NewHorizonsMain.id(planet.cloudsTexturePath)     : null;
            planet.atmosphereTexture = planet.atmosphereTexturePath != null ? NewHorizonsMain.id(planet.atmosphereTexturePath) : null;
            planet.darkAlbedoMap     = planet.darkAlbedoMapPath     != null ? NewHorizonsMain.id(planet.darkAlbedoMapPath)     : null;
            planet.heightMap         = planet.heightMapPath         != null ? NewHorizonsMain.id(planet.heightMapPath)         : null;
            planet.normalMap         = planet.normalMapPath         != null ? NewHorizonsMain.id(planet.normalMapPath)         : null;
        }

        // handle rings if present
        if (planet.ringsTexturePath != null) {
            planet.ringsTexture = NewHorizonsMain.id(planet.ringsTexturePath);
        }

        REGISTERED_PLANETS.put(NewHorizonsMain.id(key), planet);
        System.out.println("✅ Registered planet " + planet.name);
    }

    public static CelestialBodyData getPlanet(Identifier id) {
        return REGISTERED_PLANETS.get(id);
    }

    public static Map<Identifier,CelestialBodyData> getAllPlanets() {
        return REGISTERED_PLANETS;
    }

    public static double getOrbitAngle(CelestialBodyData data, double worldTime, double partialTick) {
        return (worldTime + partialTick) * data.orbitSpeed;
    }

    public static float getRotationAngle(CelestialBodyData data, double worldTime, double partialTick) {
        double angle = (worldTime + partialTick) * data.rotationSpeed;
        return (float)(angle % 360.0);
    }

    public static Vec3d getPlanetLocation(CelestialBodyData data, double worldTime, double partialTick) {
        double θ = getOrbitAngle(data, worldTime, partialTick);
        double r = data.center.length();
        double x = data.center.x + r * Math.cos(θ);
        double z = data.center.z + r * Math.sin(θ);
        double y = data.center.y;
        return new Vec3d(x, y, z);
    }

    public static class CelestialBodyData {
        public String name;
        public Vec3d center;
        public double radius;
        public double rotationSpeed;

        public transient Identifier surfaceTexture;
        public transient Identifier cloudsTexture;
        public transient Identifier atmosphereTexture;
        public transient Identifier darkAlbedoMap;
        public transient Identifier heightMap;
        public transient Identifier normalMap;


        public transient Identifier ringsTexture;
        public float ringsInnerRadius;
        public float ringsOuterRadius;

        public double orbitSpeed;
        public boolean hasAtmosphere;
        public boolean isStar;
        public int[] atmosphereColor;
        public double atmosphereRadius;
        public boolean hasDarkAlbedoMap;
        String surfaceTexturePath, cloudsTexturePath, atmosphereTexturePath;
        String darkAlbedoMapPath, normalMapPath, heightMapPath;
        String ringsTexturePath;
        public boolean usesGeneratedTexture;
    }
}

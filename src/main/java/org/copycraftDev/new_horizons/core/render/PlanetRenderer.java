package org.copycraftDev.new_horizons.core.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.core.BufferAllocatorAccessor;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PlanetRenderer {

    public static final List<Planet> planets = new ArrayList<>();

    static {
        planets.add(new Planet(0.0f, 0.0f, 50.0f, Identifier.of("spacetrav:textures/planet/sun.png"),
                0.0f, 4.0f, null, new float[]{0.0f, 100.0f, 0.0f}, "spacetrav:sun_dimension", 10.0f, PlanetShape.SPHERE));

        planets.add(new Planet(300.0f, 0.01f, 20.0f, Identifier.of("spacetrav:textures/planet/earth_day.png"),
                25.0f, 5.0f, planets.get(0), null, "spacetrav:earth_dimension", 0.0f, PlanetShape.DONUT));

        planets.add(new Planet(50.0f, 0.5f, 5.0f, Identifier.of("spacetrav:textures/planet/moon.png"),
                0.0f, 0.5f, planets.get(1), null, "minecraft:the_nether", 0.0f, PlanetShape.SPHERE));

        planets.add(new Planet(500.0f, 0.005f, 40.0f, Identifier.of("spacetrav:textures/planet/jupiter.png"),
                0.0f, 1.0f, planets.get(0), null, "spacetrav:jupiter_dimension", 0.0f, PlanetShape.CUBE));
    }

    public static void tick(Level level) {

        checkCollisions(level);

    }

    public static void renderPlanets(MatrixStack poseStack, float partialTicks) {
        RenderSystem.enableDepthTest();
        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        double camX = camPos.x;
        double camY = camPos.y;
        double camZ = camPos.z;


        for (Planet planet : planets) {
            poseStack.push();


            planet.updateOrbit(partialTicks);
            planet.updateRotation(partialTicks);


            poseStack.translate(planet.getX() - camX, planet.getY() - camY, planet.getZ() - camZ);


            Matrix4f rotationMatrix = new Matrix4f();
            rotationMatrix.rotationY((float) Math.toRadians(planet.getRotationAngle()));
            // Instead of poseStack.mulPoseMatrix, in 1.21.1 we multiply the current matrix:
            poseStack.peek();


            RenderSystem.setShaderTexture(0, planet.getTexture());
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


            renderPlanetGeometry(poseStack, planet);

            poseStack.pop();
        }
    }
    private static void renderPlanetGeometry(MatrixStack poseStack, Planet planet) {
        Tessellator tesselator = Tessellator.getInstance();
        BufferAllocator allocator = BufferAllocatorAccessor.getBufferAllocator();// Or however you obtain it
        BufferBuilder buffer = new BufferBuilder(allocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);


        poseStack.push();
        Matrix4f matrix = poseStack.peek().getPositionMatrix(); // Fixed method call

        planet.getShape().generateGeometry(buffer, planet.getSize(), matrix);
        BufferRenderer.draw(buffer.end());
        // Use the correct method
        poseStack.pop();
    }

    private static float computeLighting(Planet planet) {
        float totalLight = 0.0f;

        for (Planet star : planets) {
            if (star.getBrightness() > 0) {
                float dist = (float) Math.sqrt(Math.pow(planet.getX() - star.getX(), 2) +
                        Math.pow(planet.getY() - star.getY(), 2) +
                        Math.pow(planet.getZ() - star.getZ(), 2));
                float intensity = star.getBrightness() / (dist * 0.1f + 1.0f);
                totalLight += Math.max(0.0f, intensity);
            }
        }

        return Math.min(totalLight, 1.0f);
    }

    private static <AABB> void checkCollisions(Level level) {
        for (Planet planet : planets) {
            AABB planetBounds = (AABB) planet.getBoundingBox();
        }
    }

    public static class Planet {
        private float orbitDistance, orbitSpeed, orbitAngle, size, rotationAngle, rotationSpeed;
        private Identifier texture;
        private String dimension;
        private float x, y, z;
        private Planet orbitOrigin;
        private float[] staticPosition;
        private float brightness;
        private PlanetShape shape;

        public Planet(float orbitDistance, float orbitSpeed, float size, Identifier texture,
                      float rotationAngle, float rotationSpeed, Planet orbitOrigin,
                      float[] staticPosition, String dimension, float brightness, PlanetShape shape) {
            this.orbitDistance = orbitDistance;
            this.orbitSpeed = orbitSpeed;
            this.size = size;
            this.texture = texture;
            this.rotationAngle = rotationAngle;
            this.rotationSpeed = rotationSpeed;
            this.orbitOrigin = orbitOrigin;
            this.staticPosition = staticPosition;
            this.dimension = dimension;
            this.brightness = brightness;
            this.orbitAngle = 0;
            this.shape = shape;
        }

        public float getBrightness() {
            return brightness;
        }

        public void setBrightness(float brightness) {
            this.brightness = brightness;
        }

        public PlanetShape getShape() {
            return shape;
        }

        public float getOrbitDistance() {
            return orbitDistance;
        }

        public void setOrbitDistance(float orbitDistance) {
            this.orbitDistance = orbitDistance;
        }

        public float getOrbitSpeed() {
            return orbitSpeed;
        }

        public void setOrbitSpeed(float orbitSpeed) {
            this.orbitSpeed = orbitSpeed;
        }

        public float getOrbitAngle() {
            return orbitAngle;
        }

        public void setOrbitAngle(float orbitAngle) {
            this.orbitAngle = orbitAngle;
        }

        public float getSize() {
            return size;
        }

        public void setSize(float size) {
            this.size = size;
        }

        public float getRotationAngle() {
            return rotationAngle;
        }

        public void setRotationAngle(float rotationAngle) {
            this.rotationAngle = rotationAngle;
        }

        public float getRotationSpeed() {
            return rotationSpeed;
        }

        public void setRotationSpeed(float rotationSpeed) {
            this.rotationSpeed = rotationSpeed;
        }

        public Identifier getTexture() {
            return texture;
        }

        public void setTexture(Identifier texture) {
            this.texture = texture;
        }

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getZ() {
            return z;
        }

        public void setZ(float z) {
            this.z = z;
        }

        public Planet getOrbitOrigin() {
            return orbitOrigin;
        }

        public float[] getStaticPosition() {
            return staticPosition;
        }

        public void setOrbitOrigin(Planet orbitOrigin) {
            this.orbitOrigin = orbitOrigin;
        }

        public void setStaticPosition(Vector3d staticPosition) {
            this.staticPosition = new float[]{(float) staticPosition.x, (float) staticPosition.y, (float) staticPosition.z};
        }

        public void setShape(PlanetShape shape) {
            this.shape = shape;
        }

        public void updateRotation(float partialTicks) {
            rotationAngle = (rotationAngle + rotationSpeed * partialTicks) % 360.0f;
        }

        public void updateOrbit(float partialTicks) {
            orbitAngle = (orbitAngle + orbitSpeed * partialTicks) % 360.0f;
            if (staticPosition != null) {
                x = staticPosition[0];
                y = staticPosition[1];
                z = staticPosition[2];
            } else if (orbitOrigin != null) {
                x = orbitOrigin.getX() + orbitDistance * (float) Math.cos(Math.toRadians(orbitAngle));
                y = orbitOrigin.getY();
                z = orbitOrigin.getZ() + orbitDistance * (float) Math.sin(Math.toRadians(orbitAngle));
            }
        }

        public Box getBoundingBox() {
            return new Box(x - size / 2, y - size / 2, z - size / 2, x + size / 2, y + size / 2, z + size / 2);
        }
    }

    public enum PlanetShape {
        CUBE {
            @Override
            public void generateGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
                generateCubeGeometry(consumer, size, matrix);
            }
        },
        DONUT {
            @Override
            public void generateGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
                generateDonutGeometry(consumer, size, 32, 32, matrix);
            }
        },
        DOME {
            @Override
            public void generateGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
                generateDomeGeometry(consumer, size, matrix);
            }
        },
        BOWL {
            @Override
            public void generateGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
                generateBowlGeometry(consumer, size, matrix);
            }
        },
        WAFFLE_GRID {
            @Override
            public void generateGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
                generateWaffleGridGeometry(consumer, size, matrix);
            }
        },
        PYRAMID {
            @Override
            public void generateGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
                generatePyramidGeometry(consumer, size, matrix);
            }
        },
        SPHERE {
            @Override
            public void generateGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
                generateSphereGeometry(consumer, size, matrix);
            }
        };

        public abstract void generateGeometry(VertexConsumer consumer, float size, Matrix4f matrix);

        private static void generateCubeGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
            float half = size / 2.0f;
            // Front face (z = -half)
            addQuad(consumer, matrix,
                    new Vector3f(-half, -half, -half),
                    new Vector3f( half, -half, -half),
                    new Vector3f( half,  half, -half),
                    new Vector3f(-half,  half, -half),
                    new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f},
                    new Vector3f(0.0f, 0.0f, -1.0f)
            );
            // Back face (z = half)
            addQuad(consumer, matrix,
                    new Vector3f( half, -half, half),
                    new Vector3f(-half, -half, half),
                    new Vector3f(-half,  half, half),
                    new Vector3f( half,  half, half),
                    new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f},
                    new Vector3f(0.0f, 0.0f, 1.0f)
            );
            // Left face (x = -half)
            addQuad(consumer, matrix,
                    new Vector3f(-half, -half, half),
                    new Vector3f(-half, -half, -half),
                    new Vector3f(-half,  half, -half),
                    new Vector3f(-half,  half, half),
                    new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f},
                    new Vector3f(-1.0f, 0.0f, 0.0f)
            );
            // Right face (x = half)
            addQuad(consumer, matrix,
                    new Vector3f( half, -half, -half),
                    new Vector3f( half, -half,  half),
                    new Vector3f( half,  half,  half),
                    new Vector3f( half,  half, -half),
                    new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f},
                    new Vector3f(1.0f, 0.0f, 0.0f)
            );
            // Top face (y = half)
            addQuad(consumer, matrix,
                    new Vector3f(-half, half, -half),
                    new Vector3f( half, half, -half),
                    new Vector3f( half, half,  half),
                    new Vector3f(-half, half,  half),
                    new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f},
                    new Vector3f(0.0f, 1.0f, 0.0f)
            );
            // Bottom face (y = -half)
            addQuad(consumer, matrix,
                    new Vector3f(-half, -half,  half),
                    new Vector3f( half, -half,  half),
                    new Vector3f( half, -half, -half),
                    new Vector3f(-half, -half, -half),
                    new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f},
                    new Vector3f(0.0f, -1.0f, 0.0f)
            );
        }

        private static void addQuad(VertexConsumer consumer, Matrix4f matrix,
                                    Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4,
                                    float[] uv, Vector3f normal) {
            // First triangle
            consumer.vertex(matrix, v1.x, v1.y, v1.z).color(255, 255, 255, 255)
                    .normal(normal.x, normal.y, normal.z);
            consumer.vertex(matrix, v2.x, v2.y, v2.z).color(255, 255, 255, 255)
                    .normal(normal.x, normal.y, normal.z);
            consumer.vertex(matrix, v3.x, v3.y, v3.z).color(255, 255, 255, 255)
                    .normal(normal.x, normal.y, normal.z);
            // Second triangle
            consumer.vertex(matrix, v1.x, v1.y, v1.z).color(255, 255, 255, 255)
                    .normal(normal.x, normal.y, normal.z);
            consumer.vertex(matrix, v3.x, v3.y, v3.z).color(255, 255, 255, 255)
                    .normal(normal.x, normal.y, normal.z);
            consumer.vertex(matrix, v4.x, v4.y, v4.z).color(255, 255, 255, 255)
                    .normal(normal.x, normal.y, normal.z);
        }

        private static void generateSphereGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
            int resolution = 64;
            float radius = size / 2.0f;
            for (int lat = 0; lat < resolution; lat++) {
                float theta1 = (float) Math.PI * lat / resolution;
                float theta2 = (float) Math.PI * (lat + 1) / resolution;
                for (int lon = 0; lon < resolution; lon++) {
                    float phi1 = (float) (2 * Math.PI * lon / resolution);
                    float phi2 = (float) (2 * Math.PI * (lon + 1) / resolution);
                    float x1 = radius * (float) Math.sin(theta1) * (float) Math.cos(phi1);
                    float y1 = radius * (float) Math.cos(theta1);
                    float z1 = radius * (float) Math.sin(theta1) * (float) Math.sin(phi1);
                    float x2 = radius * (float) Math.sin(theta2) * (float) Math.cos(phi1);
                    float y2 = radius * (float) Math.cos(theta2);
                    float z2 = radius * (float) Math.sin(theta2) * (float) Math.sin(phi1);
                    float x3 = radius * (float) Math.sin(theta2) * (float) Math.cos(phi2);
                    float y3 = radius * (float) Math.cos(theta2);
                    float z3 = radius * (float) Math.sin(theta2) * (float) Math.sin(phi2);
                    float x4 = radius * (float) Math.sin(theta1) * (float) Math.cos(phi2);
                    float y4 = radius * (float) Math.cos(theta1);
                    float z4 = radius * (float) Math.sin(theta1) * (float) Math.sin(phi2);
                    float u1 = (float) (lon / (double) resolution);
                    float v1 = (float) (lat / (double) resolution);
                    float u2 = (float) ((lon + 1) / (double) resolution);
                    float v2 = (float) ((lat + 1) / (double) resolution);
                    Vector3f normal1 = new Vector3f(x1, y1, z1).normalize();
                    Vector3f normal2 = new Vector3f(x2, y2, z2).normalize();
                    Vector3f normal3 = new Vector3f(x3, y3, z3).normalize();
                    Vector3f normal4 = new Vector3f(x4, y4, z4).normalize();
                    consumer.vertex(matrix, x1, y1, z1).color(255, 255, 255, 255).normal(normal1.x, normal1.y, normal1.z);
                    consumer.vertex(matrix, x4, y4, z4).color(255, 255, 255, 255).normal(normal4.x, normal4.y, normal4.z);
                    consumer.vertex(matrix, x3, y3, z3).color(255, 255, 255, 255).normal(normal3.x, normal3.y, normal3.z);
                    consumer.vertex(matrix, x2, y2, z2).color(255, 255, 255, 255).normal(normal2.x, normal2.y, normal2.z);
                }
            }
        }

        private static void generateDomeGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
            int resolution = 32;
            float radius = size / 2.0f;
            for (int lat = 0; lat < resolution / 2; lat++) {
                float theta1 = (float) (Math.PI / 2 * lat / (resolution / 2));
                float theta2 = (float) (Math.PI / 2 * (lat + 1) / (resolution / 2));
                for (int lon = 0; lon < resolution; lon++) {
                    float phi1 = (float) (2 * Math.PI * lon / resolution);
                    float phi2 = (float) (2 * Math.PI * (lon + 1) / resolution);
                    Vector3f vertex1 = calculateSphericalVertex(theta1, phi1, radius);
                    Vector3f vertex2 = calculateSphericalVertex(theta1, phi2, radius);
                    Vector3f vertex3 = calculateSphericalVertex(theta2, phi2, radius);
                    Vector3f vertex4 = calculateSphericalVertex(theta2, phi1, radius);
                    Vector3f normal1 = vertex1.normalize();
                    Vector3f normal2 = vertex2.normalize();
                    Vector3f normal3 = vertex3.normalize();
                    Vector3f normal4 = vertex4.normalize();
                    float u1 = (float) (lon / (double) resolution);
                    float v1 = (float) (lat / (double) resolution);
                    float u2 = (float) ((lon + 1) / (double) resolution);
                    float v2 = (float) ((lat + 1) / (double) resolution);
                    consumer.vertex(matrix, vertex1.x, vertex1.y, vertex1.z).color(255, 255, 255, 255).normal(normal1.x, normal1.y, normal1.z);
                    consumer.vertex(matrix, vertex4.x, vertex4.y, vertex4.z).color(255, 255, 255, 255).normal(normal4.x, normal4.y, normal4.z);
                    consumer.vertex(matrix, vertex3.x, vertex3.y, vertex3.z).color(255, 255, 255, 255).normal(normal3.x, normal3.y, normal3.z);
                    consumer.vertex(matrix, vertex2.x, vertex2.y, vertex2.z).color(255, 255, 255, 255).normal(normal2.x, normal2.y, normal2.z);
                }
            }
        }

        private static void generateBowlGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
            int resolution = 32;
            float radius = size / 2.0f;
            for (int lat = 0; lat < resolution / 2; lat++) {
                float theta1 = (float) (Math.PI / 2 * lat / (resolution / 2));
                float theta2 = (float) (Math.PI / 2 * (lat + 1) / (resolution / 2));
                for (int lon = 0; lon < resolution; lon++) {
                    float phi1 = (float) (2 * Math.PI * lon / resolution);
                    float phi2 = (float) (2 * Math.PI * (lon + 1) / resolution);
                    Vector3f vertex1 = calculateSphericalVertex(theta1, phi1, radius);
                    Vector3f vertex2 = calculateSphericalVertex(theta1, phi2, radius);
                    Vector3f vertex3 = calculateSphericalVertex(theta2, phi2, radius);
                    Vector3f vertex4 = calculateSphericalVertex(theta2, phi1, radius);
                    Vector3f normal1 = vertex1.negate().normalize();
                    Vector3f normal2 = vertex2.negate().normalize();
                    Vector3f normal3 = vertex3.negate().normalize();
                    Vector3f normal4 = vertex4.negate().normalize();
                    float u1 = (float) (lon / (double) resolution);
                    float v1 = (float) (lat / (double) resolution);
                    float u2 = (float) ((lon + 1) / (double) resolution);
                    float v2 = (float) ((lat + 1) / (double) resolution);
                    consumer.vertex(matrix, vertex1.x, vertex1.y, vertex1.z).color(255, 255, 255, 255).normal(normal1.x, normal1.y, normal1.z);
                    consumer.vertex(matrix, vertex4.x, vertex4.y, vertex4.z).color(255, 255, 255, 255).normal(normal4.x, normal4.y, normal4.z);
                    consumer.vertex(matrix, vertex3.x, vertex3.y, vertex3.z).color(255, 255, 255, 255).normal(normal3.x, normal3.y, normal3.z);
                    consumer.vertex(matrix, vertex2.x, vertex2.y, vertex2.z).color(255, 255, 255, 255).normal(normal2.x, normal2.y, normal2.z);
                }
            }
        }

        private static void generateWaffleGridGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
            int gridSize = 10;
            for (int x = -gridSize; x < gridSize; x++) {
                for (int z = -gridSize; z < gridSize; z++) {
                    for (int y = -1; y <= 1; y++) {
                        generateCubeGeometry(consumer, size / gridSize, matrix);
                    }
                }
            }
        }

        private static void generatePyramidGeometry(VertexConsumer consumer, float size, Matrix4f matrix) {
            float half = size / 2.0f;
            // Base face (square)
            addQuad(consumer, matrix,
                    new Vector3f(-half, -half, -half),
                    new Vector3f( half, -half, -half),
                    new Vector3f( half, -half,  half),
                    new Vector3f(-half, -half,  half),
                    new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f},
                    new Vector3f(0.0f, -1.0f, 0.0f)
            );
            // Pyramid sides
            Vector3f apex = new Vector3f(0.0f, size, 0.0f);
            addTriangle(consumer, matrix, apex, new Vector3f(-half, -half, -half), new Vector3f( half, -half, -half));
            addTriangle(consumer, matrix, apex, new Vector3f( half, -half, -half), new Vector3f( half, -half,  half));
            addTriangle(consumer, matrix, apex, new Vector3f( half, -half,  half), new Vector3f(-half, -half,  half));
            addTriangle(consumer, matrix, apex, new Vector3f(-half, -half,  half), new Vector3f(-half, -half, -half));
        }

        private static void generateDonutGeometry(VertexConsumer consumer, float size, int radialSegments, int tubularSegments, Matrix4f matrix) {
            float ringRadius = size * 5f;
            float tubeRadius = size * 2f;
            for (int i = 0; i < radialSegments; i++) {
                float theta1 = (float) (2 * Math.PI * i / radialSegments);
                float theta2 = (float) (2 * Math.PI * (i + 1) / radialSegments);
                for (int j = 0; j < tubularSegments; j++) {
                    float phi1 = (float) (2 * Math.PI * j / tubularSegments);
                    float phi2 = (float) (2 * Math.PI * (j + 1) / tubularSegments);
                    Vector3f vertex1 = calculateTorusVertex(theta1, phi1, ringRadius, tubeRadius);
                    Vector3f vertex2 = calculateTorusVertex(theta1, phi2, ringRadius, tubeRadius);
                    Vector3f vertex3 = calculateTorusVertex(theta2, phi2, ringRadius, tubeRadius);
                    Vector3f vertex4 = calculateTorusVertex(theta2, phi1, ringRadius, tubeRadius);
                    Vector3f normal1 = vertex1.normalize();
                    Vector3f normal2 = vertex2.normalize();
                    Vector3f normal3 = vertex3.normalize();
                    Vector3f normal4 = vertex4.normalize();
                    float u1 = (float) (j / (double) tubularSegments);
                    float v1 = (float) (i / (double) radialSegments);
                    float u2 = (float) ((j + 1) / (double) tubularSegments);
                    float v2 = (float) ((i + 1) / (double) radialSegments);
                    consumer.vertex(matrix, vertex1.x, vertex1.y, vertex1.z).color(255, 255, 255, 255).normal(normal1.x, normal1.y, normal1.z);
                    consumer.vertex(matrix, vertex4.x, vertex4.y, vertex4.z).color(255, 255, 255, 255).normal(normal4.x, normal4.y, normal4.z);
                    consumer.vertex(matrix, vertex3.x, vertex3.y, vertex3.z).color(255, 255, 255, 255).normal(normal3.x, normal3.y, normal3.z);
                    consumer.vertex(matrix, vertex2.x, vertex2.y, vertex2.z).color(255, 255, 255, 255).normal(normal2.x, normal2.y, normal2.z);
                }
            }
        }

        private static void addTriangle(VertexConsumer consumer, Matrix4f matrix, Vector3f v1, Vector3f v2, Vector3f v3) {
            consumer.vertex(matrix, v1.x, v1.y, v1.z).color(255, 255, 255, 255);
            consumer.vertex(matrix, v2.x, v2.y, v2.z).color(255, 255, 255, 255);
            consumer.vertex(matrix, v3.x, v3.y, v3.z).color(255, 255, 255, 255);
        }

        private static Vector3f calculateTorusVertex(float theta, float phi, float ringRadius, float tubeRadius) {
            float x = (ringRadius + tubeRadius * (float) Math.cos(phi)) * (float) Math.cos(theta);
            float y = (ringRadius + tubeRadius * (float) Math.cos(phi)) * (float) Math.sin(theta);
            float z = tubeRadius * (float) Math.sin(phi);
            return new Vector3f(x, y, z);
        }

        private static Vector3f calculateSphericalVertex(float theta, float phi, float radius) {
            float x = (float) (radius * Math.cos(theta) * Math.cos(phi));
            float y = (float) (radius * Math.cos(theta) * Math.sin(phi));
            float z = (float) (radius * Math.sin(theta));
            return new Vector3f(x, y, z);
        }
    }
}


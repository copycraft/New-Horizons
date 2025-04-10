package org.copycraftDev.new_horizons.core.planet;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.portal.Portal;

import java.util.ArrayList;
import java.util.List;

/**
 * Spawns a cluster of portals arranged in the geometry of a truncated icosahedron.
 * The structure is formed by the 12 vertices (pentagonal faces) and the 20 face centers
 * (hexagonal faces) of a regular icosahedron, totaling 32 portal placements.
 *
 * This method spawns the portals in every loaded dimension. Each portal is placed
 * at (clusterCenter + (faceCenter * clusterRadius)) and is configured to lead to the
 * destination dimension "new_horizons:venus" at coordinates (100, 70, 100).
 */
public class TruncatedIcosahedronPortalSpawner {

    public static void spawnPortalsInAllDimensions(MinecraftServer server) {
        // Use RegistryKeys.DIMENSION (from Yarn mappings) to specify the destination dimension.
        //RegistryKey<World> destinationDimension = RegistryKey.of((RegistryKey<? extends net.minecraft.registry.Registry<World>>) RegistryKeys.DIMENSION, Identifier.of("new_horizons", "venus"));
        Vec3d destinationCoordinates = new Vec3d(100, 70, 100);

        double clusterRadius = 10.0;
        Vec3d clusterCenter = new Vec3d(0, 70, 0);

        List<Vec3d> faceCenters = getTruncatedIcosahedronFaceCenters();

        for (ServerWorld world : server.getWorlds()) {
            for (Vec3d faceCenter : faceCenters) {
                // Calculate portal origin
                Vec3d portalOrigin = clusterCenter.add(faceCenter.multiply(clusterRadius));
                // Use the face center as the outward normal direction
                Vec3d normal = faceCenter.normalize();

                // Determine portal orientation:
                Vec3d up = new Vec3d(0, 1, 0);
                if (Math.abs(normal.dotProduct(up)) > 0.99) {
                    up = new Vec3d(1, 0, 0);
                }
                Vec3d axisW = normal.crossProduct(up).normalize();
                Vec3d axisH = axisW.crossProduct(normal).normalize();

                // Create portal using the Immersive Portals helper API.
                //Portal portal = Portal.entityType.create(serverWorld);
                //portal.setOriginPos(portalOrigin);
                //portal.setDestinationDimension(destinationDimension);
                //portal.setDestination(destinationCoordinates);
                //portal.setOrientationAndSize(axisW, axisH, 4, 4);

                //world.spawnEntity(portal);

            }
        }
    }

    private static List<Vec3d> getTruncatedIcosahedronFaceCenters() {
        List<Vec3d> centers = new ArrayList<>();
        // 12 face centers from icosahedron vertices
        List<Vec3d> vertices = getIcosahedronVertices();
        centers.addAll(vertices);

        // 20 face centers from the icosahedron face centers
        List<Vec3d> hexagonCenters = getIcosahedronFaceCenters(vertices);
        centers.addAll(hexagonCenters);

        return centers;
    }

    private static List<Vec3d> getIcosahedronVertices() {
        List<Vec3d> vertices = new ArrayList<>();
        double phi = (1 + Math.sqrt(5)) / 2.0;
        // Define the canonical 12 vertices of a regular icosahedron.
        vertices.add(new Vec3d(0, 1, phi));
        vertices.add(new Vec3d(0, -1, phi));
        vertices.add(new Vec3d(0, 1, -phi));
        vertices.add(new Vec3d(0, -1, -phi));
        vertices.add(new Vec3d(1, phi, 0));
        vertices.add(new Vec3d(-1, phi, 0));
        vertices.add(new Vec3d(1, -phi, 0));
        vertices.add(new Vec3d(-1, -phi, 0));
        vertices.add(new Vec3d(phi, 0, 1));
        vertices.add(new Vec3d(phi, 0, -1));
        vertices.add(new Vec3d(-phi, 0, 1));
        vertices.add(new Vec3d(-phi, 0, -1));

        List<Vec3d> normalized = new ArrayList<>();
        for (Vec3d v : vertices) {
            normalized.add(v.normalize());
        }
        return normalized;
    }

    private static List<Vec3d> getIcosahedronFaceCenters(List<Vec3d> vertices) {
        List<Vec3d> faceCenters = new ArrayList<>();
        // The 20 faces of a regular icosahedron are defined by these vertex indices.
        int[][] faces = {
                {0, 8, 4},
                {0, 4, 5},
                {0, 5, 10},
                {0, 10, 2},
                {0, 2, 8},
                {3, 4, 8},
                {3, 8, 2},
                {3, 2, 11},
                {3, 11, 7},
                {3, 7, 4},
                {6, 5, 4},
                {6, 4, 7},
                {6, 7, 11},
                {6, 11, 9},
                {6, 9, 5},
                {1, 10, 5},
                {1, 5, 9},
                {1, 9, 11},
                {1, 11, 2},
                {1, 2, 10}
        };

        for (int[] face : faces) {
            Vec3d sum = Vec3d.ZERO;
            for (int idx : face) {
                sum = sum.add(vertices.get(idx));
            }
            Vec3d center = sum.multiply(1.0 / face.length).normalize();
            faceCenters.add(center);
        }
        return faceCenters;
    }
}

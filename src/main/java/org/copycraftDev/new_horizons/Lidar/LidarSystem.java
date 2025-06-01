package org.copycraftDev.new_horizons.Lidar;

import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.*;

/**
 * LidarSystem handles raycasting and generating a single scan point
 * on the grid of the hit block face. Points can override color with a probability.
 */
public class LidarSystem {

    public static final List<ScanPoint> POINTS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    /**
     * Holds color (r,g,b) and reveal probability for a block or entity.
     */
    public static class ColorInfo {
        public final float r, g, b;
        public final float probability;

        public ColorInfo(float r, float g, float b, float probability) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.probability = probability;
        }
    }

    // Map of specific blocks to override ColorInfo
    public static final Map<Block, ColorInfo> BLOCK_COLOR_OVERRIDES = new HashMap<>();

    // Map of specific entity types to override ColorInfo
    public static final Map<EntityType<?>, ColorInfo> ENTITY_COLOR_OVERRIDES = new HashMap<>();

    public static class ScanPoint {
        public final Vec3d pos;
        public final Vec3d normal;
        public final float r, g, b;
        public float life;

        public ScanPoint(Vec3d pos, Vec3d normal, float r, float g, float b) {
            this.pos = pos;
            this.normal = normal;
            this.r = r;
            this.g = g;
            this.b = b;
            this.life = 500f;
        }
    }

    public static void tick() {
        synchronized (POINTS) {
            Iterator<ScanPoint> it = POINTS.iterator();
            while (it.hasNext()) {
                ScanPoint p = it.next();
                if (--p.life <= 0) {
                    it.remove();
                }
            }
        }
    }

    public static void addPointWithDistanceColoring(Vec3d pos, Vec3d normal, Vec3d playerPos) {
        double dist = pos.distanceTo(playerPos);
        float maxDist = 30f;

        float r = Math.max(0f, 1.0f - (float) dist / maxDist);
        float g = 0.2f;
        float b = Math.min(1f, (float) dist / maxDist);

        synchronized (POINTS) {
            POINTS.add(new ScanPoint(pos, normal, r, g, b));
        }
    }

    public static void raycastAndScan(ClientWorld world,
                                      Entity entity,
                                      Vec3d origin,
                                      Vec3d direction,
                                      int resolution,
                                      double maxDistance,
                                      boolean distanceBased,
                                      float r,
                                      float g,
                                      float b) {
        Vec3d end = origin.add(direction.multiply(maxDistance));
        RaycastContext rtc = new RaycastContext(
                origin, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                entity
        );
        BlockHitResult hit = world.raycast(rtc);

        if (hit.getType() == BlockHitResult.Type.BLOCK) {
            BlockPos hitPos = hit.getBlockPos();
            Block hitBlock = world.getBlockState(hitPos).getBlock();

            ColorInfo blockInfo = BLOCK_COLOR_OVERRIDES.get(hitBlock);
            if (blockInfo != null && RANDOM.nextFloat() <= blockInfo.probability) {
                scanSingleGridPoint(world, hitPos, hit.getSide(), hit.getPos(), resolution,
                        null, false, blockInfo.r, blockInfo.g, blockInfo.b);
            } else {
                if (distanceBased) {
                    scanSingleGridPoint(world, hitPos, hit.getSide(), hit.getPos(), resolution,
                            entity.getPos(), true, r, g, b);
                } else {
                    scanSingleGridPoint(world, hitPos, hit.getSide(), hit.getPos(), resolution,
                            null, false, r, g, b);
                }
            }
        }
    }

    private static void scanSingleGridPoint(ClientWorld world,
                                            BlockPos blockPos,
                                            Direction face,
                                            Vec3d hitPos,
                                            int resolution,
                                            Vec3d playerPos,
                                            boolean useDistanceColoring,
                                            float r,
                                            float g,
                                            float b) {

        Vec3d blockCenter = Vec3d.ofCenter(blockPos);
        Vec3d faceNormal = Vec3d.of(face.getVector());

        Vec3d axis1, axis2;
        if (face.getAxis().isHorizontal()) {
            axis1 = Vec3d.of(face.rotateYClockwise().getVector()).normalize();
            axis2 = new Vec3d(0, 1, 0);
            if (face == Direction.NORTH) {
                axis1 = axis1.multiply(-1);
            }
        } else {
            axis1 = new Vec3d(1, 0, 0);
            axis2 = new Vec3d(0, 0, 1);
            if (face == Direction.DOWN) {
                axis2 = axis2.multiply(-1);
            }
        }

        double halfSize = 0.5;
        Vec3d faceOffset = faceNormal.multiply(0.501);

        Vec3d localVec = hitPos.subtract(blockCenter).subtract(faceOffset);
        double fx = localVec.x * axis1.x + localVec.y * axis1.y + localVec.z * axis1.z;
        double fy = localVec.x * axis2.x + localVec.y * axis2.y + localVec.z * axis2.z;

        int ix = (int) Math.round(((fx / halfSize + 1.0) * 0.5) * (resolution - 1));
        int iy = (int) Math.round(((fy / halfSize + 1.0) * 0.5) * (resolution - 1));

        ix = Math.max(0, Math.min(resolution - 1, ix));
        iy = Math.max(0, Math.min(resolution - 1, iy));

        double gridX = ((double) ix / (resolution - 1) - 0.5) * 2.0 * halfSize;
        double gridY = ((double) iy / (resolution - 1) - 0.5) * 2.0 * halfSize;
        Vec3d offset = axis1.multiply(gridX).add(axis2.multiply(gridY));
        Vec3d pointPos = blockCenter.add(offset).add(faceOffset);

        if (useDistanceColoring && playerPos != null) {
            addPointWithDistanceColoring(pointPos, faceNormal, playerPos);
        } else {
            synchronized (POINTS) {
                POINTS.add(new ScanPoint(pointPos, faceNormal, r, g, b));
            }
        }
    }
}

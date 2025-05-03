package org.copycraftDev.new_horizons.physics;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldAccess;

import java.util.*;

public class PhysicsMain {
    public static final PhysicsManager PHYSICS_MANAGER = new PhysicsManager();

    public static class PhysicsManager {
        private final List<PhysicsObject> activeObjects = new ArrayList<>();

        public PhysicsObject create(ServerWorld world, Vec3d pos) {
            PhysicsObject obj = new PhysicsObject(pos);
            activeObjects.add(obj);
            return obj;
        }

        public void addObject(PhysicsObject object) {
            this.activeObjects.add(object);
        }

        public void removeObject(PhysicsObject object) {
            this.activeObjects.remove(object);
        }


        public List<PhysicsObject> getAllObjects() {
            return activeObjects;
        }

        public void tickAll(ServerWorld world) {
            for (PhysicsObject obj : new ArrayList<>(activeObjects)) {
                obj.tick(world);
            }
        }
    }

    public static class PhysicsObject {
        private Vec3d position;
        private Vec3d velocity = Vec3d.ZERO;
        private Vec3d rotation = Vec3d.ZERO;

        private static final double GRAVITY = -0.04;
        private static final double TERMINAL_VELOCITY = -2.5;
        private static final double FRICTION = 0.91;
        private static final double BOUNCE_FACTOR = 0.3;

        private final Map<BlockPos, BlockState> blocks = new HashMap<>();
        final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();

        private Box localBounds = null;

        public PhysicsObject(Vec3d startPos) {
            this.position = startPos;
        }

        public void addBlock(BlockPos localPos, BlockState state, BlockEntity be) {
            blocks.put(localPos, state);
            if (be != null) blockEntities.put(localPos, be);
            localBounds = null; // invalidate cache
        }

        public void tick(ServerWorld world) {
            applyGravity();
            doMovement(world);
            updateWorldBlocks(world);
        }

        private void applyGravity() {
            velocity = velocity.add(0, GRAVITY, 0);
            if (velocity.y < TERMINAL_VELOCITY) {
                velocity = new Vec3d(velocity.x, TERMINAL_VELOCITY, velocity.z);
            }
        }

        private void doMovement(ServerWorld world) {
            Box bounds = getWorldBounds();
            Vec3d attempted = velocity;
            Vec3d moved = attempted;

            // Per-axis collision checking
            moved = tryMove(world, bounds, moved.x, 0, 0);
            bounds = bounds.offset(moved.x, 0, 0);

            moved = new Vec3d(moved.x, tryMove(world, bounds, 0, moved.y, 0).y, moved.z);
            bounds = bounds.offset(0, moved.y, 0);

            moved = new Vec3d(moved.x, moved.y, tryMove(world, bounds, 0, 0, moved.z).z);
            bounds = bounds.offset(0, 0, moved.z);

            // Apply friction and bounce
            if (moved.y != attempted.y) velocity = new Vec3d(velocity.x, -velocity.y * BOUNCE_FACTOR, velocity.z);
            if (moved.x != attempted.x) velocity = new Vec3d(velocity.x * FRICTION, velocity.y, velocity.z);
            if (moved.z != attempted.z) velocity = new Vec3d(velocity.x, velocity.y, velocity.z * FRICTION);

            position = position.add(moved);
        }

        private Vec3d tryMove(WorldAccess world, Box bounds, double dx, double dy, double dz) {
            Box newBox = bounds.offset(dx, dy, dz);
            if (!collides(world, newBox)) {
                return new Vec3d(dx, dy, dz);
            }
            return Vec3d.ZERO;
        }

        private boolean collides(WorldAccess world, Box box) {
            int minX = MathHelper.floor(box.minX);
            int maxX = MathHelper.ceil(box.maxX);
            int minY = MathHelper.floor(box.minY);
            int maxY = MathHelper.ceil(box.maxY);
            int minZ = MathHelper.floor(box.minZ);
            int maxZ = MathHelper.ceil(box.maxZ);

            BlockPos.Mutable pos = new BlockPos.Mutable();
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        pos.set(x, y, z);
                        BlockState state = world.getBlockState(pos);
                        if (!state.isAir() && state.getCollisionShape(world, pos).getBoundingBoxes().stream()
                                .anyMatch(bb -> Box.from(new BlockBox(pos)).offset(pos).intersects(box))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private Box getLocalBounds() {
            if (localBounds != null) return localBounds;
            Box b = null;
            for (BlockPos p : blocks.keySet()) {
                Box blockBox = new Box(p.getX(), p.getY(), p.getZ(),
                        p.getX() + 1, p.getY() + 1, p.getZ() + 1);
                b = (b == null) ? blockBox : b.union(blockBox);
            }
            localBounds = b;
            return b;
        }

        private Box getWorldBounds() {
            Box lb = getLocalBounds();
            return new Box(
                    lb.minX + position.x, lb.minY + position.y, lb.minZ + position.z,
                    lb.maxX + position.x, lb.maxY + position.y, lb.maxZ + position.z
            );
        }

        private void updateWorldBlocks(ServerWorld world) {
            for (var entry : blocks.entrySet()) {
                BlockPos local = entry.getKey();
                BlockState state = entry.getValue();
                BlockPos global = new BlockPos(
                        (int) (position.x + local.getX()),
                        (int) (position.y + local.getY()),
                        (int) (position.z + local.getZ())
                );
                world.setBlockState(global, state);
            }
        }

        public Vec3d getPosition() { return position; }
        public Vec3d getVelocity() { return velocity; }
        public Vec3d getRotation() { return rotation; }
        public Map<BlockPos, BlockState> getBlocks() { return blocks; }
        public void setVelocity(Vec3d v) { velocity = v; }
        public void addVelocity(Vec3d d) { velocity = velocity.add(d); }
    }

}

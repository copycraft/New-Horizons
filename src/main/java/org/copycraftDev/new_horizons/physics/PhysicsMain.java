package org.copycraftDev.new_horizons.physics;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.copycraftDev.new_horizons.core.entity.BlockColliderEntity;
import org.copycraftDev.new_horizons.core.entity.ModEntities;

import java.util.*;

public class PhysicsMain {
    public static final PhysicsManager PHYSICS_MANAGER = new PhysicsManager();

    public static class PhysicsManager {
        private static final List<PhysicsObject> activeObjects = new ArrayList<>();

        public PhysicsObject create(ServerWorld world, Vec3d pos, BlockPos origin) {
            PhysicsObject obj = new PhysicsObject(pos, origin);
            activeObjects.add(obj);
            return obj;
        }

        public void removeObject(PhysicsObject object) {
            activeObjects.remove(object);
        }

        public List<PhysicsObject> getAllObjects() {
            return activeObjects;
        }

        public static void tickAll(MinecraftServer server) {
            ServerWorld world = server.getOverworld();
            for (PhysicsObject obj : new ArrayList<>(activeObjects)) {
                obj.tick(world);
            }
        }
    }

    public static class PhysicsObject {
        private final BlockPos origin;
        private Vec3d position;
        private Vec3d velocity = Vec3d.ZERO;
        private Vec3d rotation = Vec3d.ZERO;
        private Box localBounds = null;

        private final Map<BlockPos, BlockState> blocks = new HashMap<>();
        private final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
        private final List<BlockPos> previousGlobals = new ArrayList<>();
        private final Map<PhysicsObject, Map<Vec3d, BlockColliderEntity>> colliders = new IdentityHashMap<>();

        public PhysicsObject(Vec3d startPos, BlockPos origin) {
            this.position = startPos;
            this.origin = origin;
        }

        public void setVelocity(Vec3d v) { velocity = v; }
        public void addVelocity(Vec3d dv) { velocity = velocity.add(dv); }
        public void setRotation(Vec3d rotDegrees) { rotation = rotDegrees; }
        public void addRotation(Vec3d dRot) { rotation = rotation.add(dRot); }

        public BlockPos getOrigin() { return origin; }
        public Vec3d getPosition() { return position; }
        public Vec3d getVelocity() { return velocity; }
        public Vec3d getRotation() { return rotation; }
        public boolean isAlive() { return true; }

        public Map<BlockPos, BlockState> getBlocks() { return blocks; }
        public Map<BlockPos, BlockEntity> getBlockEntities() { return blockEntities; }

        public void addBlock(BlockPos localPos, BlockState state, BlockEntity be) {
            blocks.put(localPos, state);
            if (be != null) blockEntities.put(localPos, be);
            localBounds = null;
        }

        public void tick(ServerWorld world) {
            for (PhysicsObject obj : PHYSICS_MANAGER.getAllObjects()) {
                Map<Vec3d, BlockColliderEntity> map = colliders.computeIfAbsent(obj, o -> new HashMap<>());

                for (var entry : obj.getBlocks().entrySet()) {
                    Vec3d offset = Vec3d.of(entry.getKey());
                    if (!map.containsKey(offset)) {
                        BlockColliderEntity c = new BlockColliderEntity(ModEntities.BLOCK_COLLIDER, world);
                        world.spawnEntity(c);
                        c.init(obj, offset);
                        map.put(offset, c);
                    }
                    map.get(offset).init(obj, offset);
                }

                map.keySet().removeIf(off -> {
                    boolean gone = !obj.getBlocks().containsKey(off);
                    if (gone) map.get(off).remove(Entity.RemovalReason.DISCARDED);
                    return gone;
                });

                obj.applyGravity();
                Vec3d attempted = obj.velocity;
                Vec3d moved = obj.doMovement(world, attempted);
                obj.position = obj.position.add(moved);
                obj.updateWorldBlocks(world);
            }
        }

        private void applyGravity() {
            velocity = velocity.add(0, PhysicsConfig.GRAVITY, 0);
            if (velocity.y < PhysicsConfig.TERMINAL_VELOCITY) {
                velocity = new Vec3d(velocity.x, PhysicsConfig.TERMINAL_VELOCITY, velocity.z);
            }
        }

        private Vec3d doMovement(WorldAccess world, Vec3d attempted) {
            Box bounds = getWorldBounds();
            Vec3d moved = attempted;

            moved = tryMove(world, bounds, moved.x, 0, 0);
            bounds = bounds.offset(moved.x, 0, 0);
            double my = tryMove(world, bounds, 0, moved.y, 0).y;
            moved = new Vec3d(moved.x, my, moved.z);
            bounds = bounds.offset(0, my, 0);
            double mz = tryMove(world, bounds, 0, 0, moved.z).z;
            moved = new Vec3d(moved.x, moved.y, mz);

            if (moved.y != attempted.y) velocity = new Vec3d(velocity.x, -velocity.y * PhysicsConfig.BOUNCE_FACTOR, velocity.z);
            if (moved.x != attempted.x) velocity = new Vec3d(velocity.x * PhysicsConfig.FRICTION, velocity.y, velocity.z);
            if (moved.z != attempted.z) velocity = new Vec3d(velocity.x, velocity.y, velocity.z * PhysicsConfig.FRICTION);

            return moved;
        }

        private Vec3d tryMove(WorldAccess world, Box bounds, double dx, double dy, double dz) {
            Box nb = bounds.offset(dx, dy, dz);
            return collides(world, nb) ? Vec3d.ZERO : new Vec3d(dx, dy, dz);
        }

        private boolean collides(WorldAccess world, Box box) {
            int minX = MathHelper.floor(box.minX), maxX = MathHelper.ceil(box.maxX);
            int minY = MathHelper.floor(box.minY), maxY = MathHelper.ceil(box.maxY);
            int minZ = MathHelper.floor(box.minZ), maxZ = MathHelper.ceil(box.maxZ);
            BlockPos.Mutable mpos = new BlockPos.Mutable();

            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        mpos.set(x, y, z);
                        BlockState s = world.getBlockState(mpos);
                        if (!s.isAir() && s.getCollisionShape(world, mpos).getBoundingBoxes().stream()
                                .anyMatch(bb -> Box.from(new BlockBox(mpos)).offset(mpos).intersects(box))) {
                            return true;
                        }
                    }
                }
            }

            Vec3d objPos = getPosition();
            for (BlockPos local : blocks.keySet()) {
                Box bb = new Box(
                        objPos.x + local.getX(),
                        objPos.y + local.getY(),
                        objPos.z + local.getZ(),
                        objPos.x + local.getX() + 1,
                        objPos.y + local.getY() + 1,
                        objPos.z + local.getZ() + 1
                );
                if (bb.intersects(box)) return true;
            }

            return false;
        }

        private Box getLocalBounds() {
            if (localBounds != null) return localBounds;
            Box b = null;
            for (BlockPos p : blocks.keySet()) {
                Box bb = new Box(p.getX(), p.getY(), p.getZ(),
                        p.getX() + 1, p.getY() + 1, p.getZ() + 1);
                b = (b == null) ? bb : b.union(bb);
            }
            localBounds = b;
            return b;
        }

        public Box getWorldBounds() {
            Box lb = getLocalBounds();
            return new Box(
                    lb.minX + position.x, lb.minY + position.y, lb.minZ + position.z,
                    lb.maxX + position.x, lb.maxY + position.y, lb.maxZ + position.z
            );
        }

        private void updateWorldBlocks(ServerWorld world) {
            previousGlobals.clear();

            for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
                BlockPos local = entry.getKey();
                BlockState state = entry.getValue();
                BlockPos global = new BlockPos(
                        (int) Math.floor(position.x) + local.getX(),
                        (int) Math.floor(position.y) + local.getY(),
                        (int) Math.floor(position.z) + local.getZ()
                );
                previousGlobals.add(global);
                // You may update blocks in the world here if needed
            }
        }
    }
}

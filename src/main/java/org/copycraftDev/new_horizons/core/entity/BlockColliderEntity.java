package org.copycraftDev.new_horizons.core.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.physics.PhysicsMain;

/**
 * Invisible 1×1×1 collider-entity synced to a PhysicsObject’s single block,
 * with optional light-blocking mimic based on a BlockState.
 */
public class BlockColliderEntity extends ShulkerEntity {
    private PhysicsMain.PhysicsObject linkedObject;
    private Vec3d localOffset;
    private BlockState mimicBlockState;

    public BlockColliderEntity(EntityType<? extends ShulkerEntity> type, World world) {
        super(type, world);
        this.setAiDisabled(true);
        this.setNoGravity(true);
        this.noClip = false;
    }

    /**
     * Initialize physics sync: call right after spawning.
     */

    public static void spawnBlockCollider(World world, PhysicsMain.PhysicsObject obj, Vec3d localOffset) {
        BlockColliderEntity collider = new BlockColliderEntity(ModEntities.BLOCK_COLLIDER, world);
        collider.init(obj, localOffset); // Sets position + links
        world.spawnEntity(collider);
    }

    @Override
    public boolean isCollidable() {
        return true; // Required for collision to register
    }

    @Override
    public boolean collidesWith(Entity other) {
        return true; // Allow other entities to collide
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        // Prevent pushback on collision
    }


    @Override
    public boolean isPushable() {
        return false; // We don't want this to be pushed
    }



    public void init(PhysicsMain.PhysicsObject obj, Vec3d offset) {
        this.linkedObject = obj;
        this.localOffset = offset;
        Vec3d w = obj.getPosition().add(offset);
        super.setPosition(w.x, w.y, w.z);
    }

    /**
     * Which block to mimic for light-blocking (luminance==0 == full block light-block).
     */
    public void setMimicBlockState(BlockState state) {
        this.mimicBlockState = state;
    }

    public BlockState getMimicBlockState() {
        return mimicBlockState;
    }

    @Override
    public void tick() {
        if (linkedObject == null || !linkedObject.isAlive()) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        Vec3d w = linkedObject.getPosition().add(localOffset);
        super.setPosition(w.x, w.y, w.z);
        // no super.tick() call: we only want collision sync
    }

    @Override
    public Box calculateBoundingBox() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        // 1×1×1 cube at our position
        return new Box(x, y, z, x + 1.0, y + 1.0, z + 1.0);
    }

    public boolean collides() {
        return true;
    }


    public boolean blocksLight() {
        return mimicBlockState != null && mimicBlockState.getLuminance() == 0;
    }

    /**
     * Must be registered via FabricDefaultAttributeRegistry to avoid NPE.
     */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return ShulkerEntity.createShulkerAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0);
    }
}

package org.copycraftDev.new_horizons.core.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.copycraftDev.new_horizons.physics.PhysicsMain;

/**
 * Invisible 1×1×1 collider‑entity synced to a PhysicsObject’s single block.
 */
public class BlockColliderEntity extends ShulkerEntity {
    private PhysicsMain.PhysicsObject linkedObject;
    private Vec3d localOffset;

    public BlockColliderEntity(EntityType<? extends ShulkerEntity> type, World world) {
        super(type, world);
        // disable AI, rendering, but keep collision
        this.setAiDisabled(true);
        this.setInvisible(true);
        this.noClip = false;
    }

    /**
     * Called once, right after spawning, to hook this collider to its PhysicsObject and block‑offset.
     */
    public void init(PhysicsMain.PhysicsObject obj, Vec3d offset) {
        this.linkedObject = obj;
        this.localOffset = offset;
        Vec3d w = obj.getPosition().add(offset);
        super.setPosition(w.x, w.y, w.z);
    }

    @Override
    public void tick() {
        if (linkedObject == null || !linkedObject.isAlive()) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        Vec3d w = linkedObject.getPosition().add(localOffset);
        super.setPosition(w.x, w.y, w.z);
        // we don’t call super.tick() AI or movement—only collision box matters
    }

    /**
     * Override the AABB to exactly a 1×1×1 cube at our current position
     */
    @Override
    public Box calculateBoundingBox() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        return new Box(x, y, z, x + 1.0, y + 1.0, z + 1.0);
    }

    /**
     * Must be registered via FabricDefaultAttributeRegistry—otherwise constructor NPEs.
     */
    public static DefaultAttributeContainer.Builder createAttributes() {
        // reuse the Shulker’s attributes (it adds the “covered armor” modifier, etc.)
        return ShulkerEntity.createShulkerAttributes()
                // you can also zero out health if you like:
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0);
    }
}

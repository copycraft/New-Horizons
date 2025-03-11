package org.copycraftDev.new_horizons.api;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * This interface is a boiled down version of the PhysicsWrapperEntity used in
 * the Valkyrien Warfare mod.
 *
 * @author thebest108
 *
 */
public interface IPhysicsEntity {

    /**
     * Rotates a vector from global to ship space, or from ship to global space.
     * Typically used when adjusting the velocity vector of entities.
     *
     * @param vector
     * @param transformType
     * @return
     */
    Vec3d rotateVector(Vec3d vector, TransformType transformType);

    /**
     * Transforms a vector from global to ship space, or from ship to global space.
     * Typically used when adjusting the position vector of entities.
     *
     * @param vector
     * @param transformType
     * @return
     */
    Vec3d transformVector(Vec3d vector, TransformType transformType);

}
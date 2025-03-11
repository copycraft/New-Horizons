package org.copycraftDev.new_horizons.api;


import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * This interface is used to get IPhysicsEntity objects. Do not make an
 * implementation of this, use the INSTANCE object provided to get
 * IPhysicsEntities.
 *
 * @author thebest108
 *
 */
public interface IPhysicsEntityManager {

    /**
     * This gets replaced at runtime if VW is installed.
     */
    public static final IPhysicsEntityManager INSTANCE = new DummyPhysicsEntityManager();

    /**
     * If the given BlockPos is in ship space then this returns the IPhysicsEntity
     * pertaining to the pos. If the BlockPos is not in ship space then returns
     * null.
     *
     * @param world
     * @param pos
     * @return
     */
    @Nullable
    IPhysicsEntity getPhysicsEntityFromShipSpace(World world, BlockPos pos);

    /**
     *
     * @param world
     * @param pos
     * @return true if the pos and world have a ship associated to them, false if
     *         otherwise.
     */
    default boolean isBlockPosManagedByPhysicsEntity(World world, BlockPos pos) {
        return getPhysicsEntityFromShipSpace(world, pos) != null;
    }
}
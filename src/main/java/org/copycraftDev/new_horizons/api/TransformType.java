package org.copycraftDev.new_horizons.api;

/**
 * A simple enum used to tell the ship transform which coordinates system we are
 * want to change to.
 * <p>
 * Ex. Moving from subspace to global at the center of mass will give us to the
 * position of the center of mass of the ship relative to the game world.
 *
 * @author thebest108
 */
public enum TransformType {

    GLOBAL_TO_SUBSPACE, SUBSPACE_TO_GLOBAL
}
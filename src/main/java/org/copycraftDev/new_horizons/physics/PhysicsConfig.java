package org.copycraftDev.new_horizons.physics;

import net.minecraft.util.math.Vec3d;

public class PhysicsConfig {
    public static final double GRAVITY = -0.4;
    public static final double DRAG = 0.998;
    public static final double TERMINAL_VELOCITY = 2.0;
    public static final Vec3d ROTATIONAL_DAMPING = new Vec3d(0, 0, 0);
    public static final double FRICTION = 1;
    public static final double BOUNCE_FACTOR = 0;
}

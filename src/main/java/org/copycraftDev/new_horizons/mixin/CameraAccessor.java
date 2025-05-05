package org.copycraftDev.new_horizons.mixin;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Invoker("setRotation")
    void invokeSetRotation(float yaw, float pitch);

    @Invoker("setPos")
    void invokeSetPos(double x, double y, double z);
}

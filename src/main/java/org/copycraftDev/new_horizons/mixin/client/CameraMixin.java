package org.copycraftDev.new_horizons.mixin.client;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliCameraManager;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
	@Shadow
	private Entity focusedEntity;
	@Shadow
	private Quaternionf rotation;

	private float storedTickDelta = 0.f;

	@Inject(method = "update", at = @At("HEAD"))
	private void inject_update(BlockView area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
		storedTickDelta = tickDelta;
	}

	@WrapOperation(
			method = "update",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V",
					ordinal = 0
			)
	)
	private void wrapOperation_setPos(Camera camera, double x, double y, double z, Operation<Void> original) {
		if (focusedEntity == null) {
			original.call(camera, x, y, z);
			return;
		}

		// Apply rough displacement
		Vec3d posDisplacement = LazuliCameraManager.getCameraDisplacement();
		double displacementX = posDisplacement.x;
		double displacementY = posDisplacement.y;
		double displacementZ = posDisplacement.z;

		original.call(camera, x + displacementX, y + displacementY, z + displacementZ);
	}


	// Shadow the fields for pitch and yaw from the Camera class.
	@Shadow private float pitch;

	@Shadow private float yaw;

	@Inject(method = "update", at = @At("HEAD"), cancellable = true)
	private void onUpdate(CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			// Check if the player is riding any entity.
			Entity vehicle = client.player.getVehicle();
			if (vehicle != null) {
				// Force pitch and yaw to 0.
				this.pitch = 0.0F;
				this.yaw = 0.0F;
				// Cancel the rest of the camera update.
				ci.cancel();
			}
		}
	}
}
// src/main/java/org/copycraftDev/new_horizons/mixin/AbstractBlockMixin.java
package org.copycraftDev.new_horizons.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.copycraftDev.new_horizons.physics.PhysicsMain;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(
            method = "getOutlineShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void addPhysicsOutline(
            BlockState state,
            BlockView world,
            BlockPos pos,
            ShapeContext ctx,
            CallbackInfoReturnable<VoxelShape> cir
    ) {
        VoxelShape original = cir.getReturnValue();
        boolean inPhysics = PhysicsMain.PHYSICS_MANAGER
                .getAllObjects().stream()
                .anyMatch(o -> o.getBlocks().containsKey(pos));
        if (inPhysics) {
            VoxelShape full = VoxelShapes.fullCube();
            cir.setReturnValue(VoxelShapes.union(original, full));
        }
    }
}

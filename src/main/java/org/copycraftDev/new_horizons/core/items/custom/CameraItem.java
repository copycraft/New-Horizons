package org.copycraftDev.new_horizons.core.items.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import org.copycraftDev.new_horizons.client.ShaderController;
import org.copycraftDev.new_horizons.client.rendering.ModShaders;
import org.copycraftDev.new_horizons.extrastuff.ZoomHandler;

import java.util.Map;
import java.util.WeakHashMap;

public class CameraItem extends Item {


    private static final Map<ItemStack, Boolean> cameraActiveMap = new WeakHashMap<>();

    public CameraItem(Settings settings) {
        super(settings);
    }


    private boolean isActive(ItemStack stack) {
        return cameraActiveMap.getOrDefault(stack, false);
    }


    private void setActive(ItemStack stack, boolean active) {
        if (active) {
            cameraActiveMap.put(stack, true);
        } else {
            cameraActiveMap.remove(stack);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (player.getMainHandStack() == stack) {
            if (isActive(stack)) {
                onCameraDeactivate(player, stack);
                setActive(stack, false);
            } else {
                onCameraActivate(player, stack);
                setActive(stack, true);
            }
        }

        return TypedActionResult.success(stack, false);
    }

    @Override
    public void inventoryTick(
            ItemStack stack,
            World world,
            net.minecraft.entity.Entity entity,
            int slot,
            boolean selected
    ) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            if (!selected && isActive(stack)) {
                onCameraDeactivate(player, stack);
                setActive(stack, false);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void onCameraActivate(PlayerEntity player, ItemStack stack) {
        ShaderController.setShader(ModShaders.VHS);
        if (!ZoomHandler.isZoomActive()){
            ZoomHandler.toggleZoom();
        } else {
            ZoomHandler.startZoom();
        }


    }
    @Environment(EnvType.CLIENT)
    private void onCameraDeactivate(PlayerEntity player, ItemStack stack) {
        ShaderController.setShader(ModShaders.VOID);        ZoomHandler.startZoom();
    }
}

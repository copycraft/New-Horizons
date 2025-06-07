package org.copycraftDev.new_horizons.core.items.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import org.copycraftDev.new_horizons.client.rendering.SniperHudRenderer;

public class SniperItem extends BowItem {

    public SniperItem(Settings settings) {
        super(settings);
    }

    // How long it can be charged (ticks)
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    // Called when right-click is held
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    // Called when right-click is released
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;

        if (!world.isClient) {
            float power = (float) Math.min(SniperHudRenderer.charge, 120.0f);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f + power * 0.5f);

                    if (power > 2f && user instanceof PlayerEntity player) {
                        ArrowEntity arrow = new ArrowEntity(EntityType.ARROW, world);

                        arrow.setPos(player.getX(), player.getEyeY(), player.getZ());

                        arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, (float) Math.min(power * 0.1, 20.0f), 0.0f);

                        arrow.setCritical(power >= 4f);
                        arrow.setDamage(2.0 + power * 2.0);
                        arrow.setNoClip(false);

                        world.spawnEntity(arrow);
                    }
                    power = 0.0f;
                    SniperHudRenderer.charge = 0.0f;
                }
            }




    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient) {
            int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;
            if (usedTicks<121) {
                SniperHudRenderer.charge = usedTicks;
            }
        }
    }

}

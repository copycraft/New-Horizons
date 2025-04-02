package org.copycraftDev.new_horizons.core;

//===============[ Imports ]=================
import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
@LibyAutoRegister(method = "registerThis")
public class TemplateClassCuzAzuraIsDumb {
	private static final Logger LOGGER = LoggerFactory.getLogger("philomagia");
	static double speed = 0;
	static Vec3d dir = new Vec3d(0,0,0);

	public static void registerThis() {
		//=======[ Main code ]=========================================
		LOGGER.info("yep here's a logger copy pasta");

		ServerTickEvents.START_SERVER_TICK.register((MinecraftServer server) -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if (player.getMainHandStack().getItem() == Items.STICK) {
					speed += (speed-1)*0.015;
					dir = player.getRotationVec(0);
				}

				if (player.getMainHandStack().getItem() == Items.STONE) {
					speed -= (speed)*0.08;

				}
				LazuliGeometryBuilder.rotatedSpaceDisplaceRenderingSpacePos(dir.multiply(speed));
			}
		});
	}
}

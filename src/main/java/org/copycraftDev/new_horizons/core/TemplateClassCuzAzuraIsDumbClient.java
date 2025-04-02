package org.copycraftDev.new_horizons.core;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.util.math.Vec3d;
import org.copycraftDev.new_horizons.client.NewHorizonsClient;
import org.copycraftDev.new_horizons.lazuli_snnipets.LazuliGeometryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateClassCuzAzuraIsDumbClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("philomagia");

	// These static fields store the current speed and direction of the geometry.
	static double speed = 0;
	static Vec3d direction = Vec3d.ZERO;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Client mod initializing: geometry will be controlled by arrow keys.");

		// Register a client tick event to update geometry movement based on arrow key input.

	}
}

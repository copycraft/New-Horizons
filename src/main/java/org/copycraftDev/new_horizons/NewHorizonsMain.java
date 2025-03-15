package org.copycraftDev.new_horizons;

import foundry.veil.Veil;
import nazario.liby.api.registry.auto.LibyEntrypoints;
import nazario.liby.api.registry.auto.LibyRegistryLoader;
import net.fabricmc.api.ModInitializer;
import org.copycraftDev.new_horizons.core.items.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewHorizonsMain implements ModInitializer {

    public static final String MOD_ID = "new_horizons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        LibyRegistryLoader.load("org.copycraftDev.new_horizons", LOGGER, LibyEntrypoints.MAIN);

        Veil.init();
    }
}

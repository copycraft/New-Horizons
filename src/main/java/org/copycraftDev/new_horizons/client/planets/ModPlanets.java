package org.copycraftDev.new_horizons.client.planets;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import org.copycraftDev.new_horizons.NewHorizonsMain;

@LibyAutoRegister(method = "registerPlanets")
public class ModPlanets {
    public static void registerPlanets(){
        NewHorizonsMain.LOGGER.info("Meoww~~ Azura hopes u have funn UwU");
        CelestialBodyRegistry.registerAllPlanets("new_horizons/planets", NewHorizonsMain.MOD_ID);
    }
}

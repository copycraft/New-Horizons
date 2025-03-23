package org.copycraftDev.new_horizons.core.particle;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.NewHorizonsMain;

@LibyAutoRegister(method = "init")
public class ModParticles {
    public static final SimpleParticleType FOG_PARTICLE = Registry.register(
            Registries.PARTICLE_TYPE,
            Identifier.of(NewHorizonsMain.MOD_ID, "fog"),
            FabricParticleTypes.simple()
    );

    public static void init() {
        NewHorizonsMain.LOGGER.info("Registering Mod Particles...");
    }
}

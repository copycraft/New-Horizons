package org.copycraftDev.new_horizons.client.particle;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.copycraftDev.new_horizons.core.particle.ModParticles;

@LibyAutoRegister(method = "init")
@Environment(EnvType.CLIENT)
public class ModParticlesClient {
    public static void init() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.FOG_PARTICLE, spriteProvider ->
                new FogParticle.Factory(spriteProvider)
        );
    }

    public static class FogParticle extends SpriteBillboardParticle {
        protected FogParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            super(world, x, y, z, velocityX, velocityY, velocityZ);
            this.scale = 2.0F;
            this.maxAge = 80;
            this.alpha = 0.5F;
        }

        @Override
        public ParticleTextureSheet getType() {
            return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
        }

        public static class Factory implements ParticleFactory<SimpleParticleType> {
            private final SpriteProvider spriteProvider;

            public Factory(SpriteProvider spriteProvider) {
                this.spriteProvider = spriteProvider;
            }

            @Override
            public Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
                FogParticle particle = new FogParticle(world, x, y, z, dx, dy, dz);
                particle.setSprite(spriteProvider);
                return particle;
            }
        }
    }
}

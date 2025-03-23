package org.copycraftDev.new_horizons.core.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class FogParticle extends SpriteBillboardParticle {
    public FogParticle(ClientWorld clientWorld, double x, double y, double z,
                       SpriteProvider spriteProvider, double xSpeed, double ySpeed, double zSpeed) {
        super(clientWorld, x, y, z, xSpeed, ySpeed, zSpeed);

        this.velocityMultiplier = 0.8f;

        // Set the particle to live for 300 ticks.
        this.maxAge = 300;

        // Increase the particle scale to make it giant.
        this.scale = 3.0f;

        this.setSpriteForAge(spriteProvider);

        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
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

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new FogParticle(world, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
        }
    }
}

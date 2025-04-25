package org.copycraftDev.new_horizons.core.bigbang;

import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BigBangManager {
    private static final List<BigBangParticleData> particles = new ArrayList<>();
    private static final Vec3d center = new Vec3d(0, 0, 0);
    private static final double collapseSpeed = 0.3;  // Faster collapse speed
    private static final double explodeSpeed = 3.0;   // Faster explosion speed
    private static final double threshold = 0.5;
    private static final double spawnRadius = 100;
    private static final double secondExplosionSpeed = 10.0; // Speed of the second explosion
    private static final int WAIT_TIME_TICKS = 40; // 2 seconds in ticks (20 ticks per second)

    private static int particleCount = 0;
    private static boolean finishedFirstPhase = false;
    private static int ticksAfterExplosion = 0;  // Counter to track the wait time

    public static void spawnParticles(MinecraftServer client, int amount, double radius) {
        Random random = Random.create();

        for (int i = 0; i < amount; i++) {
            // Random position inside a sphere of radius spawnRadius
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);
            double r = Math.cbrt(random.nextDouble()) * radius;

            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);

            Vec3d pos = new Vec3d(x, y, z);
            Vec3d explosionDir = new Vec3d(
                    random.nextDouble() * 2 - 1,
                    random.nextDouble() * 2 - 1,
                    random.nextDouble() * 2 - 1
            ).normalize();

            particles.add(new BigBangParticleData(pos, explosionDir));
        }

        particleCount = amount;
        finishedFirstPhase = false;
        ticksAfterExplosion = 0; // Reset wait timer
    }

    public static void tick(MinecraftClient client) {
        if (client.world == null) return;

        Iterator<BigBangParticleData> iterator = particles.iterator();

        boolean allCollapsed = true;

        // First, handle the collapse and explosion phases
        while (iterator.hasNext()) {
            BigBangParticleData data = iterator.next();

            if (!data.exploded) {
                // Collapse phase
                Vec3d dir = center.subtract(data.position).normalize().multiply(collapseSpeed);
                data.position = data.position.add(dir);

                // Use END_ROD for the light trails
                client.world.addParticle(ParticleTypes.END_ROD, data.position.x, data.position.y, data.position.z, 0, 0, 0);

                // Once the particle reaches the threshold distance, it will explode
                if (data.position.distanceTo(center) < threshold) {
                    data.exploded = true;
                    data.velocity = data.explosionDirection.multiply(explodeSpeed);

                    // Mini explosion burst at the center
                    for (int i = 0; i < 10; i++) {
                        double rx = (Math.random() - 0.5) * 0.5;
                        double ry = (Math.random() - 0.5) * 0.5;
                        double rz = (Math.random() - 0.5) * 0.5;
                        client.world.addParticle(ParticleTypes.EXPLOSION, center.x, center.y, center.z, rx, ry, rz);
                    }
                }

            } else {
                // Explosion phase: particles explode outward
                data.position = data.position.add(data.velocity);

                // Using Dust particles for a better visual explosion
                client.world.addParticle(
                        new DustParticleEffect(new Vec3d(1f, 1f, 1f).toVector3f(), 1.5f),  // White colored burst
                        data.position.x, data.position.y, data.position.z,
                        data.velocity.x * 0.1, data.velocity.y * 0.1, data.velocity.z * 0.1
                );

                if (data.ticksExploded % 5 == 0) {
                    client.world.addParticle(ParticleTypes.FIREWORK,
                            data.position.x, data.position.y, data.position.z,
                            Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
                }

                data.ticksExploded++;
                if (data.ticksExploded > 40) {
                    iterator.remove();  // Remove the particle after a while
                }
            }

            // If there are still particles that haven't exploded, we mark that some are still collapsing
            if (!data.exploded) {
                allCollapsed = false;
            }
        }

        // If all particles have collapsed and exploded, spawn new particles shooting out fast from the center
        if (allCollapsed && !finishedFirstPhase) {
            ticksAfterExplosion++; // Increase the timer each tick after explosion phase finishes
            if (ticksAfterExplosion >= WAIT_TIME_TICKS) {
                spawnSecondExplosion(client, particleCount);
                finishedFirstPhase = true;
            }
        }

        // Spawn outward lines after the second explosion sequence
        if (finishedFirstPhase && particles.isEmpty()) {
            spawnOutwardLines(client, particleCount, spawnRadius);
        }
    }

    private static void spawnSecondExplosion(MinecraftClient client, int amount) {
        Random random = Random.create();

        // Create and shoot new particles from the center with high velocity
        for (int i = 0; i < amount; i++) {
            Vec3d velocity = new Vec3d(
                    random.nextDouble() * 2 - 1,
                    random.nextDouble() * 2 - 1,
                    random.nextDouble() * 2 - 1
            ).normalize().multiply(secondExplosionSpeed);  // High speed for the second explosion

            Vec3d pos = center;  // Start from the center
            particles.add(new BigBangParticleData(pos, velocity));
        }
    }

    private static void spawnOutwardLines(MinecraftClient client, int amount, double radius) {
        Random random = Random.create();

        // Spawn outward lines that mirror the incoming lines (but go out fast)
        for (int i = 0; i < amount; i++) {
            // Same as initial particle position but shooting outward fast
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);
            double r = Math.cbrt(random.nextDouble()) * radius;

            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);

            Vec3d pos = center;  // Start from the center
            Vec3d direction = new Vec3d(x, y, z).normalize().multiply(5);  // Fast outward direction

            // Add the outward line particles with high speed
            client.world.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, direction.x, direction.y, direction.z);
        }
    }

    public static int getParticleCount() {
        return particleCount;
    }

    private static class BigBangParticleData {
        Vec3d originalPosition;
        Vec3d position;
        Vec3d velocity = Vec3d.ZERO;
        Vec3d explosionDirection;
        boolean exploded = false;
        int ticksExploded = 0;

        BigBangParticleData(Vec3d position, Vec3d explosionDirection) {
            this.originalPosition = position;
            this.position = position;
            this.explosionDirection = explosionDirection;
        }
    }
}

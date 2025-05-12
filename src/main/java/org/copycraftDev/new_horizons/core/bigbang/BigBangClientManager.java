package org.copycraftDev.new_horizons.core.bigbang;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Client‐side Big Bang particle manager.
 *
 * Usage:
 *   // On some client tick event:
 *   BigBangClientManager.tick(client.world);
 *
 *   // To start explosion:
 *   BigBangClientManager.spawnParticles(amount, radius);
 */
public class BigBangClientManager {
    private static final List<BigBangParticleData> particles = new ArrayList<>();
    private static final Vec3d CENTER = Vec3d.ZERO;
    private static final double COLLAPSE_SPEED = 0.3;
    private static final double EXPLODE_SPEED = 3.0;
    private static final double THRESHOLD = 0.5;
    private static final double SPAWN_RADIUS = 100;
    private static final double SECOND_EXPLOSION_SPEED = 10.0;
    private static final int WAIT_TIME_TICKS = 40; // 2 seconds (20 ticks/sec)

    private static int particleCount = 0;
    private static boolean finishedFirstPhase = false;
    private static boolean finishedSecondPhase = false;
    private static int ticksAfterExplosion = 0;

    /**
     * Initialize a Big Bang effect with the given number of particles and spawn radius.
     */
    public static void spawnParticles(int amount, double radius) {
        Random random = Random.create();
        particles.clear();
        for (int i = 0; i < amount; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);
            double r = Math.cbrt(random.nextDouble()) * radius;

            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);

            Vec3d pos = new Vec3d(x, y, z);
            Vec3d dir = new Vec3d(
                    random.nextDouble() * 2 - 1,
                    random.nextDouble() * 2 - 1,
                    random.nextDouble() * 2 - 1
            ).normalize();

            particles.add(new BigBangParticleData(pos, dir));
        }

        particleCount = amount;
        finishedFirstPhase = false;
        finishedSecondPhase = false;
        ticksAfterExplosion = 0;
    }

    /**
     * Update and render the Big Bang particles. Call this each client tick.
     */
    public static void tick(ClientWorld world) {
        if (world == null) return;

        Iterator<BigBangParticleData> it = particles.iterator();
        boolean allCollapsed = true;

        while (it.hasNext()) {
            BigBangParticleData data = it.next();

            if (!data.exploded) {
                // Collapse inward
                Vec3d move = CENTER.subtract(data.position).normalize().multiply(COLLAPSE_SPEED);
                data.position = data.position.add(move);
                world.addParticle(ParticleTypes.WAX_OFF,
                        data.position.x, data.position.y, data.position.z,
                        0.0, 0.0, 0.0);

                if (data.position.distanceTo(CENTER) < THRESHOLD) {
                    data.exploded = true;
                    data.velocity = data.explosionDirection.multiply(EXPLODE_SPEED);
                    // Mini‐burst
                    for (int i = 0; i < 10; i++) {
                        double rx = (Math.random() - 0.5) * 0.5;
                        double ry = (Math.random() - 0.5) * 0.5;
                        double rz = (Math.random() - 0.5) * 0.5;
                        world.addParticle(ParticleTypes.EXPLOSION,
                                CENTER.x, CENTER.y, CENTER.z,
                                rx, ry, rz);
                    }
                }
            } else {
                // Explosion outward
                data.position = data.position.add(data.velocity);
                world.addParticle(new DustParticleEffect(new Vec3d(1f,1f,1f).toVector3f(), 1.5f),
                        data.position.x, data.position.y, data.position.z,
                        data.velocity.x * 0.1, data.velocity.y * 0.1, data.velocity.z * 0.1);

                if (data.ticksExploded % 5 == 0) {
                    world.addParticle(ParticleTypes.FIREWORK,
                            data.position.x, data.position.y, data.position.z,
                            Math.random() - 0.5,
                            Math.random() - 0.5,
                            Math.random() - 0.5);
                }

                data.ticksExploded++;
                if (data.ticksExploded > 40) {
                    it.remove();
                }
            }

            if (!data.exploded) allCollapsed = false;
        }

        // After collapse, wait then second blast
        if (allCollapsed && !finishedFirstPhase) {
            ticksAfterExplosion++;
            if (ticksAfterExplosion >= WAIT_TIME_TICKS) {
                spawnSecondExplosion(world, particleCount);
                finishedFirstPhase = true;
            }
        }

        // Final outward lines (once)
        if (finishedFirstPhase && particles.isEmpty() && !finishedSecondPhase) {
            spawnOutwardLines(world, particleCount, SPAWN_RADIUS);
            finishedSecondPhase = true;
        }
    }

    private static void spawnSecondExplosion(ClientWorld world, int amount) {
        Random random = Random.create();
        for (int i = 0; i < amount; i++) {
            Vec3d vel = new Vec3d(
                    random.nextDouble()*2 - 1,
                    random.nextDouble()*2 - 1,
                    random.nextDouble()*2 - 1
            ).normalize().multiply(SECOND_EXPLOSION_SPEED);

            BigBangParticleData data = new BigBangParticleData(CENTER, Vec3d.ZERO);
            data.velocity = vel;
            data.exploded = true;
            particles.add(data);
        }
    }

    private static void spawnOutwardLines(ClientWorld world, int amount, double radius) {
        Random random = Random.create();
        for (int i = 0; i < amount; i++) {
            double theta = random.nextDouble()*2*Math.PI;
            double phi = Math.acos(2*random.nextDouble() - 1);
            double r = Math.cbrt(random.nextDouble())*radius;

            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);
            Vec3d dir = new Vec3d(x, y, z).normalize().multiply(5);

            world.addParticle(ParticleTypes.END_ROD,
                    CENTER.x, CENTER.y, CENTER.z,
                    dir.x, dir.y, dir.z);
        }
    }

    private static class BigBangParticleData {
        Vec3d position;
        Vec3d velocity = Vec3d.ZERO;
        Vec3d explosionDirection;
        boolean exploded = false;
        int ticksExploded = 0;

        BigBangParticleData(Vec3d pos, Vec3d dir) {
            this.position = pos;
            this.explosionDirection = dir;
        }
    }
}
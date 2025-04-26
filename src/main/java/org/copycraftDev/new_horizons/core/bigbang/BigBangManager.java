package org.copycraftDev.new_horizons.core.bigbang;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@LibyAutoRegister(method = "register")
public class BigBangManager {
    private static final List<BigBangParticleData> particles = new ArrayList<>();
    private static final Vec3d CENTER = Vec3d.ZERO;
    private static final double COLLAPSE_SPEED = 0.3;
    private static final double EXPLODE_SPEED  = 3.0;
    private static final double THRESHOLD      = 0.5;
    private static final double SPAWN_RADIUS   = 100;
    private static final double SECOND_EXPLOSION_SPEED = 10.0;
    private static final int WAIT_TIME_TICKS = 40; // 2 seconds (20 ticks/sec)

    private static int particleCount = 0;
    private static boolean finishedFirstPhase = false;
    private static int ticksAfterExplosion = 0;

    // Register server tick handler
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(BigBangManager::tick);
    }

    // Trigger initial collapse & explosion
    public static void spawnParticles(MinecraftServer server, int amount, double radius) {
        Random random = Random.create();
        ServerWorld world = server.getWorld(World.OVERWORLD);

        particles.clear();
        for (int i = 0; i < amount; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi   = Math.acos(2 * random.nextDouble() - 1);
            double r     = Math.cbrt(random.nextDouble()) * radius;

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
        ticksAfterExplosion = 0;
    }

    // Called every server tick
    private static void tick(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) return;

        Iterator<BigBangParticleData> it = particles.iterator();
        boolean allCollapsed = true;

        while (it.hasNext()) {
            BigBangParticleData data = it.next();

            if (!data.exploded) {
                // Collapse inward
                Vec3d move = CENTER.subtract(data.position).normalize().multiply(COLLAPSE_SPEED);
                data.position = data.position.add(move);

                world.spawnParticles(
                        ParticleTypes.WAX_OFF,
                        data.position.x, data.position.y, data.position.z,
                        1,      // count
                        0.0, 0.0, 0.0,  // dx, dy, dz
                        0.0    // speed
                );

                if (data.position.distanceTo(CENTER) < THRESHOLD) {
                    data.exploded = true;
                    data.velocity = data.explosionDirection.multiply(EXPLODE_SPEED);
                    // Mini‐burst at center
                    for (int i = 0; i < 10; i++) {
                        double rx = (Math.random() - 0.5) * 0.5;
                        double ry = (Math.random() - 0.5) * 0.5;
                        double rz = (Math.random() - 0.5) * 0.5;
                        world.spawnParticles(
                                ParticleTypes.EXPLOSION,
                                CENTER.x, CENTER.y, CENTER.z,
                                1,    // count
                                rx, ry, rz,
                                1.0  // speed
                        );
                    }
                }
            } else {
                // Explosion outward
                data.position = data.position.add(data.velocity);

                world.spawnParticles(
                        new DustParticleEffect(
                                new Vec3d(1f, 1f, 1f).toVector3f(),
                                1.5f
                        ),
                        data.position.x, data.position.y, data.position.z,
                        1,  // count
                        data.velocity.x * 0.1,
                        data.velocity.y * 0.1,
                        data.velocity.z * 0.1,
                        1.0
                );

                if (data.ticksExploded % 5 == 0) {
                    world.spawnParticles(
                            ParticleTypes.FIREWORK,
                            data.position.x, data.position.y, data.position.z,
                            1,
                            Math.random() - 0.5,
                            Math.random() - 0.5,
                            Math.random() - 0.5,
                            1.0
                    );
                }

                data.ticksExploded++;
                if (data.ticksExploded > 40) {
                    it.remove();
                }
            }

            if (!data.exploded) allCollapsed = false;
        }

        // After all exploded, wait then do second blast
        if (allCollapsed && !finishedFirstPhase) {
            ticksAfterExplosion++;
            if (ticksAfterExplosion >= WAIT_TIME_TICKS) {
                spawnSecondExplosion(world, particleCount);
                finishedFirstPhase = true;
            }
        }

        // Final outward lines
        if (finishedFirstPhase && particles.isEmpty()) {
            spawnOutwardLines(world, particleCount, SPAWN_RADIUS);
        }
    }

    // Instant second blast from center
    private static void spawnSecondExplosion(ServerWorld world, int amount) {
        Random random = Random.create();
        for (int i = 0; i < amount; i++) {
            Vec3d vel = new Vec3d(
                    random.nextDouble()*2 - 1,
                    random.nextDouble()*2 - 1,
                    random.nextDouble()*2 - 1
            ).normalize().multiply(SECOND_EXPLOSION_SPEED);

            BigBangParticleData data = new BigBangParticleData(CENTER, Vec3d.ZERO);
            data.velocity = vel;
            data.exploded = true; // skip collapse
            particles.add(data);
        }
    }

    // Radial streaks outward
    private static void spawnOutwardLines(ServerWorld world, int amount, double radius) {
        Random random = Random.create();
        for (int i = 0; i < amount; i++) {
            double theta = random.nextDouble()*2*Math.PI;
            double phi   = Math.acos(2*random.nextDouble() - 1);
            double r     = Math.cbrt(random.nextDouble())*radius;

            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);
            Vec3d dir = new Vec3d(x, y, z).normalize().multiply(5);

            world.spawnParticles(
                    ParticleTypes.END_ROD,
                    CENTER.x, CENTER.y, CENTER.z,
                    1,  // count
                    dir.x, dir.y, dir.z,
                    1.0
            );
        }
    }

    // Particle tracking
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
package org.copycraftDev.new_horizons.extrastuff.noise.map.generator.cellular;

import org.copycraftDev.new_horizons.extrastuff.noise.map.Grid;
import org.copycraftDev.new_horizons.extrastuff.noise.map.generator.AbstractGenerator;
import org.copycraftDev.new_horizons.extrastuff.noise.map.generator.util.Generators;

import java.util.Random;

/** Contains a marker – a single float value; every cell below this value is considered dead, the others are alive.
 *  During each iteration, if a living cell has too few living neighbors, it will die (marker will be subtracted from its
 *  value). If a dead cell has enough living neighbors, it will become alive (marker will modify its current value
 *  according to {@link #getMode()} – by default, marker will be added). This usually results in a cave‑like map. The
 *  more iterations, the smoother the map is.
 */
public class CellularAutomataGenerator extends AbstractGenerator implements Grid.CellConsumer {
    private static CellularAutomataGenerator INSTANCE;

    private boolean initiate = true;
    private float marker = 1f;
    private float aliveChance = 0.5f;
    private int iterationsAmount = 3;
    private int birthLimit = 4;
    private int deathLimit = 3;
    private int radius = 1;
    private Grid temporaryGrid;

    public static void generate(final Grid grid, final int iterationsAmount) {
        generate(grid, iterationsAmount, 1f, true);
    }

    public static void generate(final Grid grid, final int iterationsAmount, final float marker,
                                final boolean initiate) {
        final CellularAutomataGenerator generator = getInstance();
        generator.setIterationsAmount(iterationsAmount);
        generator.setMarker(marker);
        generator.setInitiate(initiate);
        generator.generate(grid);
    }

    public static CellularAutomataGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CellularAutomataGenerator();
        }
        return INSTANCE;
    }

    public int getIterationsAmount() {
        return iterationsAmount;
    }

    public void setIterationsAmount(final int iterationsAmount) {
        this.iterationsAmount = iterationsAmount;
    }

    public float getAliveChance() {
        return aliveChance;
    }

    public void setAliveChance(final float aliveChance) {
        this.aliveChance = aliveChance;
    }

    public boolean isInitiating() {
        return initiate;
    }

    public void setInitiate(final boolean initiate) {
        this.initiate = initiate;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(final int radius) {
        this.radius = radius;
    }

    public float getMarker() {
        return marker;
    }

    // FIXED: assign directly to field, do not call setter recursively
    public void setMarker(final float marker) {
        this.marker = marker;
    }

    public int getBirthLimit() {
        return birthLimit;
    }

    public void setBirthLimit(final int birthLimit) {
        this.birthLimit = birthLimit;
    }

    public int getDeathLimit() {
        return deathLimit;
    }

    public void setDeathLimit(final int deathLimit) {
        this.deathLimit = deathLimit;
    }

    @Override
    public void generate(final Grid grid) {
        if (initiate) {
            spawnLivingCells(grid);
        }
        temporaryGrid = grid.copy();
        for (int i = 0; i < iterationsAmount; i++) {
            grid.forEach(this);
            grid.set(temporaryGrid);
        }
    }

    protected void spawnLivingCells(final Grid grid) {
        initiate(grid, aliveChance, marker);
    }

    public static void initiate(final Grid grid, final float aliveChance, final float marker) {
        final Random random = Generators.getRandom();
        final float[] array = grid.getArray();
        for (int i = 0; i < array.length; i++) {
            if (random.nextFloat() > aliveChance) {
                if (array[i] < marker) {
                    grid.add(grid.toX(i), grid.toY(i), marker);
                }
            } else if (array[i] >= marker) {
                grid.subtract(grid.toX(i), grid.toY(i), marker);
            }
        }
    }

    @Override
    public boolean consume(final Grid grid, final int x, final int y, final float value) {
        int living = countLivingNeighbors(grid, x, y);
        if (value >= marker) {
            if (living < deathLimit) {
                temporaryGrid.subtract(x, y, marker);
            }
        } else {
            if (living > birthLimit) {
                temporaryGrid.add(x, y, marker);
            }
        }
        return CONTINUE;
    }

    protected int countLivingNeighbors(final Grid grid, final int x, final int y) {
        int count = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx, ny = y + dy;
                if (grid.isIndexValid(nx, ny) && grid.get(nx, ny) >= marker) {
                    count++;
                }
            }
        }
        return count;
    }
}

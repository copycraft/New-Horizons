package org.copycraftDev.new_horizons.extrastuff.noise.map;

import org.copycraftDev.new_horizons.extrastuff.noise.array.Array2D;

import java.util.Arrays;

/** A float‐array wrapper that treats a 1D float[] as a 2D grid—but now wraps coordinates instead of limiting them. */
public class Grid extends Array2D {
    private final float[] grid;

    public Grid(final int size) {
        this(size, size);
    }

    public Grid(final int width, final int height) {
        this(new float[width * height], width, height);
    }

    public Grid(final float initialValue, final int width, final int height) {
        this(new float[width * height], width, height);
        set(initialValue);
    }

    public Grid(final float[] grid, final int width, final int height) {
        super(width, height);
        this.grid = grid;
        if (grid.length != width * height) {
            throw new IllegalArgumentException("Array with length: " + grid.length
                    + " is too small or too big to store a grid with " + width + " columns and " + height + " rows.");
        }
    }

    public float[] getArray() {
        return grid;
    }

    /** Wrap‑aware indexing: maps any (x,y) into valid [0..width-1]×[0..height-1]. */
    @Override
    public int toIndex(int x, int y) {
        x = ((x % width) + width) % width;
        y = ((y % height) + height) % height;
        return x + y * width;
    }

    public float get(final int x, final int y) {
        return grid[toIndex(x, y)];
    }

    public float set(final int x, final int y, final float value) {
        return grid[toIndex(x, y)] = value;
    }

    public float add(final int x, final int y, final float value) {
        return grid[toIndex(x, y)] += value;
    }

    public float subtract(final int x, final int y, final float value) {
        return grid[toIndex(x, y)] -= value;
    }

    public float multiply(final int x, final int y, final float value) {
        return grid[toIndex(x, y)] *= value;
    }

    public float divide(final int x, final int y, final float value) {
        return grid[toIndex(x, y)] /= value;
    }

    public float modulo(final int x, final int y, final float mod) {
        return grid[toIndex(x, y)] %= mod;
    }

    public void forEach(final CellConsumer cellConsumer) {
        iterate(cellConsumer, 0, grid.length);
    }

    public void forEach(final CellConsumer cellConsumer, final int fromX, final int fromY) {
        iterate(cellConsumer, toIndex(fromX, fromY), grid.length);
    }

    public void forEach(final CellConsumer cellConsumer, final int fromX, final int fromY, final int toX,
                        final int toY) {
        iterate(cellConsumer, toIndex(fromX, fromY), toIndex(toX, toY));
    }

    protected void iterate(final CellConsumer cellConsumer, final int fromIndex, final int toIndex) {
        for (int index = fromIndex; index < toIndex; index++) {
            if (cellConsumer.consume(this, toX(index), toY(index), grid[index])) {
                break;
            }
        }
    }

    public void set(final Grid grid) {
        validateGrid(grid);
        System.arraycopy(grid.grid, 0, this.grid, 0, this.grid.length);
    }

    public void add(final Grid grid) {
        validateGrid(grid);
        for (int index = 0, length = this.grid.length; index < length; index++) {
            this.grid[index] += grid.grid[index];
        }
    }

    public void subtract(final Grid grid) {
        validateGrid(grid);
        for (int index = 0, length = this.grid.length; index < length; index++) {
            this.grid[index] -= grid.grid[index];
        }
    }

    public void multiply(final Grid grid) {
        validateGrid(grid);
        for (int index = 0, length = this.grid.length; index < length; index++) {
            this.grid[index] *= grid.grid[index];
        }
    }

    public void divide(final Grid grid) {
        validateGrid(grid);
        for (int index = 0, length = this.grid.length; index < length; index++) {
            this.grid[index] /= grid.grid[index];
        }
    }

    protected void validateGrid(final Grid grid) {
        if (grid.width != width || grid.height != height) {
            throw new IllegalStateException("Grid's sizes do not match. Unable to perform operation.");
        }
    }

    public Grid set(final float value) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] = value;
        }
        return this;
    }

    public Grid fill(final float value) {
        return set(value);
    }

    public Grid fillColumn(final int x, final float value) {
        for (int y = 0; y < height; y++) {
            grid[toIndex(x, y)] = value;
        }
        return this;
    }

    public Grid fillRow(final int y, final float value) {
        for (int x = 0; x < width; x++) {
            grid[toIndex(x, y)] = value;
        }
        return this;
    }

    public Grid add(final float value) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] += value;
        }
        return this;
    }

    public Grid subtract(final float value) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] -= value;
        }
        return this;
    }

    public Grid multiply(final float value) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] *= value;
        }
        return this;
    }

    public Grid divide(final float value) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] /= value;
        }
        return this;
    }

    public Grid modulo(final float modulo) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] %= modulo;
        }
        return this;
    }

    public Grid negate() {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] = -grid[index];
        }
        return this;
    }

    public Grid clamp(final float min, final float max) {
        for (int index = 0, length = grid.length; index < length; index++) {
            final float value = grid[index];
            grid[index] = value > max ? max : value < min ? min : value;
        }
        return this;
    }

    public Grid replace(final float value, final float withValue) {
        for (int index = 0, length = grid.length; index < length; index++) {
            if (Float.compare(grid[index], value) == 0) {
                grid[index] = withValue;
            }
        }
        return this;
    }

    public Grid increment() {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index]++;
        }
        return this;
    }

    public Grid decrement() {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index]--;
        }
        return this;
    }

    @Override
    public boolean equals(final Object object) {
        return object == this || object instanceof Grid && ((Grid) object).width == width
                && Arrays.equals(((Grid) object).grid, grid);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(grid);
    }

    public Grid copy() {
        final float[] copy = new float[grid.length];
        System.arraycopy(grid, 0, copy, 0, copy.length);
        return new Grid(copy, width, height);
    }

    @Override
    public String toString() {
        final StringBuilder logger = new StringBuilder();
        forEach(new CellConsumer() {
            @Override
            public boolean consume(final Grid grid, final int x, final int y, final float value) {
                logger.append('[').append(x).append(',').append(y).append('|').append(value).append(']');
                if (x == grid.width - 1) {
                    logger.append('\n');
                } else {
                    logger.append(' ');
                }
                return CONTINUE;
            }
        });
        return logger.toString();
    }

    public static interface CellConsumer {
        boolean BREAK = true, CONTINUE = false;

        boolean consume(Grid grid, int x, int y, float value);
    }
}

package org.copycraftDev.new_horizons.lazuli_snnipets;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import org.joml.Matrix4f;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LazuliRenderEvents {
    private static final List<LazuliRenderCallback> CALLBACKS = new CopyOnWriteArrayList<>();

    public static void register(LazuliRenderCallback callback) {
        CALLBACKS.add(callback);
    }

    @FunctionalInterface
    public interface LazuliRenderCallback {
        void render(WorldRenderContext context, Matrix4f viewProjectionMatrix, float tickDelta);
    }
}

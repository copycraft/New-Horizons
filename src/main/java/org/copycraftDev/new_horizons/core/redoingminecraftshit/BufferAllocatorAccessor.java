package org.copycraftDev.new_horizons.core.redoingminecraftshit;

import java.lang.reflect.Field;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.BufferAllocator;

public class BufferAllocatorAccessor {
    private static final BufferAllocator INSTANCE = new BufferAllocator(1024);

    public static BufferAllocator getInstance() {
        return INSTANCE;
    }
}

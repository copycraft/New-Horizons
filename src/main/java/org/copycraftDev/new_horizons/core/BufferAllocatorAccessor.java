package org.copycraftDev.new_horizons.core;

import java.lang.reflect.Field;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.BufferAllocator;

public class BufferAllocatorAccessor {
    public static BufferAllocator getBufferAllocator() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            Field field = MinecraftClient.class.getDeclaredField("bufferAllocator");
            field.setAccessible(true);
            return (BufferAllocator) field.get(client);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}


package org.copycraftDev.new_horizons.core.portal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.api.PortalAPI;
import qouteall.imm_ptl.core.chunk_loading.ChunkLoader;
import qouteall.imm_ptl.core.chunk_loading.DimensionalChunkPos;
import qouteall.imm_ptl.core.portal.Portal;

import java.util.Collections;

public class TeleportPortal extends Portal {
    public TeleportPortal(EntityType<?> type, World world) {
        super(type, world);
    }
    private boolean isOpen = true;


    @Override
    public void tick() {
        super.tick();

        // Example: keep chunks loaded server-side once per second
        if (!this.getWorld().isClient) {
            // TODO: replace with your actual caster lookup
            ServerPlayerEntity player = /* your code to find the linked player */ null;
            if (player != null) {
                ChunkPos cp = new ChunkPos(
                        this.getBlockX() >> 4,
                        this.getBlockZ() >> 4
                );
                DimensionalChunkPos dcp = new DimensionalChunkPos(
                        this.getWorld().getRegistryKey(),
                        cp.x, cp.z
                );
                PortalAPI.addChunkLoaderForPlayer(player, new ChunkLoader(dcp, 1));
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("IsOpen", this.isOpen);
        // write additional fields here...
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.isOpen = nbt.getBoolean("IsOpen");
        // read additional fields here...
    }
    public boolean isOpen() {
        return this.isOpen;
    }

    public void onEntityCollision(Entity entity) {
        // Only teleport real players on the server
        if (!this.getWorld().isClient && entity instanceof ServerPlayerEntity sp) {
            ServerWorld sw = (ServerWorld) this.getWorld();
            // Teleport signature now requires a Set of PositionFlags:
            sp.teleport(
                    sw,
                    entity.getX(),    // x
                    100.0,            // y
                    entity.getZ(),    // z

                    Collections.emptySet(), // no flags
                    entity.getYaw(),  // yaw
                    entity.getPitch()
            );
        }
    }
    public void setOpen(boolean open) {
        // TODO: implement portal open state handling
    }
}
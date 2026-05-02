package com.onewhohears.onewholibs.entity;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.common.core.SimulatedEntityManager;
import com.onewhohears.onewholibs.util.UtilEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.UUID;

/**
 * Add this interface to any {@link Entity} class if you want it to tick while unloaded.
 * MUST ADD {@code SimulatedEntity.super.onVanillaTick()} TO THE TOP OF YOUR {@link Entity#tick()} OVERRIDE.
 * @author 1whohears
 */
public interface SimulatedEntity {
    Logger LOGGER = LogUtils.getLogger();
    /**
     * MUST ADD {@code SimulatedEntity.super.onVanillaTick()} TO THE TOP OF YOUR TICK OVERRIDE
     */
    default void onVanillaTick() {
        if (!isClientSide()) {
            if (getLastServerTick() <= 0) {
                if (!startSimulate()) {
                    kill();
                    LOGGER.info("SIMULATED ENTITY ALREADY EXISTS KILL {} {}", getId(), getUUID());
                    return;
                }
            }
            setLastServerTick(getWorld().getGameTime());
        }
    }
    void onAlwaysTickPre(@NotNull MinecraftServer server);
    /**
     * Always gets called whether the entity is in ticking range or not.
     * Called on Server Tick Post after {@link #onSimulatedTick(MinecraftServer)} and {@link #onVanillaTick()}
     */
    default void onAlwaysTickPost(@NotNull MinecraftServer server) {}
    /**
     * Called only when the entity is not being ticked by the server.
     * Called on Server Tick Post before {@link #onAlwaysTickPost(MinecraftServer)}.
     */
    void onSimulatedTick(@NotNull MinecraftServer server);
    /**
     * used internally
     */
    default void onServerTickPost(@NotNull MinecraftServer server) {
        onAlwaysTickPost(server);
    }
    /**
     * used internally
     */
    default void onServerTickPre(@NotNull MinecraftServer server) {
        onAlwaysTickPre(server);
        if (isUnloaded()) {
            if (checkRevive(server)) return;
            ++((Entity)this).tickCount;
            onSimulatedTick(server);
        }
    }
    /**
     * used internally
     */
    default boolean startSimulate() {
        if (!isClientSide()) {
            return SimulatedEntityManager.get().startSimulatingEntity(this);
        }
        return false;
    }
    default boolean checkRevive(@NotNull MinecraftServer server) {
        ServerLevel sl = (ServerLevel) getWorld();
        Entity entity = (Entity) this;
        ServerChunkCache scc = sl.getChunkSource();
        boolean inTickRange = scc.chunkMap.getDistanceManager().inEntityTickingRange(entity.chunkPosition().toLong());
        ChunkPos cp = entity.chunkPosition();
        boolean hasChunk = sl.hasChunk(cp.x, cp.z);
        if (hasChunk && inTickRange) {
            try {
                UtilEntity.revive(entity);
                sl.addFreshEntity(entity);
            } catch (Exception e) {
                SimulatedEntityManager.get().stopSimulatingEntity(this);
                LOGGER.error("Failed to revive simulated entity. Canceling future attempts to simulate: {} | {}",
                        entity, e.getMessage());
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
    /**
     * @return true if the entity is not being ticked by the server
     */
    default boolean isUnloaded() {
        if (isClientSide()) return false;
        return getWorld().getGameTime() > getLastServerTick();
    }
    /**
     * used internally. simply add a private field to your entity.
     */
    long getLastServerTick();
    /**
     * used internally. simply add a private field to your entity.
     */
    void setLastServerTick(long tick);
    default Level getWorld() {
        return UtilEntity.getLevel((Entity)this);
    }
    default boolean isStopSimulating() {
        Entity.RemovalReason reason = ((Entity)this).getRemovalReason();
        return reason == Entity.RemovalReason.KILLED;
    }
    default int getId() {
        return ((Entity)this).getId();
    }
    default UUID getUUID() {
        return ((Entity)this).getUUID();
    }
    default boolean isClientSide() {
        return getWorld().isClientSide();
    }
    default void kill() {
        ((Entity)this).kill();
    }
}

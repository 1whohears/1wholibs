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
 * If this entity is initially spawned in an unloaded chunk,
 * you will have to manually call {@link #startSimulate()} after creating the entity.
 * @author 1whohears
 */
public interface SimulatedEntity {
    Logger LOGGER = LogUtils.getLogger();
    /**
     * MUST ADD {@code SimulatedEntity.super.onVanillaTick()} TO THE TOP OF YOUR TICK OVERRIDE
     */
    default void onVanillaTick() {
        if (!isClientSide()) {
            if (isAutoStartSimulateOnVanillaTick() && getLastServerTick() <= 0 && !startSimulate()) {
                return;
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
     * Used by {@link #onVanillaTick()} if {@link #isAutoStartSimulateOnVanillaTick()} is true.
     * If false this must be called on entity creation.
     * It must also be called if the entity is initially spawned outside loaded chunks.
     * @return false if an entity with the same UUID is already being simulated.
     */
    default boolean startSimulate() {
        if (!isClientSide()) {
            boolean result = SimulatedEntityManager.get().startSimulatingEntity(this);
            if (!result) {
                kill();
                LOGGER.warn("SIMULATED ENTITY ALREADY EXISTS KILL {} {}", getUUID(), this);
            }
            return result;
        }
        return false;
    }
    default void stopSimulate() {
        if (!isClientSide()) {
            SimulatedEntityManager.get().stopSimulatingEntity(this);
        }
    }
    default boolean isSimulateEnabled() {
        return !isClientSide() && SimulatedEntityManager.get().isSimulated(this);
    }
    /**
     * @return true if the entity can be revived and was successfully revived (ticked by vanilla)
     */
    default boolean checkRevive(@NotNull MinecraftServer server) {
        ServerLevel sl = (ServerLevel) getWorld();
        Entity entity = (Entity) this;
        ServerChunkCache scc = sl.getChunkSource();
        boolean inTickRange = scc.chunkMap.getDistanceManager().inEntityTickingRange(entity.chunkPosition().toLong());
        if (!inTickRange) return false;
        boolean hasChunk = UtilEntity.isChunkLoaded(sl, entity);
        if (!hasChunk) return false;
        //boolean alreadyAdded = sl.getEntity(getUUID()) != null;
        /*System.out.println("CHECK REVIVE hasChunk "+hasChunk+" inTickRange "+inTickRange+" alreadyAdded "+alreadyAdded
                +" inhabited time "+chunk.getInhabitedTime());*/
        if (sl.getEntity(getUUID()) == null) {
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
     * @return true if the entity is not being ticked by the vanilla server
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
    default boolean isAutoStartSimulateOnVanillaTick() {
        return true;
    }
}

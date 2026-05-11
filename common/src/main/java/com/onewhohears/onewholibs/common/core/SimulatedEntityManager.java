package com.onewhohears.onewholibs.common.core;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.entity.SimulatedEntity;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Predicate;

public class SimulatedEntityManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final IntObjectMap<SimulatedEntity> ENTITIES = new IntObjectHashMap<>();
    private final Set<Integer> FOR_REMOVAL = new HashSet<>();

    public void tickPre(@NotNull MinecraftServer server) {
        ENTITIES.forEach((id, entity) -> {
            if (entity.isStopSimulating()) {
                FOR_REMOVAL.add(id);
                return;
            }
            entity.onServerTickPre(server);
        });
        FOR_REMOVAL.forEach(ENTITIES::remove);
        FOR_REMOVAL.clear();
    }

    public void tickPost(@NotNull MinecraftServer server) {
        ENTITIES.forEach((id, entity) -> {
            entity.onServerTickPost(server);
        });
    }

    public boolean startSimulatingEntity(@NotNull SimulatedEntity entity) {
        if (ENTITIES.containsKey(entity.getId())) return true;
        if (getByUUID(entity.getUUID()) != null) return false;
        ENTITIES.put(entity.getId(), entity);
        return true;
    }

    @Nullable
    public SimulatedEntity getByUUID(@NotNull UUID uuid) {
        for (SimulatedEntity sim : ENTITIES.values()) {
            if (sim.getUUID().equals(uuid)) {
                return sim;
            }
        }
        return null;
    }

    @Nullable
    public SimulatedEntity getById(int id) {
        return ENTITIES.get(id);
    }

    public <E extends Entity> List<E> getAllOfClass(Class<E> type, Predicate<E> filter) {
        List<E> list = new ArrayList<>();
        ENTITIES.forEach((id, sim) -> {
            if (type.isInstance(sim)) {
                E entity = type.cast(sim);
                if (filter.test(entity)) {
                    list.add(entity);
                }
            }
        });
        return list;
    }

    public <E extends Entity> List<E> getAllOfClass(Class<E> type) {
        return getAllOfClass(type, entity -> true);
    }

    public boolean isSimulated(@NotNull SimulatedEntity entity) {
        return ENTITIES.containsKey(entity.getId());
    }

    public void stopSimulatingEntity(@NotNull SimulatedEntity entity) {
        ENTITIES.remove(entity.getId());
    }

    public void serverStop(MinecraftServer server) {
        LOGGER.warn("{} Simulated Entities may not save if currently unloaded.", ENTITIES.size());
    }

    private static SimulatedEntityManager INSTANCE;

    public static void init() {
        INSTANCE = new SimulatedEntityManager();
        LOGGER.info("SIMULATED ENTITY MANAGER INIT");
    }

    /**
     * ONLY USE ON SERVER SIDE, WILL BE NULL ON CLIENT
     */
    public static SimulatedEntityManager get() {
        return INSTANCE;
    }

    private SimulatedEntityManager() {}
}

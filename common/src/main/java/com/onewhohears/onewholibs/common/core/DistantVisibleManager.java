package com.onewhohears.onewholibs.common.core;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.util.UtilEntity;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Consumer;

/**
 * @author 1whohears
 */
public class DistantVisibleManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final IntObjectMap<VisibleData> VISIBLES = new IntObjectHashMap<>();
    private static int ID_COUNTER = 0;
    private static int BLOCKS_CHECKED = 0;

    public static void queryVisible(@NotNull MinecraftServer server,
                                    @NotNull Entity entity1, @NotNull Entity entity2,
                                    @NotNull VisibleRequestData requestData) {
        VisibleData visibleData = null;
        boolean flipEntites = false;
        for (VisibleData data : VISIBLES.values()) {
            if (data.entityId1 == entity1.getId() && data.entityId2 == entity2.getId()) {
                visibleData = data;
                break;
            } else if (data.entityId1 == entity2.getId() && data.entityId2 == entity1.getId()) {
                visibleData = data;
                flipEntites = true;
                break;
            }
        }
        if (visibleData == null) {
            visibleData = new VisibleData(entity1, entity2);
            VISIBLES.put(visibleData.id, visibleData);
        }
        visibleData.addRequest(server, flipEntites, requestData);
    }

    public static void onServerTick(@NotNull MinecraftServer server) {
        int max = getMaxBlocksPerTick();
        BLOCKS_CHECKED = 0;
        for (VisibleData visible : VISIBLES.values()) {
            visible.tick(server, max);
            if (BLOCKS_CHECKED >= max) break;
        }
        removeFailedVisibles();
    }

    public static int getMaxBlocksPerTick() {
        return 500;
    }

    public static void onServerStart(@NotNull MinecraftServer server) {
        VISIBLES.clear();
    }

    private static void removeFailedVisibles() {
        VISIBLES.entrySet().removeIf(entry -> entry.getValue().isForRemoval());
    }

    public static class VisibleData {
        public final int id;
        public final int entityId1;
        public final int entityId2;
        public @NotNull final ResourceKey<Level> levelId;
        private @NotNull final IntObjectMap<VisibleRequestState> requests = new IntObjectHashMap<>();
        private @NotNull final IntObjectMap<VisibleRequestState> requestsFlipped = new IntObjectHashMap<>();
        private @NotNull VisibleTestResult result = VisibleTestResult.NONE;
        private @NotNull Vec3 entityPos1, entityPos2, diff, dir;
        private float progress = 0;
        private int blocksChecked = 0;
        private int fastestRequestUpdateRate = -1;
        private int prevUpdateTime = -1000;
        private void tick(@NotNull MinecraftServer server, int maxBlocks) {
            int currentTime = server.getTickCount();
            removeExpiredRequests(currentTime);
            if (progress == 0 && (prevUpdateTime == -1000 || currentTime - prevUpdateTime < fastestRequestUpdateRate)) return;
            // TODO start checking blocks in the raycast past at a limit of some number of blocks per tick
            ServerLevel level = getLevel(server);
            if (level == null) {
                setFailed(VisibleTestResult.FAILED_INVALID_LEVEL_ID, currentTime);
                return;
            }
            int buildHeight = level.getBuildHeight();
            int buildFloor = level.getBuildFloor();
            if ((entityPos1.y > buildHeight && entityPos2.y > buildHeight) || (entityPos1.y < buildFloor && entityPos2.y < buildFloor)) {
                update(server, true);
            }
            // do some math
            while (progress < 1 && BLOCKS_CHECKED < max) {
                // determine which block to check
                // if (obstructed) update(server, false);
                ++BLOCKS_CHECKED;
            }
            if (progress >= 1) {
                update(server, true);
            }
        }
        private void addRequest(@NotNull MinecraftServer server, boolean flipEntities,
                                @NotNull VisibleRequestData requestData) {
            int currentTime = server.getTickCount();
            IntObjectMap<VisibleRequestState> reqs = flipEntities ? requestsFlipped : requests;
            VisibleRequestState state = reqs.get(requestData.typeId);
            if (state == null) {
                state = new VisibleRequestState(requestData, currentTime);
                reqs.put(requestData.typeId, state);
            } else {
                state.requestTime = currentTime;
            }
            if (fastestRequestUpdateRate == -1 || requestData.updateRate < fastestRequestUpdateRate) {
                fastestRequestUpdateRate = requestData.updateRate;
            }
        }
        private VisibleData(@NotNull Entity entity1, @NotNull Entity entity2) {
            this.id = ++ID_COUNTER;
            this.levelId = UtilEntity.getLevel(entity1).dimension();
            this.entityId1 = entity1.getId();
            this.entityId2 = entity2.getId();
            calcPositions(entity1, entity2);
        }
        private void calcPositions(@NotNull Entity entity1, @NotNull Entity entity2) {
            entityPos1 = entity1.getEyePosition();
            entityPos2 = entity2.getEyePosition();
            diff = entityPos2.subtract(entityPos1);
            dir = diff.normalize();
            // TODO based on distance, determine which blocks need to be checked (lower LOD in the middle)
        }
        public @Nullable ServerLevel getLevel(@NotNull MinecraftServer server) {
            return server.getLevel(levelId);
        }
        public @Nullable Entity getEntity(@NotNull MinecraftServer server, int entityId) {
            ServerLevel level = getLevel(server);
            if (level == null) return null;
            return level.getEntity(entityId);
        }
        public @Nullable Entity getEntity1(@NotNull MinecraftServer server) {
            return getEntity(server, entityId1);
        }
        public @Nullable Entity getEntity2(@NotNull MinecraftServer server) {
            return getEntity(server, entityId2);
        }
        private void update(@NotNull MinecraftServer server, boolean visible) {
            ServerLevel level = getLevel(server);
            if (level == null) {
                setFailed(VisibleTestResult.FAILED_INVALID_LEVEL_ID, server.getTickCount());
                return;
            }
            Entity entity1 = level.getEntity(entityId1);
            if (entity1 == null) {
                setFailed(VisibleTestResult.FAILED_ENTITY_1_NOT_FOUND, server.getTickCount());
                return;
            }
            Entity entity2 = level.getEntity(entityId2);
            if (entity2 == null) {
                setFailed(VisibleTestResult.FAILED_ENTITY_2_NOT_FOUND, server.getTickCount());
                return;
            }
            setPassed(visible, level, entity1, entity2, server.getTickCount());
        }
        private void setPassed(boolean visible, @NotNull ServerLevel level,
                               @NotNull Entity entity1, @NotNull Entity entity2, int currentTime) {
            this.result = visible ? VisibleTestResult.VISION_PASSED : VisibleTestResult.VISION_OBSTRUCTED;
            VisibleUpdateEvent event = new VisibleUpdateEvent(this, level, entity1, entity2, result);
            updateRequestStates(event, currentTime);
            // reset for next ray cast compute
            calcPositions(entity1, entity2);
            this.progress = 0;
            this.blocksChecked = 0;
            this.prevUpdateTime = currentTime;
        }
        private void setFailed(VisibleTestResult result, int currentTime) {
            this.result = result;
            VisibleUpdateEvent event = new VisibleUpdateEvent(this, null, null, null, result);
            updateRequestStates(event, currentTime);
        }
        private void updateRequestStates(VisibleUpdateEvent event, int currentTime) {
            requests.forEach((typeId, state) -> {
                if (currentTime - state.updateTime < state.requestData.updateRate) return;
                state.requestData.onVisibleUpdate.accept(event);
                state.updateTime = currentTime;
            });
            VisibleUpdateEvent eventFlipped = event.flipEntities();
            requestsFlipped.forEach((typeId, state) -> {
                if (currentTime - state.updateTime < state.requestData.updateRate) return;
                state.requestData.onVisibleUpdate.accept(eventFlipped);
                state.updateTime = currentTime;
            });
        }
        private void removeExpiredRequests(int currentTime) {
            requests.entrySet().removeIf(entry -> {
               VisibleRequestState state = entry.getValue();
               return currentTime - state.requestTime > state.requestData.expireTime;
            });
            requestsFlipped.entrySet().removeIf(entry -> {
                VisibleRequestState state = entry.getValue();
                return currentTime - state.requestTime > state.requestData.expireTime;
            });
        }
        public boolean isFailed() {
            return result.failed;
        }
        public boolean isEmptyRequests() {
            return requests.isEmpty() && requestsFlipped.isEmpty();
        }
        public boolean isForRemoval() {
            return isFailed() || isEmptyRequests();
        }
        public @NotNull VisibleTestResult getResult() {
            return result;
        }
        public float getProgress() {
            return progress;
        }
        public int getBlocksChecked() {
            return blocksChecked;
        }
    }

    public static class VisibleRequestState {
        public @NotNull final VisibleRequestData requestData;
        public int requestTime, updateTime;
        public VisibleRequestState(@NotNull VisibleRequestData requestData, int requestTime) {
            this.requestData = requestData;
            this.requestTime = requestTime;
            this.updateTime = -requestData.updateRate();
        }
    }

    public record VisibleRequestData(int typeId, int expireTime, int updateRate,
                                     @NotNull Consumer<VisibleUpdateEvent> onVisibleUpdate) {}

    public record VisibleUpdateEvent(@NotNull VisibleData data, @Nullable ServerLevel level,
                                     @Nullable Entity entity1, @Nullable Entity entity2,
                                     @NotNull VisibleTestResult result) {
        public VisibleUpdateEvent flipEntities() {
            return new VisibleUpdateEvent(data, level, entity2, entity1, result);
        }
    }
}

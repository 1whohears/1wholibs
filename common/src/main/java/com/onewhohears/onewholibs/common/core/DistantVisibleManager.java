package com.onewhohears.onewholibs.common.core;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.util.UtilEntity;
import com.onewhohears.onewholibs.util.UtilMCText;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Consumer;

import static com.onewhohears.onewholibs.common.command.CanSeeCommand.YELLOW;

/**
 * @author 1whohears
 */
public class DistantVisibleManager {

    public static final int CAN_SEE_COMMAND_TYPE = 0x1010;

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
        return 500; // TODO make getMaxBlocksPerTick configurable
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
        private float length;
        private float progress = 0;
        private int blocksChecked = 0;
        private int fastestRequestUpdateRate = -1;
        private int prevUpdateTime = -1000;
        private void tick(@NotNull MinecraftServer server, int maxBlocks) {
            int currentTime = server.getTickCount();
            removeExpiredRequests(currentTime);
            //System.out.println("TICK "+id+" "+requests.size()+" "+requestsFlipped.size()+" "+progress+" "+BLOCKS_CHECKED);
            if (progress == 0 && prevUpdateTime != -1000 && currentTime - prevUpdateTime < fastestRequestUpdateRate) {
                return;
            }
            ServerLevel level = getLevel(server);
            if (level == null) {
                setFailed(VisibleTestResult.FAILED_INVALID_LEVEL_ID, server);
                return;
            }
            if (progress == 0) {
                Entity entity1 = level.getEntity(entityId1);
                if (entity1 == null) {
                    setFailed(VisibleTestResult.FAILED_ENTITY_1_NOT_FOUND, server);
                    return;
                }
                Entity entity2 = level.getEntity(entityId2);
                if (entity2 == null) {
                    setFailed(VisibleTestResult.FAILED_ENTITY_2_NOT_FOUND, server);
                    return;
                }
                calcPositions(entity1, entity2);
            }
            int buildHeight = level.getMaxBuildHeight();
            int buildFloor = level.getMinBuildHeight();
            if ((entityPos1.y > buildHeight && entityPos2.y > buildHeight) || (entityPos1.y < buildFloor && entityPos2.y < buildFloor)) {
                update(server, level, true);
                return;
            }
            while (progress < 1 && BLOCKS_CHECKED < maxBlocks) {
                progress += 0.05f; // TODO calculate next progress value
                Vec3 next = entityPos1.add(dir.scale(length * progress));
                if (next.y > buildHeight) {
                    if (dir.y >= 0) {
                        progress = 1;
                        break;
                    } continue;
                } else if (next.y < buildFloor) {
                    if (dir.y <= 0) {
                        progress = 1;
                        break;
                    } continue;
                }
                BlockPos nextBlock = UtilGeometry.toBlockPos(next);
                //ChunkPos nextChunk = new ChunkPos(nextBlock);
                BlockState state = level.getBlockState(nextBlock);
                ++BLOCKS_CHECKED;
                boolean obstructed = UtilEntity.blocksMotion(state);
                if (obstructed) {
                    update(server, level, false);
                    break;
                }
            }
            if (progress >= 1) update(server, level, true);
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
        }
        private void calcPositions(@NotNull Entity entity1, @NotNull Entity entity2) {
            entityPos1 = entity1.getEyePosition();
            entityPos2 = entity2.getEyePosition();
            diff = entityPos2.subtract(entityPos1);
            dir = diff.normalize();
            length = (float) diff.length();
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
        private void update(@NotNull MinecraftServer server, @NotNull ServerLevel level, boolean visible) {
            Entity entity1 = level.getEntity(entityId1);
            if (entity1 == null) {
                setFailed(VisibleTestResult.FAILED_ENTITY_1_NOT_FOUND, server);
                return;
            }
            Entity entity2 = level.getEntity(entityId2);
            if (entity2 == null) {
                setFailed(VisibleTestResult.FAILED_ENTITY_2_NOT_FOUND, server);
                return;
            }
            setPassed(visible, server, level, entity1, entity2);
        }
        private void setPassed(boolean visible, @NotNull MinecraftServer server, @NotNull ServerLevel level,
                               @NotNull Entity entity1, @NotNull Entity entity2) {
            this.result = visible ? VisibleTestResult.VISION_PASSED : VisibleTestResult.VISION_OBSTRUCTED;
            VisibleUpdateEvent event = new VisibleUpdateEvent(this, server, level, entity1, entity2, result);
            updateRequestStates(event, server.getTickCount());
            // reset for next ray cast compute
            this.progress = 0;
            this.blocksChecked = 0;
            this.prevUpdateTime = server.getTickCount();
        }
        private void setFailed(VisibleTestResult result, @NotNull MinecraftServer server) {
            this.result = result;
            VisibleUpdateEvent event = new VisibleUpdateEvent(this, server,
                    null, null, null, result);
            updateRequestStates(event, server.getTickCount());
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

    /**
     * @param onVisibleUpdate ONLY USE THE GIVEN FIELDS IN {@link VisibleUpdateEvent}
     */
    public record VisibleRequestData(int typeId, int expireTime, int updateRate,
                                     @NotNull Consumer<VisibleUpdateEvent> onVisibleUpdate) {}

    /**
     * @param level WILL BE NULL IF {@code result().failed == true}
     * @param entity1 WILL BE NULL IF {@code result().failed == true}
     * @param entity2 WILL BE NULL IF {@code result().failed == true}
     */
    public record VisibleUpdateEvent(@NotNull VisibleData data, @NotNull MinecraftServer server,
                                     ServerLevel level, Entity entity1, Entity entity2,
                                     @NotNull VisibleTestResult result) {
        public VisibleUpdateEvent flipEntities() {
            return new VisibleUpdateEvent(data, server, level, entity2, entity1, result);
        }
    }

    public static final Style GREEN = Style.EMPTY.withColor(ChatFormatting.GREEN);
    public static final Style PURPLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE);
    public static final Style RED = Style.EMPTY.withColor(ChatFormatting.RED);

    public static final VisibleRequestData CAN_SEE_TEST_DATA = new VisibleRequestData(
            CAN_SEE_COMMAND_TYPE, 200, 20, event -> {
                int time = event.server.getTickCount();
                if (event.entity1 instanceof Player player) {
                    if (event.entity2 != null) {
                        Style style = event.result.passed ? GREEN : PURPLE;
                        player.sendSystemMessage(UtilMCText.literal("Result "+event.result+" "+time+" "
                                +event.entity1.getScoreboardName()+" "+event.entity2.getScoreboardName())
                                .setStyle(style));
                    } else {
                        player.sendSystemMessage(UtilMCText.literal("Result "+event.result+" "+time+" "
                                +event.entity1.getScoreboardName()).setStyle(RED));
                    }
                }
                if (event.result().failed) {
                    LOGGER.info("Visible Query FAILED: {} | {} | {} | {} | {}", event.result,
                            time, event.data.id, event.entity1, event.entity2);
                    return;
                }
                LOGGER.info("Visible Query RESULT: {} | {} | {} | {} | {}", event.result, event.data.id,
                        time, event.entity1, event.entity2);
            }
    );
}

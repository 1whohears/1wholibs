package com.onewhohears.onewholibs.common.core;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.common.command.CustomGameRules;
import com.onewhohears.onewholibs.entity.SimulatedEntity;
import com.onewhohears.onewholibs.util.UtilEntity;
import com.onewhohears.onewholibs.util.UtilMCText;
import com.onewhohears.onewholibs.util.UtilParse;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This system does the ray casting entirely on the server side.
 * Blocks in unloaded chunks are checked via a cached low resolution height map via {@link HeightMapManager}.
 * @author 1whohears
 */
public class DistantVisibleManager {

    public static final int CAN_SEE_COMMAND_TYPE = 0x1010;
    public static final int TICK_TIME_WARN = 2;

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<VisibleData> VISIBLES = new ArrayList<>();

    private static int ID_COUNTER = 0;
    private static int BLOCKS_CHECKED = 0;
    private static int HEIGHT_MAP_CHECKS = 0;
    private static int VISIBLE_CHECKED_INDEX = 0;

    private static final List<Long> TICK_TIMES = new ArrayList<>();
    private static final int TICK_TIMES_LENGTH = 200;
    private static double TICK_TIME_AVG;

    public static void queryVisible(@NotNull MinecraftServer server,
                                    @NotNull Entity entity1, @NotNull Entity entity2,
                                    @NotNull VisibleRequestData requestData) {
        VisibleData visibleData = null;
        boolean flipEntites = false;
        for (VisibleData data : VISIBLES) {
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
            VISIBLES.add(visibleData);
        }
        visibleData.addRequest(server, flipEntites, requestData);
    }

    public static void cancelFirstEntityQuery(int entityId1, int entityId2, int requestTypeId) {
        VisibleData visibleData = null;
        boolean flipEntites = false;
        for (VisibleData data : VISIBLES) {
            if (data.entityId1 == entityId1 && data.entityId2 == entityId2) {
                visibleData = data;
                break;
            } else if (data.entityId1 == entityId2 && data.entityId2 == entityId1) {
                visibleData = data;
                flipEntites = true;
                break;
            }
        }
        if (visibleData == null) return;
        if (flipEntites) visibleData.requestsFlipped.remove(requestTypeId);
        else visibleData.requests.remove(requestTypeId);
    }

    public static void cancelBothEntityQueries(int entityId1, int entityId2, int requestTypeId) {
        VisibleData visibleData = null;
        for (VisibleData data : VISIBLES) {
            if (data.entityId1 == entityId1 && data.entityId2 == entityId2) {
                visibleData = data;
                break;
            } else if (data.entityId1 == entityId2 && data.entityId2 == entityId1) {
                visibleData = data;
                break;
            }
        }
        if (visibleData == null) return;
        visibleData.requests.remove(requestTypeId);
        visibleData.requestsFlipped.remove(requestTypeId);
    }

    public static void cancelAllEntityQueries(int entityId1, int entityId2) {
        VisibleData visibleData = null;
        for (VisibleData data : VISIBLES) {
            if (data.entityId1 == entityId1 && data.entityId2 == entityId2) {
                visibleData = data;
                break;
            } else if (data.entityId1 == entityId2 && data.entityId2 == entityId1) {
                visibleData = data;
                break;
            }
        }
        if (visibleData == null) return;
        visibleData.requests.clear();
        visibleData.requestsFlipped.clear();
    }

    public static void onServerTick(@NotNull MinecraftServer server) {
        long startTime = System.nanoTime();

        boolean useHeightMap = server.getGameRules().getBoolean(CustomGameRules.RAYCAST_USE_HEIGHT_MAP);
        int maxBlocks = server.getGameRules().getInt(CustomGameRules.MAX_RAYCAST_BLOCK_CHECKS);
        int maxMaps = server.getGameRules().getInt(CustomGameRules.MAX_RAYCAST_HEIGHT_MAP_CHECKS);
        BLOCKS_CHECKED = 0;
        HEIGHT_MAP_CHECKS = 0;
        if (VISIBLE_CHECKED_INDEX < 0) VISIBLE_CHECKED_INDEX = 0;
        int k = 0, size = VISIBLES.size();
        int removed = 0;
        while (k++ < VISIBLES.size()) {
            int index = VISIBLE_CHECKED_INDEX;
            if (index >= size) index = 0;
            VisibleData visible = VISIBLES.get(index);
            visible.tick(server, maxBlocks, maxMaps, useHeightMap);
            if (visible.isForRemoval()) ++removed;
            if (BLOCKS_CHECKED >= maxBlocks || HEIGHT_MAP_CHECKS >= maxMaps) break;
            VISIBLE_CHECKED_INDEX = index + 1;
        }
        VISIBLES.removeIf(VisibleData::isForRemoval);
        VISIBLE_CHECKED_INDEX -= removed;

        long endTime = System.nanoTime();
        TICK_TIMES.add(0, endTime - startTime);
        while (TICK_TIMES.size() > TICK_TIMES_LENGTH) TICK_TIMES.remove(TICK_TIMES.size()-1);
        long total = 0;
        for (Long time : TICK_TIMES) total += time;
        TICK_TIME_AVG = (double) total / TICK_TIMES.size() * 10E-6;
        if (TICK_TIME_AVG >= TICK_TIME_WARN && server.getTickCount() % 100 == 0) {
            LOGGER.warn("Distant Raycasts {} are taking {} milliseconds to compute.", VISIBLES.size(), TICK_TIME_AVG);
        }
    }

    public static void onServerStart(@NotNull MinecraftServer server) {
        VISIBLES.clear();
    }

    public static void onServerStop(@NotNull MinecraftServer server) {
        VISIBLES.clear();
    }

    public static List<String> getAllVisibleDebug(@NotNull MinecraftServer server) {
        List<String> debugs = new ArrayList<>();
        for (VisibleData visible : VISIBLES) debugs.add(visible.getDebug(server));
        return debugs;
    }

    @Nullable
    public static VisibleData getById(int id) {
        for (VisibleData visibleData : VISIBLES) {
            if (visibleData.id == id) {
                return visibleData;
            }
        }
        return null;
    }

    public static String prettyVec3(@Nullable Vec3 vec) {
        if (vec != null) return Mth.floor(vec.x)+","+Mth.floor(vec.y)+","+Mth.floor(vec.z);
        return "null";
    }

    public static class VisibleData {
        public final int id;
        public final int entityId1;
        public final int entityId2;
        public @NotNull final ResourceKey<Level> levelId;
        private @NotNull final IntObjectMap<VisibleRequestState> requests = new IntObjectHashMap<>();
        private @NotNull final IntObjectMap<VisibleRequestState> requestsFlipped = new IntObjectHashMap<>();
        private @NotNull VisibleTestResult result = VisibleTestResult.NONE;
        private Vec3 entityPos1, entityPos2, diff, dir;
        private float length;
        private int spreadIndex = -1;
        private float[] spreads;
        private int fastestRequestUpdateRate = -1;
        private int prevUpdateTime = -1000;
        private void tick(@NotNull MinecraftServer server, int maxBlocks, int maxMaps, boolean checkHeightMap) {
            int currentTime = server.getTickCount();
            removeExpiredRequests(server);
            if (isEmptyRequests()) return;
            //System.out.println("TICK "+id+" "+requests.size()+" "+requestsFlipped.size()+" "+progress+" "+BLOCKS_CHECKED);
            if (spreadIndex == -1 && prevUpdateTime != -1000 && currentTime - prevUpdateTime < fastestRequestUpdateRate) {
                return;
            }
            ServerLevel level = getLevel(server);
            if (level == null) {
                setFailed(VisibleTestResult.FAILED_INVALID_LEVEL_ID, server);
                return;
            }
            if (spreadIndex == -1) {
                Entity entity1 = getEntity(level, entityId1);
                if (entity1 == null) {
                    setFailed(VisibleTestResult.FAILED_ENTITY_1_NOT_FOUND, server);
                    return;
                }
                Entity entity2 = getEntity(level, entityId2);
                if (entity2 == null) {
                    setFailed(VisibleTestResult.FAILED_ENTITY_2_NOT_FOUND, server);
                    return;
                }
                calcPositions(entity1, entity2);
            }
            int buildHeight = level.getMaxBuildHeight();
            int buildFloor = level.getMinBuildHeight();
            if ((entityPos1.y > buildHeight && entityPos2.y > buildHeight)
                    || (entityPos1.y < buildFloor && entityPos2.y < buildFloor)) {
                update(server, level, true, false, entityPos2);
                return;
            }
            Vec3 prev = entityPos1;
            BlockPos.MutableBlockPos nextBlock = new BlockPos.MutableBlockPos();
            int lastChunkX = Integer.MIN_VALUE;
            int lastChunkZ = Integer.MIN_VALUE;
            boolean chunkLoaded = false;
            LevelChunk chunk = null;
            while (spreadIndex < spreads.length-1 && BLOCKS_CHECKED < maxBlocks && HEIGHT_MAP_CHECKS < maxMaps) {
                ++spreadIndex;
                Vec3 next = entityPos1.add(dir.scale(spreads[spreadIndex]));
                nextBlock.set(next.x, next.y, next.z);
                if (nextBlock.getY() > buildHeight) {
                    if (dir.y >= 0) {
                        spreadIndex = spreads.length-1;
                        break;
                    } continue;
                } else if (nextBlock.getY() < buildFloor) {
                    if (dir.y <= 0) {
                        spreadIndex = spreads.length-1;
                        break;
                    } continue;
                }
                int chunkX = nextBlock.getX() >> 4;
                int chunkZ = nextBlock.getZ() >> 4;
                if (chunkX != lastChunkX || chunkZ != lastChunkZ) {
                    lastChunkX = chunkX;
                    lastChunkZ = chunkZ;
                    chunkLoaded = UtilEntity.isChunkLoaded(level, chunkX, chunkZ);
                    if (chunkLoaded) chunk = level.getChunk(chunkX, chunkZ);
                    else chunk = null;
                }
                if (!chunkLoaded) {
                    if (checkHeightMap) {
                        ++HEIGHT_MAP_CHECKS;
                        short height = HeightMapManager.getHeight(levelId, next);
                        short prevHeight = HeightMapManager.getHeight(levelId, prev);
                        if ((prev.y > prevHeight && next.y <= height) || (prev.y <= prevHeight && next.y > height)) {
                            update(server, level, false, false, next);
                            break;
                        }
                    }
                    prev = next;
                    continue;
                } else if (checkHeightMap) {
                    ++HEIGHT_MAP_CHECKS;
                    short height = HeightMapManager.getHeight(levelId, next);
                    if (next.y > height) {
                        prev = next;
                        continue;
                    }
                }
                ++BLOCKS_CHECKED;
                int height = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, nextBlock.getX(), nextBlock.getZ());
                if (nextBlock.getY() == height) {
                    update(server, level, false, true, next);
                    break;
                }
                if (nextBlock.getY() < height) {
                    // TODO ++BLOCKS_CHECKED; again?
                    BlockState state = chunk.getBlockState(nextBlock); // TODO how expensive is this actually?
                    if (UtilEntity.blocksMotion(state)) {
                        update(server, level, false, true, next);
                        break;
                    }
                }
                prev = next;
            }
            if (spreadIndex == spreads.length-1) update(server, level, true, false, entityPos2);
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
            spreads = UtilGeometry.generateSpread(length, getMinSpread(), getMaxSpread(),
                    UtilGeometry.SpreadMode.BOTH, getSpreadFactor(), getSpreadGrowthSmooth());
            //System.out.println("LENGTH = "+length+" SPREADS "+spreads.length+" = "+ Arrays.toString(spreads));
        }
        public @Nullable ServerLevel getLevel(@NotNull MinecraftServer server) {
            return server.getLevel(levelId);
        }
        public @Nullable Entity getEntity(@Nullable ServerLevel level, int entityId) {
            if (level == null) return null;
            SimulatedEntity sim = SimulatedEntityManager.get().getById(entityId);
            if (sim != null && sim.getWorld().dimension().location().equals(levelId.location())) return (Entity) sim;
            return level.getEntity(entityId);
        }
        public @Nullable Entity getEntity1(@NotNull MinecraftServer server) {
            return getEntity(getLevel(server), entityId1);
        }
        public @Nullable Entity getEntity2(@NotNull MinecraftServer server) {
            return getEntity(getLevel(server), entityId2);
        }
        private void update(@NotNull MinecraftServer server, @NotNull ServerLevel level,
                            boolean visible, boolean block, @NotNull Vec3 approxObstructPos) {
            Entity entity1 = getEntity(level, entityId1);
            if (entity1 == null) {
                setFailed(VisibleTestResult.FAILED_ENTITY_1_NOT_FOUND, server);
                return;
            }
            Entity entity2 = getEntity(level, entityId2);
            if (entity2 == null) {
                setFailed(VisibleTestResult.FAILED_ENTITY_2_NOT_FOUND, server);
                return;
            }
            setPassed(visible, block, server, level, entity1, entity2, approxObstructPos);
        }
        private void setPassed(boolean visible, boolean block,
                               @NotNull MinecraftServer server, @NotNull ServerLevel level,
                               @NotNull Entity entity1, @NotNull Entity entity2,
                               @NotNull Vec3 approxObstructPos) {
            this.result = visible ? VisibleTestResult.VISION_PASSED :
                    block ? VisibleTestResult.VISION_OBSTRUCTED_BLOCK : VisibleTestResult.VISION_OBSTRUCTED_HEIGHT_MAP;
            VisibleUpdateEvent event = new VisibleUpdateEvent(this, server, level, entity1, entity2, result, approxObstructPos);
            updateRequestStates(event, server.getTickCount());
            // reset for next ray cast compute
            this.spreadIndex = -1;
            this.prevUpdateTime = server.getTickCount();
        }
        private void setFailed(VisibleTestResult result, @NotNull MinecraftServer server) {
            this.result = result;
            VisibleUpdateEvent event = new VisibleUpdateEvent(this, server,
                    null, null, null, result, Vec3.ZERO);
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
        private void removeExpiredRequests(@NotNull MinecraftServer server) {
            removeExpiredRequests(server, requests);
            removeExpiredRequests(server, requestsFlipped);
        }
        private void removeExpiredRequests(@NotNull MinecraftServer server,
                                           @NotNull IntObjectMap<VisibleRequestState> req) {
            int currentTime = server.getTickCount();
            req.forEach((id, state) -> {
                if (!state.isExpired(currentTime)) return;
                VisibleUpdateEvent event = new VisibleUpdateEvent(this, server,
                        null, null, null, VisibleTestResult.FAILED_EXPIRED, Vec3.ZERO);
                state.requestData.onVisibleUpdate.accept(event);
            });
            req.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
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
        public String getDebug(@NotNull MinecraftServer server) {
            String debug = id +" | "+Mth.ceil(length)+" | "+spreadIndex+"/"+spreads.length+" | "+getResult()
                    +"\n    ["+prettyVec3(entityPos1)+"] ["+prettyVec3(entityPos2)+"]";
            ServerLevel level = getLevel(server);
            if (level == null) return debug;
            Entity entity1 = getEntity(level, entityId1);
            if (entity1 == null) return debug;
            debug += "\n    <" + UtilEntity.getEntityIdName(entity1) + " | " + entity1.getScoreboardName() + ">";
            Entity entity2 = getEntity(level, entityId2);
            if (entity2 == null) return debug;
            debug += "\n    <" + UtilEntity.getEntityIdName(entity2) + " | " + entity2.getScoreboardName() + ">";
            return debug;
        }
    }
    // TODO make these spread settings configurable
    public static int getMinSpread() {
        return 1;
    }

    public static int getMaxSpread() {
        return 64;
    }

    public static int getSpreadFactor() {
        return 2;
    }

    public static int getSpreadGrowthSmooth() {
        return 500;
    }

    public static class VisibleRequestState {
        public @NotNull final VisibleRequestData requestData;
        public int requestTime, updateTime;
        public VisibleRequestState(@NotNull VisibleRequestData requestData, int requestTime) {
            this.requestData = requestData;
            this.requestTime = requestTime;
            this.updateTime = -requestData.updateRate();
        }
        public boolean isExpired(int currentTime) {
            return currentTime - requestTime > requestData.expireTime;
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
                                     @NotNull VisibleTestResult result, @NotNull Vec3 approxObstructPos) {
        public VisibleUpdateEvent flipEntities() {
            return new VisibleUpdateEvent(data, server, level, entity2, entity1, result, approxObstructPos);
        }
    }

    public static final Style GREEN = Style.EMPTY.withColor(ChatFormatting.GREEN);
    public static final Style PURPLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE);
    public static final Style RED = Style.EMPTY.withColor(ChatFormatting.RED);

    public static final VisibleRequestData CAN_SEE_TEST_DATA = new VisibleRequestData(
            CAN_SEE_COMMAND_TYPE, 200, 20, event -> {
                int time = event.server.getTickCount();
                if (event.result().failed) {
                    LOGGER.info("Visible Query FAILED: {} | {} | {} |  {} | {} | {}", event.result,
                            event.data.id, time, Math.ceil(TICK_TIME_AVG), event.entity1, event.entity2);
                    return;
                }
                LOGGER.info("Visible Query RESULT: {} | {} | {} | {} | {} | {} | {}", event.result,
                        event.data.id, time, Math.ceil(TICK_TIME_AVG), event.approxObstructPos, event.entity1, event.entity2);
                if (event.entity1 instanceof Player player && event.entity2 != null) {
                    Style style = event.result.passed ? GREEN : PURPLE;
                    player.sendSystemMessage(UtilMCText.literal("Result "+event.result+" "+time+" "
                                    +event.entity1.getScoreboardName()+" "+event.entity2.getScoreboardName())
                            .setStyle(style));
                } else if (event.level != null && event.level.getEntity(event.data.entityId1) instanceof Player player) {
                    player.sendSystemMessage(UtilMCText.literal("Result "+event.result+" "+time+" "
                            +event.entity1.getScoreboardName()).setStyle(RED));
                }

            }
    );
}

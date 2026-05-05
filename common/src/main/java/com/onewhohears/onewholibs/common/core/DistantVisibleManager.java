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

    public static void queryVisible(@NotNull Entity entity1, @NotNull Entity entity2,
                                    @NotNull Consumer<VisibleUpdateEvent> onVisibleUpdate) {

    }

    public static void onServerTick() {

    }

    public static class VisibleData {
        public final int id;
        public final int entityId1;
        public final int entityId2;
        public @NotNull final ResourceKey<Level> levelId;
        public @NotNull final Consumer<VisibleUpdateEvent> onVisibleUpdate;
        public @NotNull final Consumer<VisibleTestFailEvent> onVisibleTestFail;
        private VisibleTestFailReason failReason = VisibleTestFailReason.NONE;
        private VisibleData(@NotNull Entity entity1, @NotNull Entity entity2,
                            @NotNull Consumer<VisibleUpdateEvent> onVisibleUpdate,
                            @NotNull Consumer<VisibleTestFailEvent> onVisibleTestFail) {
            this.id = ++ID_COUNTER;
            this.levelId = UtilEntity.getLevel(entity1).dimension();
            this.entityId1 = entity1.getId();
            this.entityId2 = entity2.getId();
            this.onVisibleUpdate = onVisibleUpdate;
            this.onVisibleTestFail = onVisibleTestFail;
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
                setFailed(VisibleTestFailReason.INVALID_LEVEL_ID);
                return;
            }
            Entity entity1 = level.getEntity(entityId1);
            if (entity1 == null) {
                setFailed(VisibleTestFailReason.ENTITY_1_NOT_FOUND);
                return;
            }
            Entity entity2 = level.getEntity(entityId2);
            if (entity2 == null) {
                setFailed(VisibleTestFailReason.ENTITY_2_NOT_FOUND);
                return;
            }
            onVisibleUpdate.accept(new VisibleUpdateEvent(level, entity1, entity2, visible));
        }
        private void setFailed(VisibleTestFailReason reason) {
            failReason = reason;
            onVisibleTestFail.accept(new VisibleTestFailEvent(levelId, entityId1, entityId2, reason));
        }
        public boolean isFailed() {
            return failReason != VisibleTestFailReason.NONE;
        }
        public VisibleTestFailReason getFailReason() {
            return failReason;
        }
    }

    public record VisibleUpdateEvent(@NotNull ServerLevel level, @NotNull Entity entity1,
                                     @NotNull Entity entity2, boolean visible) {}
    public record VisibleTestFailEvent(@NotNull ResourceKey<Level> levelId, int entity1Id, int entity2Id,
                                       @NotNull VisibleTestFailReason reason) {}
}

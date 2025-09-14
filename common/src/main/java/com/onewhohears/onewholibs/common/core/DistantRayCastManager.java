package com.onewhohears.onewholibs.common.core;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.common.network.toclient.ToClientCanSeePos;
import com.onewhohears.onewholibs.util.UtilEntity;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DistantRayCastManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final IntObjectMap<RayCastData> RAY_CASTS = new IntObjectHashMap<>();

    private static int RAY_CAST_ID_COUNTER = 0;

    public static void distantRayCast(@NotNull ServerLevel level, @NotNull Entity eyeEntity, @NotNull Entity targetEntity,
                               @NotNull RayCastComplete onComplete, long timeoutTime, long rayCastLifeTime,
                               double throWater, double throBlock) {
        RayCastData data = getRayCastWithEntities(eyeEntity, targetEntity);
        if (data != null) {
            data.apply();
            data.sendPayloads();
            return;
        }
        long createdTime = System.currentTimeMillis();
        data = new RayCastData(++RAY_CAST_ID_COUNTER, level, eyeEntity, targetEntity,
                createdTime, timeoutTime, rayCastLifeTime, throWater, throBlock, onComplete);
        RAY_CASTS.put(data.rayCastId, data);
        data.sendPayloads();
    }

    @Nullable
    public static RayCastData getRayCastWithEntities(@NotNull Entity entity1, @NotNull Entity entity2) {
        for (RayCastData data : RAY_CASTS.values())
            if (data.hasEntity(entity1) && data.hasEntity(entity2))
                return data;
        return null;
    }

    public static void handleC2SRayCast(int rayCastId, RayCastPerspective perspective, boolean success) {
        RayCastData data = RAY_CASTS.get(rayCastId);
        if (data == null) {
            LOGGER.warn("Received ray cast packet with ID {} that doesn't exist.", rayCastId);
            return;
        }
        data.handle(perspective, success);
    }

    public static class RayCastData {
        public final int rayCastId;
        @NotNull public final ServerLevel level;
        @NotNull public final Entity eyeEntity;
        @NotNull public final Entity targetEntity;
        public final long createdTime;
        public final long timeoutTime;
        public final long rayCastLifeTime;
        public final double throWater;
        public final double throBlock;
        @NotNull public final RayCastComplete onComplete;
        public boolean eyeConfirm = false, targetConfirm = false;
        public boolean eyeComplete = false, targetComplete = false;
        public long completeTime;
        public RayCastData(int rayCastId, @NotNull ServerLevel level, @NotNull Entity eyeEntity, @NotNull Entity targetEntity,
                           long createdTime, long timeoutTime, long rayCastLifeTime,
                           double throWater, double throBlock, @NotNull RayCastComplete onComplete) {
            this.rayCastId = rayCastId;
            this.level = level;
            this.eyeEntity = eyeEntity;
            this.targetEntity = targetEntity;
            this.createdTime = createdTime;
            this.timeoutTime = timeoutTime;
            this.rayCastLifeTime = rayCastLifeTime;
            this.throWater = throWater;
            this.throBlock = throBlock;
            this.onComplete = onComplete;
        }
        public boolean isConfirmed() {
            return eyeConfirm && targetConfirm;
        }
        public boolean isComplete() {
            return eyeComplete && targetComplete;
        }
        public boolean hasEntity(@NotNull Entity entity) {
            return eyeEntity.getId() == entity.getId() || targetEntity.getId() == entity.getId();
        }
        public void sendPayloads() {
            sendPayload(eyeEntity, targetEntity.getEyePosition(), RayCastPerspective.EYE);
            sendPayload(targetEntity, eyeEntity.getEyePosition(), RayCastPerspective.TARGET);
        }
        private void sendPayload(Entity entity, Vec3 pos, RayCastPerspective perspective) {
            ToClientCanSeePos packet = new ToClientCanSeePos(rayCastId, perspective,
                    entity, pos, throWater, throBlock);
            if (entity instanceof ServerPlayer player) {
                packet.sendTo(player);
            } else if (entity.getControllingPassenger() instanceof ServerPlayer player) {
                packet.sendTo(player);
            } else {
                int viewDistanceBlocks = level.getServer().getPlayerList().getViewDistance() * 16;
                Player nearestPlayer = level.getNearestPlayer(entity, viewDistanceBlocks);
                if (nearestPlayer == null) {
                    handleC2SRayCast(rayCastId, perspective, UtilEntity.isLocalVisionBlocked(
                            level, entity.getEyePosition(), pos, throWater, throBlock, 32));
                } else {
                    packet.sendTo((ServerPlayer) nearestPlayer);
                }
            }
        }
        public void apply() {
            completeTime = System.currentTimeMillis();
            onComplete.apply(level, eyeEntity, targetEntity, isConfirmed());
        }
        public void handle(RayCastPerspective perspective, boolean success) {
            if (perspective == RayCastPerspective.EYE) {
                eyeComplete = true;
                eyeConfirm = success;
            } else if (perspective == RayCastPerspective.TARGET) {
                targetComplete = true;
                targetConfirm = success;
            }
            if (isComplete()) apply();
        }
    }

    public interface RayCastComplete {
        void apply(@NotNull ServerLevel level, @NotNull Entity eyeEntity,
                   @NotNull Entity targetEntity, @NotNull Boolean pass);
    }

    public static void onServerTick() {
        long currentTime = System.currentTimeMillis();
        RAY_CASTS.entrySet().removeIf(entry -> {
            RayCastData data = entry.getValue();
            if (!data.isComplete() && currentTime - data.createdTime > data.timeoutTime) {
                return true;
            } else if (data.isComplete() && currentTime - data.completeTime > data.rayCastLifeTime) {
                return true;
            }
            return false;
        });
    }

}

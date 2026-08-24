package com.onewhohears.onewholibs.entity;

import org.jetbrains.annotations.NotNull;

public interface StateMachineAnimEntity {
    static float tickToSeconds(int tick, float partialTick) {
        return (tick + partialTick) * 0.05f;
    }
    static float loopAnimSeconds(float animTime, float animSpeed, float animLength) {
        return (animTime * animSpeed) % animLength;
    }
    boolean isAnimDataIdActive(@NotNull String animDataId);
    /**
     * usually just return {@link net.minecraft.world.entity.Entity#tickCount}
     */
    int getAnimTick(@NotNull String animDataId);
    /**
     * @return time in seconds into the animation
     */
    default float getAnimTime(@NotNull String animDataId, float partialTicks) {
        return tickToSeconds(getAnimTick(animDataId), partialTicks);
    }
    default float getAnimSpeed(@NotNull String animDataId) {
        return 1;
    }
    default float getAnimSeconds(@NotNull String animDataId, float partialTicks, float animationLength) {
        return loopAnimSeconds(getAnimTime(animDataId, partialTicks), getAnimSpeed(animDataId), animationLength);
    }
    /**
     * @return an animDataId representing the start of an animated transition
     */
    default @NotNull String getTransitionAnimIdStart() {
        return "";
    }
    /**
     * @return an animDataId representing the end of an animated transition
     */
    default @NotNull String getTransitionAnimIdEnd() {
        return "";
    }
    /**
     * @return between 0 and 1. 0 being transition start, 1 being transition end.
     */
    default float getTransitionPercent(float partialTicks) {
        return 0;
    }

    /**
     * intended to return the most recent result of {@link #getAnimSeconds(String, float, float)}
     * for the animDataId of {@link #getTransitionAnimIdStart()}
     */
    default float getTransitionStartSeconds() {
        return 0;
    }
}

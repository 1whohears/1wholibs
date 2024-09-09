package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;
import net.minecraft.world.entity.Entity;

public class ControllableAnimPlayer<T extends Entity> extends TriggerableAnimPlayer<T> {

    private final KFAnimData stats;
    private final KeyframeAnimationController<T> controller;

    public ControllableAnimPlayer(KFAnimData stats, KeyframeAnimationTrigger<T> trigger,
                                  KeyframeAnimationController<T> controller) {
        super(trigger);
        this.stats = stats;
        this.controller = controller;
    }

    public void applyAnimationAtTime(ImmutableMap.Builder<String, Matrix4f> builder, float animationTime) {
        getAnimation().applyAnimationAtSecond(builder, animationTime);
    }

    @Override
    public void applyAnimation(ImmutableMap.Builder<String, Matrix4f> builder, T entity, float partialTicks) {
        applyAnimationAtTime(builder, getAnimationTime(entity, partialTicks));
    }

    @Override
    public KFAnimData getAnimationStats() {
        return stats;
    }

    public float getAnimationTime(T entity, float partialTicks) {
        return controller.getAnimationSeconds(entity, partialTicks, getAnimationLength());
    }
}

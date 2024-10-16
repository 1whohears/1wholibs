package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.common.collect.ImmutableMap;

import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;

import java.util.Map;

public class ControllableAnimPlayer<T extends Entity> extends TriggerableAnimPlayer<T> {

    private final KFAnimData stats;
    private final KeyframeAnimationController<T> controller;

    public ControllableAnimPlayer(KFAnimData stats, KeyframeAnimationTrigger<T> trigger,
                                  KeyframeAnimationController<T> controller) {
        super(trigger);
        this.stats = stats;
        this.controller = controller;
    }

    public void applyAnimationAtTime(Map<String, Matrix4f> transforms, float animationTime) {
        getAnimation().applyAnimationAtSecond(transforms, animationTime);
    }

    @Override
    public void applyAnimation(Map<String, Matrix4f> transforms, T entity, float partialTicks) {
        applyAnimationAtTime(transforms, getAnimationTime(entity, partialTicks));
    }

    @Override
    public KFAnimData getAnimationStats() {
        return stats;
    }

    public float getAnimationTime(T entity, float partialTicks) {
        return controller.getAnimationSeconds(entity, partialTicks, getAnimationLength());
    }
}

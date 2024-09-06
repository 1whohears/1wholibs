package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;
import net.minecraft.world.entity.Entity;

public abstract class ControllableAnim<T extends Entity> extends TriggerableAnim<T> {

    private final KeyframeAnimationController<T> controller;

    public ControllableAnim(KeyframeAnimationTrigger<T> trigger, KeyframeAnimationController<T> controller) {
        super(trigger);
        this.controller = controller;
    }

    public abstract void applyAnimationAtTime(ImmutableMap.Builder<String, Matrix4f> builder, float animationTime);

    @Override
    public void applyAnimation(ImmutableMap.Builder<String, Matrix4f> builder, T entity, float partialTicks) {
        applyAnimationAtTime(builder, getAnimationTime(entity, partialTicks));
    }

    public float getAnimationTime(T entity, float partialTicks) {
        return controller.getAnimationTime(entity, partialTicks, getAnimationLength());
    }

}

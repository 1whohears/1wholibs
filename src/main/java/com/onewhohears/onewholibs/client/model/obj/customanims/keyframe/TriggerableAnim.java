package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import net.minecraft.world.entity.Entity;

public abstract class TriggerableAnim<T extends Entity> implements KeyframeAnimation<T> {

    private final KeyframeAnimationTrigger<T> trigger;

    public TriggerableAnim(KeyframeAnimationTrigger<T> trigger) {
        this.trigger = trigger;
    }

    @Override
    public boolean isAnimationActive(T entity) {
        return trigger.isAnimationActive(entity);
    }

}

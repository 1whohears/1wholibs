package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import net.minecraft.world.entity.Entity;

public abstract class TriggerableAnimPlayer<T extends Entity> implements KeyframeAnimationPlayer<T> {

    private final KeyframeAnimationTrigger<T> trigger;

    public TriggerableAnimPlayer(KeyframeAnimationTrigger<T> trigger) {
        this.trigger = trigger;
    }

    @Override
    public boolean isAnimationActive(T entity) {
        return trigger.isAnimationActive(entity);
    }

}

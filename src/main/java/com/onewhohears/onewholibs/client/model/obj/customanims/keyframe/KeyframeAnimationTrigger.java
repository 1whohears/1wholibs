package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import net.minecraft.world.entity.Entity;

public interface KeyframeAnimationTrigger<T extends Entity> {
    boolean isAnimationActive(T entity);
}

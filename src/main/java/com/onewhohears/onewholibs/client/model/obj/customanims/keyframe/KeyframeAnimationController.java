package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import net.minecraft.world.entity.Entity;

public interface KeyframeAnimationController<T extends Entity> {
    float getAnimationTime(T entity, float partialTicks, float animationLength);
}

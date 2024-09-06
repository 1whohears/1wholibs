package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;
import net.minecraft.world.entity.Entity;

public interface KeyframeAnimation<T extends Entity> {
    boolean isAnimationActive(T entity);
    void applyAnimation(ImmutableMap.Builder<String, Matrix4f> builder, T entity, float partialTicks);
    float getAnimationLength();
}

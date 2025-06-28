package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;
import net.minecraft.world.entity.Entity;

import java.util.Map;

public interface KeyframeAnimationPlayer<T extends Entity> {
    boolean isAnimationActive(T entity);
    void applyAnimation(Map<String, Matrix4f> transforms, T entity, float partialTicks);
    KFAnimData getAnimationStats();
    default float getAnimationLength() {
        return getAnimationStats().getAnimation().getAnimationLength();
    }
    default KeyframeAnimation getAnimation() {
        return getAnimationStats().getAnimation();
    }
}

package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;

public interface KeyframeAnimation {
    /**
     * @return animation length in seconds
     */
    float getAnimationLength();
    void applyAnimationAtSecond(ImmutableMap.Builder<String, Matrix4f> builder, float seconds);
    default void applyAnimationAtPercent(ImmutableMap.Builder<String, Matrix4f> builder, float percent) {
        applyAnimationAtSecond(builder, getAnimationLength() * percent);
    }
}

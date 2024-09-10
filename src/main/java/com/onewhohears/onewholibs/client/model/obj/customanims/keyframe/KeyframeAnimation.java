package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;

import java.util.Map;

public interface KeyframeAnimation {
    /**
     * @return animation length in seconds
     */
    float getAnimationLength();
    void applyAnimationAtSecond(Map<String, Matrix4f> transforms, float seconds);
    default void applyAnimationAtPercent(Map<String, Matrix4f> transforms, float percent) {
        applyAnimationAtSecond(transforms, getAnimationLength() * percent);
    }
}

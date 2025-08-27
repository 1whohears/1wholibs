package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.onewhohears.onewholibs.util.math.Mat4f;

import java.util.Map;

public interface KeyframeAnimation {
    /**
     * @return animation length in seconds
     */
    float getAnimationLength();
    void applyAnimationAtSecond(Map<String, Mat4f> transforms, float seconds);
    default void applyAnimationAtPercent(Map<String, Mat4f> transforms, float percent) {
        applyAnimationAtSecond(transforms, getAnimationLength() * percent);
    }
}

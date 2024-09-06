package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import javax.annotation.Nullable;

public interface KeyframeAnimParser {
    @Nullable KeyframeAnimation getAnimation(String animation_id);
}

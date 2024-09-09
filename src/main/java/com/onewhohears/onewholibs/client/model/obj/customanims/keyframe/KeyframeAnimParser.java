package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import javax.annotation.Nullable;

public interface KeyframeAnimParser extends ResourceManagerReloadListener {
    @Nullable
    KeyframeAnimation getAnimation(String animation_id);
}

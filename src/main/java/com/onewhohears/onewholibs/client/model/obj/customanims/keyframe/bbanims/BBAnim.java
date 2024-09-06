package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.bbanims;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.math.Matrix4f;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.ControllableAnim;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimationController;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimationTrigger;
import net.minecraft.world.entity.Entity;

public class BBAnim<T extends Entity> extends ControllableAnim<T> {

    private final float length;

    public BBAnim(JsonObject json, KeyframeAnimationTrigger<T> trigger,
                  KeyframeAnimationController<T> controller) {
        super(trigger, controller);
        length = json.get("animation_length").getAsFloat();
    }

    @Override
    public void applyAnimationAtTime(ImmutableMap.Builder<String, Matrix4f> builder, float animationTime) {

    }

    @Override
    public float getAnimationLength() {
        return length;
    }

}

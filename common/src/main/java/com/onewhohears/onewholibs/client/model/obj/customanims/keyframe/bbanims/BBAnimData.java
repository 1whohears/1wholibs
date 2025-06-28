package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.bbanims;

import com.google.gson.JsonObject;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KFAnimData;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KFAnimType;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimation;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetType;
import net.minecraft.resources.ResourceLocation;

public class BBAnimData extends KFAnimData {

    private KeyframeAnimation animation;

    public BBAnimData(ResourceLocation key, JsonObject json) {
        super(key, json);
    }

    @Override
    public KeyframeAnimation getAnimation() {
        if (animation == null) animation = BlockBenchAnims.get().getAnimation(getAnimationId());
        return animation;
    }

    @Override
    public JsonPresetType getType() {
        return KFAnimType.BLOCK_BENCH;
    }
}

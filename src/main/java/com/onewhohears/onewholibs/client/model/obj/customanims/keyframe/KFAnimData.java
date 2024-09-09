package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.gson.JsonObject;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetInstance;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import com.onewhohears.onewholibs.util.UtilParse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public abstract class KFAnimData extends JsonPresetStats {

    private final String animation_player;
    private final String animation_id;

    private KeyframeAnimationPlayer<?> player;

    public KFAnimData(ResourceLocation key, JsonObject json) {
        super(key, json);
        animation_player = UtilParse.getStringSafe(json, "animation_player", "");
        animation_id = UtilParse.getStringSafe(json, "animation_id", "");
    }

    @Override
    public @Nullable JsonPresetInstance<?> createPresetInstance() {
        return null;
    }

    public String getAnimationPlayerId() {
        return animation_player;
    }

    public String getAnimationId() {
        return animation_id;
    }

    public abstract KeyframeAnimation getAnimation();

    public <T extends Entity> KeyframeAnimationPlayer<T> getAnimationPlayer() {
        if (player == null) player = KFAnimPlayers.createAnimationPlayer(getAnimationPlayerId(), this);
        return (KeyframeAnimationPlayer<T>) player;
    }

}

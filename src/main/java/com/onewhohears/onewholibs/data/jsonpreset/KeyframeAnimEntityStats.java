package com.onewhohears.onewholibs.data.jsonpreset;

import com.google.gson.JsonObject;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KFAnimData;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KFAnimPlayers;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimationPlayer;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimsEntityModel;
import com.onewhohears.onewholibs.util.UtilParse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class KeyframeAnimEntityStats<M extends KeyframeAnimsEntityModel<E>, E extends Entity> extends CustomAnimStats <M, E>{

    private final String[] anim_data_ids;

    private List<KeyframeAnimationPlayer<E>> animationPlayers;

    public KeyframeAnimEntityStats(ResourceLocation key, JsonObject json) {
        super(key, json);
        anim_data_ids = UtilParse.getStringArraySafe(UtilParse.getJsonSafe(json, "model_data"), "anim_data");
    }

    public List<KeyframeAnimationPlayer<E>> getAnimationPlayers() {
        if (animationPlayers == null) animationPlayers = KFAnimPlayers.getAnimPlayersFromDataIds(anim_data_ids);
        return animationPlayers;
    }
}

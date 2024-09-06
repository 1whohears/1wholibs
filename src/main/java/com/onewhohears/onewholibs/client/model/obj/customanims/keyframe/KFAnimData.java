package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.gson.JsonObject;
import com.onewhohears.onewholibs.util.UtilParse;

public class KFAnimData {

    private final String id;
    private final String animation_id;
    private final String animation_type;
    private final String trigger;
    private final String controller;

    public KFAnimData(String id, JsonObject json) {
        this.id = id;
        animation_id = UtilParse.getStringSafe(json, "animation_id", "");
        animation_type = UtilParse.getStringSafe(json, "animation_type", "");
        trigger = UtilParse.getStringSafe(json, "trigger", "");
        controller = UtilParse.getStringSafe(json, "controller", "");
    }

    public String getId() {
        return id;
    }

    public String getAnimationType() {
        return animation_type;
    }

    public String getTriggerId() {
        return trigger;
    }

    public String getControllerId() {
        return controller;
    }

    public String getAnimationId() {
        return animation_id;
    }
}

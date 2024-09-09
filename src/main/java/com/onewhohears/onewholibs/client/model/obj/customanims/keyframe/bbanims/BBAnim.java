package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.bbanims;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.ControllableAnimPlayer;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimation;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimationController;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimationTrigger;
import com.onewhohears.onewholibs.util.math.UtilAngles;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Set;

public class BBAnim implements KeyframeAnimation {

    private final Map<String, Vector3f> pivots;
    private final float length;

    public BBAnim(JsonObject json, Map<String, Vector3f> pivots) {
        this.pivots = pivots;
        length = json.get("animation_length").getAsFloat();
        JsonObject bones = json.get("bones").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> boneJsons = bones.entrySet();
        for (Map.Entry<String, JsonElement> b : boneJsons) {
            JsonObject bone = b.getValue().getAsJsonObject();

        }
    }

    @Override
    public void applyAnimationAtSecond(ImmutableMap.Builder<String, Matrix4f> builder, float seconds) {

    }

    public Map<String, Vector3f> getPivots() {
        return pivots;
    }

    @Override
    public float getAnimationLength() {
        return length;
    }

}

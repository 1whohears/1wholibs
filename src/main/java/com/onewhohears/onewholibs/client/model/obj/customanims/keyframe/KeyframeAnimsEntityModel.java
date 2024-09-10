package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.mojang.math.Matrix4f;
import com.onewhohears.onewholibs.client.model.obj.customanims.CustomAnimsEntityModel;
import com.onewhohears.onewholibs.client.model.obj.customanims.EntityModelTransform;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.model.renderable.CompositeRenderable;

import java.util.List;
import java.util.Map;

public class KeyframeAnimsEntityModel<T extends Entity> extends CustomAnimsEntityModel<T> {

    private List<KeyframeAnimationPlayer<T>> keyframeAnimations;
    private String[] anim_data_ids;

    public KeyframeAnimsEntityModel(String model_id, JsonArray transforms, List<KeyframeAnimationPlayer<T>> keyframeAnimations) {
        super(model_id, transforms);
        this.keyframeAnimations = keyframeAnimations;
    }

    public KeyframeAnimsEntityModel(String model_id, List<KeyframeAnimationPlayer<T>> keyframeAnimations) {
        this(model_id, new JsonArray(), keyframeAnimations);
    }

    public KeyframeAnimsEntityModel(String model_id, JsonArray transforms, String... anim_data_ids) {
        super(model_id, transforms);
        this.anim_data_ids = anim_data_ids;
    }

    public KeyframeAnimsEntityModel(String model_id, String... anim_data_ids) {
        super(model_id, new JsonArray());
        this.anim_data_ids = anim_data_ids;
    }

    @Override
    protected void addComponentTransforms(Map<String, Matrix4f> transforms, T entity, float partialTicks) {
        super.addComponentTransforms(transforms, entity, partialTicks);
        for (KeyframeAnimationPlayer<T> anim : getKeyframeAnimations())
            if (anim.isAnimationActive(entity))
                anim.applyAnimation(transforms, entity, partialTicks);
    }

    public List<KeyframeAnimationPlayer<T>> getKeyframeAnimations() {
        if (keyframeAnimations == null && anim_data_ids != null)
            keyframeAnimations = KFAnimPlayers.getAnimPlayersFromDataIds(anim_data_ids);
        return keyframeAnimations;
    }
}

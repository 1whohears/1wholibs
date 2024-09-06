package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.mojang.math.Matrix4f;
import com.onewhohears.onewholibs.client.model.obj.customanims.CustomAnimsEntityModel;
import com.onewhohears.onewholibs.client.model.obj.customanims.EntityModelTransform;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.model.renderable.CompositeRenderable;

import java.util.List;

public class KeyframeAnimsEntityModel<T extends Entity> extends CustomAnimsEntityModel<T> {

    protected final List<KeyframeAnimation<T>> keyframeAnimations;

    public KeyframeAnimsEntityModel(String model_id, JsonArray transforms, List<KeyframeAnimation<T>> keyframeAnimations) {
        super(model_id, transforms);
        this.keyframeAnimations = keyframeAnimations;
    }

    @Override
    protected CompositeRenderable.Transforms getComponentTransforms(T entity, float partialTicks) {
        ImmutableMap.Builder<String, Matrix4f> builder = ImmutableMap.builder();
        for (EntityModelTransform<T> trans : transforms.values())
            builder.put(trans.getKey(), trans.getTransform(entity, partialTicks));
        for (KeyframeAnimation<T> anim : keyframeAnimations)
            if (anim.isAnimationActive(entity))
                anim.applyAnimation(builder, entity, partialTicks);
        return CompositeRenderable.Transforms.of(builder.build());
    }
}

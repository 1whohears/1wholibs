package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.state_machine;

import com.google.gson.JsonArray;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimationPlayer;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimsEntityModel;
import com.onewhohears.onewholibs.entity.StateMachineAnimEntity;
import com.onewhohears.onewholibs.util.math.Mat4f;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onewhohears.onewholibs.client.model.obj.customanims.EntityModelTransform.NOTHING;

public class StateMachineAnimEntityModel<T extends Entity & StateMachineAnimEntity> extends KeyframeAnimsEntityModel<T> {

    private final Map<String, Mat4f> startTransforms = new HashMap<>();
    private final Map<String, Mat4f> endTransforms = new HashMap<>();

    public StateMachineAnimEntityModel(String model_id, JsonArray transforms, List<KeyframeAnimationPlayer<T>> keyframeAnimations) {
        super(model_id, transforms, keyframeAnimations);
    }

    public StateMachineAnimEntityModel(String model_id, List<KeyframeAnimationPlayer<T>> keyframeAnimations) {
        super(model_id, keyframeAnimations);
    }

    public StateMachineAnimEntityModel(String model_id, JsonArray transforms, String... anim_data_ids) {
        super(model_id, transforms, anim_data_ids);
    }

    public StateMachineAnimEntityModel(String model_id, String... anim_data_ids) {
        super(model_id, anim_data_ids);
    }

    @Override
    protected void addComponentTransforms(Map<String, Mat4f> transforms, T entity, float partialTicks) {
        super.addComponentTransforms(transforms, entity, partialTicks);
        if (entity.getTransitionAnimIdStart().isEmpty() || entity.getTransitionAnimIdEnd().isEmpty()) return;
        KeyframeAnimationPlayer<T> transStart = getPlayerById(entity.getTransitionAnimIdStart());
        KeyframeAnimationPlayer<T> transEnd = getPlayerById(entity.getTransitionAnimIdEnd());
        if (transStart == null || transEnd == null) return;
        startTransforms.clear();
        endTransforms.clear();
        transStart.getAnimation().applyAnimationAtSecond(startTransforms, entity.getTransitionStartSeconds());
        transEnd.getAnimation().applyAnimationAtSecond(endTransforms, 0.01f);
        startTransforms.keySet().forEach(key -> {
            if (!endTransforms.containsKey(key)) endTransforms.put(key, NOTHING);
        });
        endTransforms.keySet().forEach(key -> {
            if (!startTransforms.containsKey(key)) startTransforms.put(key, NOTHING);
        });
        float percent = entity.getTransitionPercent(partialTicks);
        startTransforms.forEach((key, startMat) -> {
            Mat4f endMat = endTransforms.get(key);
            Mat4f lerpMat = new Mat4f(startMat);
            lerpMat.lerpAnim(endMat, percent);
            transforms.put(key, lerpMat);
        });
    }
}

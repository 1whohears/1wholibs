package com.onewhohears.onewholibs.client.model.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.onewhohears.onewholibs.util.math.UtilAngles;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public interface ObjEntityModel<T extends Entity> {

    Matrix4f INVISIBLE = Matrix4f.createScaleMatrix(0, 0, 0);
    /**
     * DO NOT TOUCH
     */
    Map<String, Matrix4f> TRANSFORMS = new HashMap<>();

    String getModelId();
    void addComponentTransforms(Map<String, Matrix4f> transforms, T entity, float partialTicks);
    void renderModel(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks,
                     int lightmap, int overlay, Map<String, Matrix4f> transforms);

    default void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource,
                        int lightmap, float partialTicks) {
        rotate(entity, partialTicks, poseStack);
        handleGlobalOverrides(entity, partialTicks, poseStack);
        TRANSFORMS.clear();
        addComponentTransforms(TRANSFORMS, entity, partialTicks);
        renderModel(entity, poseStack, bufferSource, partialTicks, getLight(entity, lightmap), getOverlay(entity), TRANSFORMS);
    }

    default void rotate(T entity, float partialTicks, PoseStack poseStack) {
        Vector3f pivot = getGlobalPivot();
        Quaternion yRot = Vector3f.YN.rotationDegrees(entity.getViewYRot(partialTicks));
        Quaternion xRot = Vector3f.XP.rotationDegrees(entity.getViewXRot(partialTicks));
        if (!UtilGeometry.isZero(pivot)) {
            if (globalRotateY()) poseStack.mulPoseMatrix(UtilAngles.pivotInvRot(pivot, yRot));
            if (globalRotateX()) poseStack.mulPoseMatrix(UtilAngles.pivotInvRot(pivot, xRot));
        } else {
            if (globalRotateY()) poseStack.mulPose(yRot);
            if (globalRotateX()) poseStack.mulPose(xRot);
        }
    }

    default void handleGlobalOverrides(T entity, float partialTicks, PoseStack poseStack) {
        Vector3f pivot = getGlobalPivot();
        if (!UtilGeometry.isZero(pivot)) poseStack.translate(pivot.x(), pivot.y(), pivot.z());
        getModelOverride().applyNoTranslate(poseStack);
    }

    default ObjEntityModels.ModelOverrides getModelOverride() {
        return ObjEntityModels.get().getModelOverride(getModelId());
    }

    default int getLight(T entity, int lightmap) {
        return lightmap;
    }

    default int getOverlay(T entity) {
        return OverlayTexture.NO_OVERLAY;
    }

    default Vector3f getGlobalPivot() {
        return getModelOverride().translate;
    }

    default boolean globalRotateX() {
        return true;
    }

    default boolean globalRotateY() {
        return true;
    }
}

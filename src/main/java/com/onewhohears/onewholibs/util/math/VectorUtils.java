package com.onewhohears.onewholibs.util.math;

import org.joml.Vector3f;
import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;

public class VectorUtils {

    public static final Vector3f POSITIVE_X = new Vector3f(1, 0, 0);
    public static final Vector3f POSITIVE_Y = new Vector3f(0, 1, 0);
    public static final Vector3f NEGATIVE_Y = new Vector3f(0, -1, 0);
    public static final Vector3f POSITIVE_Z = new Vector3f(0, 0, 1);

    public static void applyRotation(PoseStack poseStack, float[] rotation) {
        if (rotation[0] != 0) poseStack.mulPose(new Quaternionf().rotationAxis(rotation[0], POSITIVE_X));
        if (rotation[1] != 0) poseStack.mulPose(new Quaternionf().rotationAxis(rotation[1], NEGATIVE_Y));
        if (rotation[2] != 0) poseStack.mulPose(new Quaternionf().rotationAxis(rotation[2], POSITIVE_Z));
    }

    public static void applyTranslation(PoseStack poseStack, Vector3f translate) {
        poseStack.translate(translate.x(), translate.y(), translate.z());
    }

    public static void applyScale(PoseStack poseStack, float scale, float[] scale3d) {
        poseStack.scale(scale * scale3d[0], scale * scale3d[1], scale * scale3d[2]);
    }

    public static Quaternionf rotationQuaternion(Vector3f axis, float angleDegrees) {
        float angleRadians = (float) Math.toRadians(angleDegrees);
        return new Quaternionf().rotationAxis(angleRadians, axis);
    }

}


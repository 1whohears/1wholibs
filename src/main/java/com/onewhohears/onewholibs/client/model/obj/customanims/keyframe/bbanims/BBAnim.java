package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.bbanims;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimation;
import com.onewhohears.onewholibs.util.UtilParse;
import com.onewhohears.onewholibs.util.math.UtilAngles;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BBAnim implements KeyframeAnimation {

    private final Map<String, Vector3f> pivots;
    private final float length;
    private final List<BBBone> bones = new ArrayList<>();

    public BBAnim(JsonObject json, Map<String, Vector3f> pivots) {
        this.pivots = pivots;
        length = json.get("animation_length").getAsFloat();
        JsonObject bonesJson = json.get("bones").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> boneJsons = bonesJson.entrySet();
        for (Map.Entry<String, JsonElement> b : boneJsons) {
            JsonObject bone = b.getValue().getAsJsonObject();
            bones.add(new BBBone(b.getKey(), bone));
        }
    }

    @Override
    public void applyAnimationAtSecond(ImmutableMap.Builder<String, Matrix4f> builder, float seconds) {
        bones.forEach((bone) -> bone.applyAnimationAtSecond(builder, seconds));
    }

    public Map<String, Vector3f> getPivots() {
        return pivots;
    }

    @Override
    public float getAnimationLength() {
        return length;
    }

    public class BBBone {
        public final String name;
        public final float pivotX, pivotY, pivotZ;
        public final Transform rotation, translation, scale;
        public BBBone(String name, JsonObject json) {
            this.name = name;
            if (pivots.containsKey(name)) {
                Vector3f p = pivots.get(name);
                pivotX = p.x(); pivotY = p.y(); pivotZ = p.z();
            } else {
                pivotX = 0; pivotY = 0; pivotZ = 0;
            }
            this.rotation = new Rotation(UtilParse.getJsonSafe(json, "rotation"));
            this.translation = new Translation(UtilParse.getJsonSafe(json, "position"));
            this.scale = new Scale(UtilParse.getJsonSafe(json, "scale"));
        }
        public void applyAnimationAtSecond(ImmutableMap.Builder<String, Matrix4f> builder, float seconds) {
            Matrix4f mat = new Matrix4f();
            mat.setIdentity();
            if (!rotation.isEmpty) mat.multiply(rotation.getTransformAtSecond(seconds, pivotX, pivotY, pivotZ));
            if (!translation.isEmpty) mat.multiply(translation.getTransformAtSecond(seconds, pivotX, pivotY, pivotZ));
            if (!scale.isEmpty) mat.multiply(scale.getTransformAtSecond(seconds, pivotX, pivotY, pivotZ));
            builder.put(name, mat);
        }
    }

    public abstract static class Transform {
        public final List<Float> times = new ArrayList<>();
        public final List<Vector3f> transforms = new ArrayList<>();
        public final boolean isEmpty;
        public Transform(JsonObject json) {
            Set<Map.Entry<String, JsonElement>> jsons = json.entrySet();
            for (Map.Entry<String, JsonElement> t : jsons) {
                times.add(Float.parseFloat(t.getKey()));
                JsonArray trans = t.getValue().getAsJsonArray();
                float x = trans.get(0).getAsFloat();
                float y = trans.get(1).getAsFloat();
                float z = trans.get(2).getAsFloat();
                transforms.add(new Vector3f(x, y, z));
            }
            isEmpty = times.isEmpty();
        }
        public Vector3f interpolate(float time) {
            if (isEmpty) return getDefaultTransform();
            if (time <= times.get(0)) return transforms.get(0);
            for (int i = 1; i < times.size(); ++i) {
                float t = times.get(i);
                if (time == t) return transforms.get(i);
                if (time > t) continue;
                float to = times.get(i-1);
                float p = (time - to) / (t - to);
                Vector3f trans = transforms.get(i);
                Vector3f transo = transforms.get(i-1);
                return new Vector3f(Mth.lerp(p,transo.x(),trans.x()),
                        Mth.lerp(p,transo.y(),trans.y()),
                        Mth.lerp(p,transo.z(),trans.z()));
            }
            return transforms.get(transforms.size()-1);
        }
        public abstract Matrix4f getTransformAtSecond(float seconds, float pivotX, float pivotY, float pivotZ);
        public abstract Vector3f getDefaultTransform();
    }

    public static class Rotation extends Transform {
        public Rotation(JsonObject json) {
            super(json);
        }
        @Override
        public Matrix4f getTransformAtSecond(float seconds, float pivotX, float pivotY, float pivotZ) {
            Vector3f rotation = interpolate(seconds);
            Matrix4f rot = UtilAngles.pivotPixelsRotX(pivotX, pivotY, pivotZ, -rotation.x());
            rot.multiply(UtilAngles.pivotPixelsRotY(pivotX, pivotY, pivotZ, -rotation.y()));
            rot.multiply(UtilAngles.pivotPixelsRotZ(pivotX, pivotY, pivotZ, rotation.z()));
            return rot;
        }
        @Override
        public Vector3f getDefaultTransform() {
            return Vector3f.ZERO;
        }
    }

    public static class Translation extends Transform {
        public Translation(JsonObject json) {
            super(json);
        }
        @Override
        public Matrix4f getTransformAtSecond(float seconds, float pivotX, float pivotY, float pivotZ) {
            Vector3f trans = interpolate(seconds);
            trans.mul(0.0625f);
            return Matrix4f.createTranslateMatrix(trans.x(), trans.y(), trans.z());
        }
        @Override
        public Vector3f getDefaultTransform() {
            return Vector3f.ZERO;
        }
    }

    public static class Scale extends Transform {
        private final Vector3f ONE = new Vector3f(1,1,1);
        public Scale(JsonObject json) {
            super(json);
        }
        @Override
        public Matrix4f getTransformAtSecond(float seconds, float pivotX, float pivotY, float pivotZ) {
            Vector3f scale = interpolate(seconds);
            return Matrix4f.createScaleMatrix(scale.x(), scale.y(), scale.z());
        }
        @Override
        public Vector3f getDefaultTransform() {
            return ONE;
        }
    }

}

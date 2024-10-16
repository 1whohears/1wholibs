package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.bbanims;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimation;
import com.onewhohears.onewholibs.util.UtilParse;
import com.onewhohears.onewholibs.util.math.UtilAngles;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

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
    public void applyAnimationAtSecond(Map<String, Matrix4f> transforms, float seconds) {
        bones.forEach((bone) -> bone.applyAnimationAtSecond(transforms, seconds));
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
                pivotX = p.x();
                pivotY = p.y();
                pivotZ = p.z();
            } else {
                pivotX = 0;
                pivotY = 0;
                pivotZ = 0;
            }
            this.rotation = new Rotation(UtilParse.getJsonSafe(json, "rotation"));
            this.translation = new Translation(UtilParse.getJsonSafe(json, "position"));
            this.scale = new Scale(UtilParse.getJsonSafe(json, "scale"));
        }

        public void applyAnimationAtSecond(Map<String, Matrix4f> transforms, float seconds) {
            Matrix4f mat = new Matrix4f();

            // Apply the transformations in sequence: rotation, translation, scale
            if (!rotation.isEmpty) {
                mat.mul(rotation.getTransformAtSecond(seconds, pivotX, pivotY, pivotZ));
            }
            if (!translation.isEmpty) {
                mat.mul(translation.getTransformAtSecond(seconds, pivotX, pivotY, pivotZ));
            }
            if (!scale.isEmpty) {
                mat.mul(scale.getTransformAtSecond(seconds, pivotX, pivotY, pivotZ));
            }

            // Store the result in the transforms map
            transforms.put(name, mat);
        }
    }

    public abstract static class Transform {
        public final List<Keyframe> keyframes = new ArrayList<>();
        public final boolean isEmpty;

        public Transform(JsonObject json) {
            Set<Map.Entry<String, JsonElement>> jsons = json.entrySet();
            for (Map.Entry<String, JsonElement> t : jsons) {
                float time = Float.parseFloat(t.getKey());
                if (t.getValue().isJsonArray())
                    keyframes.add(new Keyframe(time, t.getValue().getAsJsonArray()));
                else if (t.getValue().isJsonObject())
                    keyframes.add(new Keyframe(time, t.getValue().getAsJsonObject()));
            }
            isEmpty = keyframes.isEmpty();
        }

        public Vector3f interpolate(float time) {
            if (isEmpty) return getDefaultTransform();
            if (time <= keyframes.get(0).time) return keyframes.get(0).pre;
            for (int i = 1; i < keyframes.size(); ++i) {
                Keyframe keyframe = keyframes.get(i);
                float t = keyframe.time;
                if (time == t) return keyframes.get(i).post;
                if (time > t) continue;
                Keyframe before = keyframes.get(i - 1);
                if (before.lerp_mode == LerpMode.CATMULLROM) {
                    Keyframe beforeBefore = (i >= 2) ? keyframes.get(i - 2) : before;
                    Keyframe after = (i <= keyframes.size() - 2) ? keyframes.get(i + 1) : keyframe;
                    return keyframe.lerpWithEnd(before, time, beforeBefore, after);
                }
                return keyframe.lerpWithEnd(before, time);
            }
            return keyframes.get(keyframes.size() - 1).post;
        }

        public abstract Matrix4f getTransformAtSecond(float seconds, float pivotX, float pivotY, float pivotZ);

        public abstract Vector3f getDefaultTransform();
    }

    public static class Keyframe {
        public final float time, alpha;
        public final Vector3f pre, post;
        public final LerpMode lerp_mode;
        private Vec2[][] cmrs;

        public Keyframe(float time, JsonArray json) {
            this.time = time;
            pre = post = fromJsonArray(json);
            lerp_mode = LerpMode.LINEAR;
            alpha = 0.5f;
        }

        public Keyframe(float time, JsonObject json) {
            this.time = time;
            if (json.has("post"))
                post = fromJsonArray(json.getAsJsonArray("post"));
            else post = new Vector3f(0, 0, 0);
            ;
            if (json.has("pre"))
                pre = fromJsonArray(json.getAsJsonArray("pre"));
            else pre = post;
            String mode = UtilParse.getStringSafe(json, "lerp_mode", "");
            if (mode.equals("catmullrom")) lerp_mode = LerpMode.CATMULLROM;
            else lerp_mode = LerpMode.LINEAR;
            alpha = UtilParse.getFloatSafe(json, "alpha", 0.15f);
        }

        public Vector3f lerpWithEnd(Keyframe before, float animTime, Keyframe... surrounding) {
            if (before.lerp_mode == LerpMode.CATMULLROM && surrounding.length == 2) {
                if (cmrs == null) cmrs = calcCMRs(before.alpha, surrounding[0], before, this, surrounding[1]);
                return new Vector3f(UtilGeometry.findYInCatmullromArray(animTime, cmrs[0]),
                        UtilGeometry.findYInCatmullromArray(animTime, cmrs[1]),
                        UtilGeometry.findYInCatmullromArray(animTime, cmrs[2]));
            }
            float p = (animTime - before.time) / (time - before.time);
            return new Vector3f(Mth.lerp(p, before.post.x(), pre.x()),
                    Mth.lerp(p, before.post.y(), pre.y()),
                    Mth.lerp(p, before.post.z(), pre.z()));
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Keyframe keyframe) return is(keyframe);
            return false;
        }

        public boolean is(Keyframe keyframe) {
            return this.time == keyframe.time;
        }
    }

    private static Vec2[][] calcCMRs(float alpha, Keyframe... kfs) {
        float pointsPerSecond = 30;
        int points = (int) ((kfs[3].time - kfs[0].time) * pointsPerSecond);
        Vec2[][] cmrs = new Vec2[3][points];
        cmrs[0] = UtilGeometry.catmullromArray(points, alpha, kfs[0].time, kfs[0].post.x(),
                kfs[1].time, kfs[1].post.x(), kfs[2].time, kfs[2].post.x(), kfs[3].time, kfs[3].post.x());
        //UtilGeometry.DEBUG_CATMULLROM = true;
        cmrs[1] = UtilGeometry.catmullromArray(points, alpha, kfs[0].time, kfs[0].post.y(),
                kfs[1].time, kfs[1].post.y(), kfs[2].time, kfs[2].post.y(), kfs[3].time, kfs[3].post.y());
        //UtilGeometry.DEBUG_CATMULLROM = false;
        cmrs[2] = UtilGeometry.catmullromArray(points, alpha, kfs[0].time, kfs[0].post.z(),
                kfs[1].time, kfs[1].post.z(), kfs[2].time, kfs[2].post.z(), kfs[3].time, kfs[3].post.z());
        return cmrs;
    }

    public static Vector3f fromJsonArray(JsonArray json) {
        float x = json.get(0).getAsFloat();
        float y = json.get(1).getAsFloat();
        float z = json.get(2).getAsFloat();
        return new Vector3f(x, y, z);
    }

    public enum LerpMode {
        LINEAR,
        CATMULLROM
    }

    public static class Rotation extends Transform {
        public Rotation(JsonObject json) {
            super(json);
        }

        @Override
        public Matrix4f getTransformAtSecond(float seconds, float pivotX, float pivotY, float pivotZ) {
            Vector3f rotation = interpolate(seconds);

            // Create the rotation matrices for X, Y, Z axes, with pivot offsets
            Matrix4f rot = UtilAngles.pivotPixelsRotX(pivotX, pivotY, pivotZ, -rotation.x());
            rot.mul(UtilAngles.pivotPixelsRotY(pivotX, pivotY, pivotZ, -rotation.y()));
            rot.mul(UtilAngles.pivotPixelsRotZ(pivotX, pivotY, pivotZ, rotation.z()));

            return rot;
        }

        @Override
        public Vector3f getDefaultTransform() {
            // Use the zero vector
            return new Vector3f(0, 0, 0);
        }
    }

    public static class Translation extends Transform {
        public Translation(JsonObject json) {
            super(json);
        }

        @Override
        public Matrix4f getTransformAtSecond(float seconds, float pivotX, float pivotY, float pivotZ) {
            Vector3f trans = interpolate(seconds);

            // Scale down the translation values as before
            trans.mul(0.0625f);

            // Create and return the translation matrix using JOML's translation method
            return new Matrix4f().translation(trans.x(), trans.y(), trans.z());
        }

        @Override
        public Vector3f getDefaultTransform() {
            // Return the zero vector for default translation
            return new Vector3f(0, 0, 0);
        }
    }

    public static class Scale extends Transform {
        private static final Vector3f ONE = new Vector3f(1, 1, 1);

        public Scale(JsonObject json) {
            super(json);
        }

        @Override
        public Matrix4f getTransformAtSecond(float seconds, float pivotX, float pivotY, float pivotZ) {
            Vector3f scale = interpolate(seconds);

            // Create and return the scale matrix using JOML's scaling method
            return new Matrix4f().scaling(scale.x(), scale.y(), scale.z());
        }

        @Override
        public Vector3f getDefaultTransform() {
            // Return the unit vector for default scaling
            return ONE;
        }
    }
}

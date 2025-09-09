package com.onewhohears.onewholibs.util.math;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Mojang in their infinite wisdom decided to refactor/remove a bunch of their math util classes from 1.19.2.
 * My mods were designed around these math classes, so I have copied the 1.19.2 implementation here so I don't
 * have to worry about Mojang changing it again. If this is a copy right issue this mod is MIT and who in their
 * right mind is going to copy right math.
 */
public final class Vec3f {
    public static final Codec<Vec3f> CODEC;
    public static Vec3f XN;
    public static Vec3f XP;
    public static Vec3f YN;
    public static Vec3f YP;
    public static Vec3f ZN;
    public static Vec3f ZP;
    public static Vec3f ZERO;
    private float x;
    private float y;
    private float z;

    public Vec3f() {
    }

    public Vec3f(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public Vec3f(Vec4f vec4f) {
        this(vec4f.x(), vec4f.y(), vec4f.z());
    }

    public Vec3f(Vec3 vec3) {
        this((float)vec3.x, (float)vec3.y, (float)vec3.z);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            Vec3f Vec3f = (Vec3f)object;
            if (Float.compare(Vec3f.x, this.x) != 0) {
                return false;
            } else if (Float.compare(Vec3f.y, this.y) != 0) {
                return false;
            } else {
                return Float.compare(Vec3f.z, this.z) == 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        return i;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
    }

    public void mul(float f, float g, float h) {
        this.x *= f;
        this.y *= g;
        this.z *= h;
    }

    public void clamp(Vec3f Vec3f, Vec3f Vec3f2) {
        this.x = Mth.clamp(this.x, Vec3f.x(), Vec3f2.x());
        this.y = Mth.clamp(this.y, Vec3f.x(), Vec3f2.y());
        this.z = Mth.clamp(this.z, Vec3f.z(), Vec3f2.z());
    }

    public void clamp(float f, float g) {
        this.x = Mth.clamp(this.x, f, g);
        this.y = Mth.clamp(this.y, f, g);
        this.z = Mth.clamp(this.z, f, g);
    }

    public void set(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public void load(Vec3f Vec3f) {
        this.x = Vec3f.x;
        this.y = Vec3f.y;
        this.z = Vec3f.z;
    }

    public void add(float f, float g, float h) {
        this.x += f;
        this.y += g;
        this.z += h;
    }

    public void add(Vec3f Vec3f) {
        this.x += Vec3f.x;
        this.y += Vec3f.y;
        this.z += Vec3f.z;
    }

    public void sub(Vec3f Vec3f) {
        this.x -= Vec3f.x;
        this.y -= Vec3f.y;
        this.z -= Vec3f.z;
    }

    public float dot(Vec3f Vec3f) {
        return this.x * Vec3f.x + this.y * Vec3f.y + this.z * Vec3f.z;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z;
        if ((double)f < 1.0E-5) {
            return false;
        } else {
            float g = (float) Mth.fastInvSqrt(f);
            this.x *= g;
            this.y *= g;
            this.z *= g;
            return true;
        }
    }

    public void cross(Vec3f Vec3f) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = Vec3f.x();
        float j = Vec3f.y();
        float k = Vec3f.z();
        this.x = g * k - h * j;
        this.y = h * i - f * k;
        this.z = f * j - g * i;
    }

    public void transform(Mat3f mat3f) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        this.x = mat3f.m00 * f + mat3f.m01 * g + mat3f.m02 * h;
        this.y = mat3f.m10 * f + mat3f.m11 * g + mat3f.m12 * h;
        this.z = mat3f.m20 * f + mat3f.m21 * g + mat3f.m22 * h;
    }

    public void transform(QuaternionF quaternion) {
        QuaternionF quaternion2 = new QuaternionF(quaternion);
        quaternion2.mul(new QuaternionF(this.x(), this.y(), this.z(), 0.0F));
        QuaternionF quaternion3 = new QuaternionF(quaternion);
        quaternion3.conj();
        quaternion2.mul(quaternion3);
        this.set(quaternion2.i(), quaternion2.j(), quaternion2.k());
    }

    public void lerp(Vec3f Vec3f, float f) {
        float g = 1.0F - f;
        this.x = this.x * g + Vec3f.x * f;
        this.y = this.y * g + Vec3f.y * f;
        this.z = this.z * g + Vec3f.z * f;
    }

    public QuaternionF rotation(float f) {
        return new QuaternionF(this, f, false);
    }

    public QuaternionF rotationDegrees(float f) {
        return new QuaternionF(this, f, true);
    }

    public Vec3f copy() {
        return new Vec3f(this.x, this.y, this.z);
    }

    public void map(Float2FloatFunction float2FloatFunction) {
        this.x = float2FloatFunction.get(this.x);
        this.y = float2FloatFunction.get(this.y);
        this.z = float2FloatFunction.get(this.z);
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    static {
        CODEC = Codec.FLOAT.listOf().comapFlatMap((list) -> Util.fixedSize(list, 3).map((listx) -> new Vec3f((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2))), (Vec3f) -> ImmutableList.of(Vec3f.x, Vec3f.y, Vec3f.z));
        XN = new Vec3f(-1.0F, 0.0F, 0.0F);
        XP = new Vec3f(1.0F, 0.0F, 0.0F);
        YN = new Vec3f(0.0F, -1.0F, 0.0F);
        YP = new Vec3f(0.0F, 1.0F, 0.0F);
        ZN = new Vec3f(0.0F, 0.0F, -1.0F);
        ZP = new Vec3f(0.0F, 0.0F, 1.0F);
        ZERO = new Vec3f(0.0F, 0.0F, 0.0F);
    }

    public Vector3f convert() {
        return new Vector3f(x, y, z);
    }

    public static Vec3f from(Vector3f v) {
        return new Vec3f(v.x(), v.y(), v.z());
    }
}

package com.onewhohears.onewholibs.util.math;

import com.mojang.math.Vector4f;
import net.minecraft.util.Mth;

/**
 * Mojang in their infinite wisdom decided to refactor/remove a bunch of their math util classes from 1.19.2.
 * My mods were designed around these math classes, so I have copied the 1.19.2 implementation here so I don't
 * have to worry about Mojang changing it again. If this is a copy right issue this mod is MIT and who in their
 * right mind is going to copy right math.
 */
public final class Vec4f {
    private float x;
    private float y;
    private float z;
    private float w;

    public Vec4f() {
    }

    public Vec4f(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public Vec4f(Vec3f vector3f) {
        this(vector3f.x(), vector3f.y(), vector3f.z(), 1.0F);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            Vec4f Vec4f = (Vec4f)object;
            if (Float.compare(Vec4f.x, this.x) != 0) {
                return false;
            } else if (Float.compare(Vec4f.y, this.y) != 0) {
                return false;
            } else if (Float.compare(Vec4f.z, this.z) != 0) {
                return false;
            } else {
                return Float.compare(Vec4f.w, this.w) == 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        i = 31 * i + Float.floatToIntBits(this.w);
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

    public float w() {
        return this.w;
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
        this.w *= f;
    }

    public void mul(Vec3f vector3f) {
        this.x *= vector3f.x();
        this.y *= vector3f.y();
        this.z *= vector3f.z();
    }

    public void set(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public void add(float f, float g, float h, float i) {
        this.x += f;
        this.y += g;
        this.z += h;
        this.w += i;
    }

    public float dot(Vec4f Vec4f) {
        return this.x * Vec4f.x + this.y * Vec4f.y + this.z * Vec4f.z + this.w * Vec4f.w;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if ((double)f < 1.0E-5) {
            return false;
        } else {
            float g = Mth.fastInvSqrt(f);
            this.x *= g;
            this.y *= g;
            this.z *= g;
            this.w *= g;
            return true;
        }
    }

    public void transform(Mat4f matrix4f) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = this.w;
        this.x = matrix4f.m00 * f + matrix4f.m01 * g + matrix4f.m02 * h + matrix4f.m03 * i;
        this.y = matrix4f.m10 * f + matrix4f.m11 * g + matrix4f.m12 * h + matrix4f.m13 * i;
        this.z = matrix4f.m20 * f + matrix4f.m21 * g + matrix4f.m22 * h + matrix4f.m23 * i;
        this.w = matrix4f.m30 * f + matrix4f.m31 * g + matrix4f.m32 * h + matrix4f.m33 * i;
    }

    public void transform(QuaternionF quaternion) {
        QuaternionF quaternion2 = new QuaternionF(quaternion);
        quaternion2.mul(new QuaternionF(this.x(), this.y(), this.z(), 0.0F));
        QuaternionF quaternion3 = new QuaternionF(quaternion);
        quaternion3.conj();
        quaternion2.mul(quaternion3);
        this.set(quaternion2.i(), quaternion2.j(), quaternion2.k(), this.w());
    }

    public void perspectiveDivide() {
        this.x /= this.w;
        this.y /= this.w;
        this.z /= this.w;
        this.w = 1.0F;
    }

    public void lerp(Vec4f Vec4f, float f) {
        float g = 1.0F - f;
        this.x = this.x * g + Vec4f.x * f;
        this.y = this.y * g + Vec4f.y * f;
        this.z = this.z * g + Vec4f.z * f;
        this.w = this.w * g + Vec4f.w * f;
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }

    public Vector4f convert() {
        return new Vector4f(x, y, z, w);
    }
}

package com.onewhohears.onewholibs.util.math;

import net.minecraft.util.Mth;
import org.joml.Quaternionf;

/**
 * Mojang in their infinite wisdom decided to refactor/remove a bunch of their math util classes from 1.19.2.
 * My mods were designed around these math classes, so I have copied the 1.19.2 implementation here so I don't
 * have to worry about Mojang changing it again. If this is a copy right issue this mod is MIT and who in their
 * right mind is going to copy right math.
 */
public final class QuaternionF {
    public static final QuaternionF ONE = new QuaternionF(0.0F, 0.0F, 0.0F, 1.0F);
    private float i;
    private float j;
    private float k;
    private float r;

    public QuaternionF(float f, float g, float h, float i) {
        this.i = f;
        this.j = g;
        this.k = h;
        this.r = i;
    }

    public QuaternionF(Vec3f vec3f, float f, boolean bl) {
        if (bl) {
            f *= ((float)Math.PI / 180F);
        }

        float g = sin(f / 2.0F);
        this.i = vec3f.x() * g;
        this.j = vec3f.y() * g;
        this.k = vec3f.z() * g;
        this.r = cos(f / 2.0F);
    }

    public QuaternionF(float f, float g, float h, boolean bl) {
        if (bl) {
            f *= ((float)Math.PI / 180F);
            g *= ((float)Math.PI / 180F);
            h *= ((float)Math.PI / 180F);
        }

        float i = sin(0.5F * f);
        float j = cos(0.5F * f);
        float k = sin(0.5F * g);
        float l = cos(0.5F * g);
        float m = sin(0.5F * h);
        float n = cos(0.5F * h);
        this.i = i * l * n + j * k * m;
        this.j = j * k * n - i * l * m;
        this.k = i * k * n + j * l * m;
        this.r = j * l * n - i * k * m;
    }

    public QuaternionF(QuaternionF QuaternionF) {
        this.i = QuaternionF.i;
        this.j = QuaternionF.j;
        this.k = QuaternionF.k;
        this.r = QuaternionF.r;
    }

    public static QuaternionF fromYXZ(float f, float g, float h) {
        QuaternionF QuaternionF = ONE.copy();
        QuaternionF.mul(new QuaternionF(0.0F, (float)Math.sin((double)(f / 2.0F)), 0.0F, (float)Math.cos((double)(f / 2.0F))));
        QuaternionF.mul(new QuaternionF((float)Math.sin((double)(g / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(g / 2.0F))));
        QuaternionF.mul(new QuaternionF(0.0F, 0.0F, (float)Math.sin((double)(h / 2.0F)), (float)Math.cos((double)(h / 2.0F))));
        return QuaternionF;
    }

    public static QuaternionF fromXYZDegrees(Vec3f vec3f) {
        return fromXYZ((float)Math.toRadians((double)vec3f.x()), (float)Math.toRadians((double)vec3f.y()), (float)Math.toRadians((double)vec3f.z()));
    }

    public static QuaternionF fromXYZ(Vec3f vec3f) {
        return fromXYZ(vec3f.x(), vec3f.y(), vec3f.z());
    }

    public static QuaternionF fromXYZ(float f, float g, float h) {
        QuaternionF QuaternionF = ONE.copy();
        QuaternionF.mul(new QuaternionF((float)Math.sin((double)(f / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(f / 2.0F))));
        QuaternionF.mul(new QuaternionF(0.0F, (float)Math.sin((double)(g / 2.0F)), 0.0F, (float)Math.cos((double)(g / 2.0F))));
        QuaternionF.mul(new QuaternionF(0.0F, 0.0F, (float)Math.sin((double)(h / 2.0F)), (float)Math.cos((double)(h / 2.0F))));
        return QuaternionF;
    }

    public Vec3f toXYZ() {
        float f = this.r() * this.r();
        float g = this.i() * this.i();
        float h = this.j() * this.j();
        float i = this.k() * this.k();
        float j = f + g + h + i;
        float k = 2.0F * this.r() * this.i() - 2.0F * this.j() * this.k();
        float l = (float)Math.asin((double)(k / j));
        return Math.abs(k) > 0.999F * j ? new Vec3f(2.0F * (float)Math.atan2((double)this.i(), (double)this.r()), l, 0.0F) : new Vec3f((float)Math.atan2((double)(2.0F * this.j() * this.k() + 2.0F * this.i() * this.r()), (double)(f - g - h + i)), l, (float)Math.atan2((double)(2.0F * this.i() * this.j() + 2.0F * this.r() * this.k()), (double)(f + g - h - i)));
    }

    public Vec3f toXYZDegrees() {
        Vec3f vec3f = this.toXYZ();
        return new Vec3f((float)Math.toDegrees((double)vec3f.x()), (float)Math.toDegrees((double)vec3f.y()), (float)Math.toDegrees((double)vec3f.z()));
    }

    public Vec3f toYXZ() {
        float f = this.r() * this.r();
        float g = this.i() * this.i();
        float h = this.j() * this.j();
        float i = this.k() * this.k();
        float j = f + g + h + i;
        float k = 2.0F * this.r() * this.i() - 2.0F * this.j() * this.k();
        float l = (float)Math.asin((double)(k / j));
        return Math.abs(k) > 0.999F * j ? new Vec3f(l, 2.0F * (float)Math.atan2((double)this.j(), (double)this.r()), 0.0F) : new Vec3f(l, (float)Math.atan2((double)(2.0F * this.i() * this.k() + 2.0F * this.j() * this.r()), (double)(f - g - h + i)), (float)Math.atan2((double)(2.0F * this.i() * this.j() + 2.0F * this.r() * this.k()), (double)(f - g + h - i)));
    }

    public Vec3f toYXZDegrees() {
        Vec3f vec3f = this.toYXZ();
        return new Vec3f((float)Math.toDegrees((double)vec3f.x()), (float)Math.toDegrees((double)vec3f.y()), (float)Math.toDegrees((double)vec3f.z()));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            QuaternionF QuaternionF = (QuaternionF)object;
            if (Float.compare(QuaternionF.i, this.i) != 0) {
                return false;
            } else if (Float.compare(QuaternionF.j, this.j) != 0) {
                return false;
            } else if (Float.compare(QuaternionF.k, this.k) != 0) {
                return false;
            } else {
                return Float.compare(QuaternionF.r, this.r) == 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.i);
        i = 31 * i + Float.floatToIntBits(this.j);
        i = 31 * i + Float.floatToIntBits(this.k);
        i = 31 * i + Float.floatToIntBits(this.r);
        return i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("QuaternionF[").append(this.r()).append(" + ");
        stringBuilder.append(this.i()).append("i + ");
        stringBuilder.append(this.j()).append("j + ");
        stringBuilder.append(this.k()).append("k]");
        return stringBuilder.toString();
    }

    public float i() {
        return this.i;
    }

    public float j() {
        return this.j;
    }

    public float k() {
        return this.k;
    }

    public float r() {
        return this.r;
    }

    public void mul(QuaternionF QuaternionF) {
        float f = this.i();
        float g = this.j();
        float h = this.k();
        float i = this.r();
        float j = QuaternionF.i();
        float k = QuaternionF.j();
        float l = QuaternionF.k();
        float m = QuaternionF.r();
        this.i = i * j + f * m + g * l - h * k;
        this.j = i * k - f * l + g * m + h * j;
        this.k = i * l + f * k - g * j + h * m;
        this.r = i * m - f * j - g * k - h * l;
    }

    public void mul(float f) {
        this.i *= f;
        this.j *= f;
        this.k *= f;
        this.r *= f;
    }

    public void conj() {
        this.i = -this.i;
        this.j = -this.j;
        this.k = -this.k;
    }

    public void set(float f, float g, float h, float i) {
        this.i = f;
        this.j = g;
        this.k = h;
        this.r = i;
    }

    private static float cos(float f) {
        return (float)Math.cos((double)f);
    }

    private static float sin(float f) {
        return (float)Math.sin((double)f);
    }

    public void normalize() {
        float f = this.i() * this.i() + this.j() * this.j() + this.k() * this.k() + this.r() * this.r();
        if (f > 1.0E-6F) {
            float g = (float) Mth.fastInvSqrt(f);
            this.i *= g;
            this.j *= g;
            this.k *= g;
            this.r *= g;
        } else {
            this.i = 0.0F;
            this.j = 0.0F;
            this.k = 0.0F;
            this.r = 0.0F;
        }

    }

    public void slerp(QuaternionF QuaternionF, float f) {
        throw new UnsupportedOperationException();
    }

    public QuaternionF copy() {
        return new QuaternionF(this);
    }

    public Quaternionf convert() {
        return new Quaternionf(i, j, k, r);
    }

    public static QuaternionF from(Quaternionf q) {
        return new QuaternionF(q.x(), q.y(), q.z(), q.w());
    }
}

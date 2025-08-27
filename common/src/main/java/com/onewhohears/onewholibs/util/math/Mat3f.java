package com.onewhohears.onewholibs.util.math;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/**
 * Mojang in their infinite wisdom decided to refactor/remove a bunch of their math util classes from 1.19.2.
 * My mods were designed around these math classes, so I have copied the 1.19.2 implementation here so I don't
 * have to worry about Mojang changing it again. If this is a copy right issue this mod is MIT and who in their
 * right mind is going to copy right math.
 */
public final class Mat3f {
    private static final int ORDER = 3;
    private static final float G = 3.0F + 2.0F * (float)Math.sqrt((double)2.0F);
    private static final float CS = (float)Math.cos((Math.PI / 8D));
    private static final float SS = (float)Math.sin((Math.PI / 8D));
    private static final float SQ2 = 1.0F / (float)Math.sqrt((double)2.0F);
    float m00;
    float m01;
    float m02;
    float m10;
    float m11;
    float m12;
    float m20;
    float m21;
    float m22;

    public Mat3f() {
    }

    public Mat3f(QuaternionF quaternion) {
        float f = quaternion.i();
        float g = quaternion.j();
        float h = quaternion.k();
        float i = quaternion.r();
        float j = 2.0F * f * f;
        float k = 2.0F * g * g;
        float l = 2.0F * h * h;
        this.m00 = 1.0F - k - l;
        this.m11 = 1.0F - l - j;
        this.m22 = 1.0F - j - k;
        float m = f * g;
        float n = g * h;
        float o = h * f;
        float p = f * i;
        float q = g * i;
        float r = h * i;
        this.m10 = 2.0F * (m + r);
        this.m01 = 2.0F * (m - r);
        this.m20 = 2.0F * (o - q);
        this.m02 = 2.0F * (o + q);
        this.m21 = 2.0F * (n + p);
        this.m12 = 2.0F * (n - p);
    }

    public static Mat3f createScaleMatrix(float f, float g, float h) {
        Mat3f Mat3f = new Mat3f();
        Mat3f.m00 = f;
        Mat3f.m11 = g;
        Mat3f.m22 = h;
        return Mat3f;
    }

    public Mat3f(Mat4f matrix4f) {
        this.m00 = matrix4f.m00;
        this.m01 = matrix4f.m01;
        this.m02 = matrix4f.m02;
        this.m10 = matrix4f.m10;
        this.m11 = matrix4f.m11;
        this.m12 = matrix4f.m12;
        this.m20 = matrix4f.m20;
        this.m21 = matrix4f.m21;
        this.m22 = matrix4f.m22;
    }

    public Mat3f(Mat3f Mat3f) {
        this.m00 = Mat3f.m00;
        this.m01 = Mat3f.m01;
        this.m02 = Mat3f.m02;
        this.m10 = Mat3f.m10;
        this.m11 = Mat3f.m11;
        this.m12 = Mat3f.m12;
        this.m20 = Mat3f.m20;
        this.m21 = Mat3f.m21;
        this.m22 = Mat3f.m22;
    }

    private static Pair<Float, Float> approxGivensQuat(float f, float g, float h) {
        float i = 2.0F * (f - h);
        if (G * g * g < i * i) {
            float k = (float) Mth.fastInvSqrt(g * g + i * i);
            return Pair.of(k * g, k * i);
        } else {
            return Pair.of(SS, CS);
        }
    }

    private static Pair<Float, Float> qrGivensQuat(float f, float g) {
        float h = (float)Math.hypot((double)f, (double)g);
        float i = h > 1.0E-6F ? g : 0.0F;
        float j = Math.abs(f) + Math.max(h, 1.0E-6F);
        if (f < 0.0F) {
            float k = i;
            i = j;
            j = k;
        }

        float k = (float) Mth.fastInvSqrt(j * j + i * i);
        j *= k;
        i *= k;
        return Pair.of(i, j);
    }

    private static QuaternionF stepJacobi(Mat3f Mat3f) {
        Mat3f Mat3f2 = new Mat3f();
        QuaternionF quaternion = QuaternionF.ONE.copy();
        if (Mat3f.m01 * Mat3f.m01 + Mat3f.m10 * Mat3f.m10 > 1.0E-6F) {
            Pair<Float, Float> pair = approxGivensQuat(Mat3f.m00, 0.5F * (Mat3f.m01 + Mat3f.m10), Mat3f.m11);
            Float float_ = (Float)pair.getFirst();
            Float float2 = (Float)pair.getSecond();
            QuaternionF quaternion2 = new QuaternionF(0.0F, 0.0F, float_, float2);
            float f = float2 * float2 - float_ * float_;
            float g = -2.0F * float_ * float2;
            float h = float2 * float2 + float_ * float_;
            quaternion.mul(quaternion2);
            Mat3f2.setIdentity();
            Mat3f2.m00 = f;
            Mat3f2.m11 = f;
            Mat3f2.m10 = -g;
            Mat3f2.m01 = g;
            Mat3f2.m22 = h;
            Mat3f.mul(Mat3f2);
            Mat3f2.transpose();
            Mat3f2.mul(Mat3f);
            Mat3f.load(Mat3f2);
        }

        if (Mat3f.m02 * Mat3f.m02 + Mat3f.m20 * Mat3f.m20 > 1.0E-6F) {
            Pair<Float, Float> pair = approxGivensQuat(Mat3f.m00, 0.5F * (Mat3f.m02 + Mat3f.m20), Mat3f.m22);
            float i = -(Float)pair.getFirst();
            Float float2 = (Float)pair.getSecond();
            QuaternionF quaternion2 = new QuaternionF(0.0F, i, 0.0F, float2);
            float f = float2 * float2 - i * i;
            float g = -2.0F * i * float2;
            float h = float2 * float2 + i * i;
            quaternion.mul(quaternion2);
            Mat3f2.setIdentity();
            Mat3f2.m00 = f;
            Mat3f2.m22 = f;
            Mat3f2.m20 = g;
            Mat3f2.m02 = -g;
            Mat3f2.m11 = h;
            Mat3f.mul(Mat3f2);
            Mat3f2.transpose();
            Mat3f2.mul(Mat3f);
            Mat3f.load(Mat3f2);
        }

        if (Mat3f.m12 * Mat3f.m12 + Mat3f.m21 * Mat3f.m21 > 1.0E-6F) {
            Pair<Float, Float> pair = approxGivensQuat(Mat3f.m11, 0.5F * (Mat3f.m12 + Mat3f.m21), Mat3f.m22);
            Float float_ = (Float)pair.getFirst();
            Float float2 = (Float)pair.getSecond();
            QuaternionF quaternion2 = new QuaternionF(float_, 0.0F, 0.0F, float2);
            float f = float2 * float2 - float_ * float_;
            float g = -2.0F * float_ * float2;
            float h = float2 * float2 + float_ * float_;
            quaternion.mul(quaternion2);
            Mat3f2.setIdentity();
            Mat3f2.m11 = f;
            Mat3f2.m22 = f;
            Mat3f2.m21 = -g;
            Mat3f2.m12 = g;
            Mat3f2.m00 = h;
            Mat3f.mul(Mat3f2);
            Mat3f2.transpose();
            Mat3f2.mul(Mat3f);
            Mat3f.load(Mat3f2);
        }

        return quaternion;
    }

    private static void sortSingularValues(Mat3f Mat3f, QuaternionF quaternion) {
        float f = Mat3f.m00 * Mat3f.m00 + Mat3f.m10 * Mat3f.m10 + Mat3f.m20 * Mat3f.m20;
        float g = Mat3f.m01 * Mat3f.m01 + Mat3f.m11 * Mat3f.m11 + Mat3f.m21 * Mat3f.m21;
        float h = Mat3f.m02 * Mat3f.m02 + Mat3f.m12 * Mat3f.m12 + Mat3f.m22 * Mat3f.m22;
        if (f < g) {
            float i = Mat3f.m10;
            Mat3f.m10 = -Mat3f.m00;
            Mat3f.m00 = i;
            i = Mat3f.m11;
            Mat3f.m11 = -Mat3f.m01;
            Mat3f.m01 = i;
            i = Mat3f.m12;
            Mat3f.m12 = -Mat3f.m02;
            Mat3f.m02 = i;
            QuaternionF quaternion2 = new QuaternionF(0.0F, 0.0F, SQ2, SQ2);
            quaternion.mul(quaternion2);
            i = f;
            f = g;
            g = i;
        }

        if (f < h) {
            float i = Mat3f.m20;
            Mat3f.m20 = -Mat3f.m00;
            Mat3f.m00 = i;
            i = Mat3f.m21;
            Mat3f.m21 = -Mat3f.m01;
            Mat3f.m01 = i;
            i = Mat3f.m22;
            Mat3f.m22 = -Mat3f.m02;
            Mat3f.m02 = i;
            QuaternionF quaternion2 = new QuaternionF(0.0F, SQ2, 0.0F, SQ2);
            quaternion.mul(quaternion2);
            h = f;
        }

        if (g < h) {
            float i = Mat3f.m20;
            Mat3f.m20 = -Mat3f.m10;
            Mat3f.m10 = i;
            i = Mat3f.m21;
            Mat3f.m21 = -Mat3f.m11;
            Mat3f.m11 = i;
            i = Mat3f.m22;
            Mat3f.m22 = -Mat3f.m12;
            Mat3f.m12 = i;
            QuaternionF quaternion2 = new QuaternionF(SQ2, 0.0F, 0.0F, SQ2);
            quaternion.mul(quaternion2);
        }

    }

    public void transpose() {
        float f = this.m01;
        this.m01 = this.m10;
        this.m10 = f;
        f = this.m02;
        this.m02 = this.m20;
        this.m20 = f;
        f = this.m12;
        this.m12 = this.m21;
        this.m21 = f;
    }

    public Triple<QuaternionF, Vec3f, QuaternionF> svdDecompose() {
        QuaternionF quaternion = QuaternionF.ONE.copy();
        QuaternionF quaternion2 = QuaternionF.ONE.copy();
        Mat3f Mat3f = this.copy();
        Mat3f.transpose();
        Mat3f.mul(this);

        for(int i = 0; i < 5; ++i) {
            quaternion2.mul(stepJacobi(Mat3f));
        }

        quaternion2.normalize();
        Mat3f Mat3f2 = new Mat3f(this);
        Mat3f2.mul(new Mat3f(quaternion2));
        float f = 1.0F;
        Pair<Float, Float> pair = qrGivensQuat(Mat3f2.m00, Mat3f2.m10);
        Float float_ = (Float)pair.getFirst();
        Float float2 = (Float)pair.getSecond();
        float g = float2 * float2 - float_ * float_;
        float h = -2.0F * float_ * float2;
        float j = float2 * float2 + float_ * float_;
        QuaternionF quaternion3 = new QuaternionF(0.0F, 0.0F, float_, float2);
        quaternion.mul(quaternion3);
        Mat3f Mat3f3 = new Mat3f();
        Mat3f3.setIdentity();
        Mat3f3.m00 = g;
        Mat3f3.m11 = g;
        Mat3f3.m10 = h;
        Mat3f3.m01 = -h;
        Mat3f3.m22 = j;
        f *= j;
        Mat3f3.mul(Mat3f2);
        pair = qrGivensQuat(Mat3f3.m00, Mat3f3.m20);
        float k = -(Float)pair.getFirst();
        Float float3 = (Float)pair.getSecond();
        float l = float3 * float3 - k * k;
        float m = -2.0F * k * float3;
        float n = float3 * float3 + k * k;
        QuaternionF quaternion4 = new QuaternionF(0.0F, k, 0.0F, float3);
        quaternion.mul(quaternion4);
        Mat3f Mat3f4 = new Mat3f();
        Mat3f4.setIdentity();
        Mat3f4.m00 = l;
        Mat3f4.m22 = l;
        Mat3f4.m20 = -m;
        Mat3f4.m02 = m;
        Mat3f4.m11 = n;
        f *= n;
        Mat3f4.mul(Mat3f3);
        pair = qrGivensQuat(Mat3f4.m11, Mat3f4.m21);
        Float float4 = (Float)pair.getFirst();
        Float float5 = (Float)pair.getSecond();
        float o = float5 * float5 - float4 * float4;
        float p = -2.0F * float4 * float5;
        float q = float5 * float5 + float4 * float4;
        QuaternionF quaternion5 = new QuaternionF(float4, 0.0F, 0.0F, float5);
        quaternion.mul(quaternion5);
        Mat3f Mat3f5 = new Mat3f();
        Mat3f5.setIdentity();
        Mat3f5.m11 = o;
        Mat3f5.m22 = o;
        Mat3f5.m21 = p;
        Mat3f5.m12 = -p;
        Mat3f5.m00 = q;
        f *= q;
        Mat3f5.mul(Mat3f4);
        f = 1.0F / f;
        quaternion.mul((float)Math.sqrt((double)f));
        Vec3f vector3f = new Vec3f(Mat3f5.m00 * f, Mat3f5.m11 * f, Mat3f5.m22 * f);
        return Triple.of(quaternion, vector3f, quaternion2);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            Mat3f Mat3f = (Mat3f)object;
            return Float.compare(Mat3f.m00, this.m00) == 0 && Float.compare(Mat3f.m01, this.m01) == 0 && Float.compare(Mat3f.m02, this.m02) == 0 && Float.compare(Mat3f.m10, this.m10) == 0 && Float.compare(Mat3f.m11, this.m11) == 0 && Float.compare(Mat3f.m12, this.m12) == 0 && Float.compare(Mat3f.m20, this.m20) == 0 && Float.compare(Mat3f.m21, this.m21) == 0 && Float.compare(Mat3f.m22, this.m22) == 0;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = this.m00 != 0.0F ? Float.floatToIntBits(this.m00) : 0;
        i = 31 * i + (this.m01 != 0.0F ? Float.floatToIntBits(this.m01) : 0);
        i = 31 * i + (this.m02 != 0.0F ? Float.floatToIntBits(this.m02) : 0);
        i = 31 * i + (this.m10 != 0.0F ? Float.floatToIntBits(this.m10) : 0);
        i = 31 * i + (this.m11 != 0.0F ? Float.floatToIntBits(this.m11) : 0);
        i = 31 * i + (this.m12 != 0.0F ? Float.floatToIntBits(this.m12) : 0);
        i = 31 * i + (this.m20 != 0.0F ? Float.floatToIntBits(this.m20) : 0);
        i = 31 * i + (this.m21 != 0.0F ? Float.floatToIntBits(this.m21) : 0);
        i = 31 * i + (this.m22 != 0.0F ? Float.floatToIntBits(this.m22) : 0);
        return i;
    }

    private static int bufferIndex(int i, int j) {
        return j * 3 + i;
    }

    public void load(FloatBuffer floatBuffer) {
        this.m00 = floatBuffer.get(bufferIndex(0, 0));
        this.m01 = floatBuffer.get(bufferIndex(0, 1));
        this.m02 = floatBuffer.get(bufferIndex(0, 2));
        this.m10 = floatBuffer.get(bufferIndex(1, 0));
        this.m11 = floatBuffer.get(bufferIndex(1, 1));
        this.m12 = floatBuffer.get(bufferIndex(1, 2));
        this.m20 = floatBuffer.get(bufferIndex(2, 0));
        this.m21 = floatBuffer.get(bufferIndex(2, 1));
        this.m22 = floatBuffer.get(bufferIndex(2, 2));
    }

    public void loadTransposed(FloatBuffer floatBuffer) {
        this.m00 = floatBuffer.get(bufferIndex(0, 0));
        this.m01 = floatBuffer.get(bufferIndex(1, 0));
        this.m02 = floatBuffer.get(bufferIndex(2, 0));
        this.m10 = floatBuffer.get(bufferIndex(0, 1));
        this.m11 = floatBuffer.get(bufferIndex(1, 1));
        this.m12 = floatBuffer.get(bufferIndex(2, 1));
        this.m20 = floatBuffer.get(bufferIndex(0, 2));
        this.m21 = floatBuffer.get(bufferIndex(1, 2));
        this.m22 = floatBuffer.get(bufferIndex(2, 2));
    }

    public void load(FloatBuffer floatBuffer, boolean bl) {
        if (bl) {
            this.loadTransposed(floatBuffer);
        } else {
            this.load(floatBuffer);
        }

    }

    public void load(Mat3f Mat3f) {
        this.m00 = Mat3f.m00;
        this.m01 = Mat3f.m01;
        this.m02 = Mat3f.m02;
        this.m10 = Mat3f.m10;
        this.m11 = Mat3f.m11;
        this.m12 = Mat3f.m12;
        this.m20 = Mat3f.m20;
        this.m21 = Mat3f.m21;
        this.m22 = Mat3f.m22;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Mat3f:\n");
        stringBuilder.append(this.m00);
        stringBuilder.append(" ");
        stringBuilder.append(this.m01);
        stringBuilder.append(" ");
        stringBuilder.append(this.m02);
        stringBuilder.append("\n");
        stringBuilder.append(this.m10);
        stringBuilder.append(" ");
        stringBuilder.append(this.m11);
        stringBuilder.append(" ");
        stringBuilder.append(this.m12);
        stringBuilder.append("\n");
        stringBuilder.append(this.m20);
        stringBuilder.append(" ");
        stringBuilder.append(this.m21);
        stringBuilder.append(" ");
        stringBuilder.append(this.m22);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public void store(FloatBuffer floatBuffer) {
        floatBuffer.put(bufferIndex(0, 0), this.m00);
        floatBuffer.put(bufferIndex(0, 1), this.m01);
        floatBuffer.put(bufferIndex(0, 2), this.m02);
        floatBuffer.put(bufferIndex(1, 0), this.m10);
        floatBuffer.put(bufferIndex(1, 1), this.m11);
        floatBuffer.put(bufferIndex(1, 2), this.m12);
        floatBuffer.put(bufferIndex(2, 0), this.m20);
        floatBuffer.put(bufferIndex(2, 1), this.m21);
        floatBuffer.put(bufferIndex(2, 2), this.m22);
    }

    public void storeTransposed(FloatBuffer floatBuffer) {
        floatBuffer.put(bufferIndex(0, 0), this.m00);
        floatBuffer.put(bufferIndex(1, 0), this.m01);
        floatBuffer.put(bufferIndex(2, 0), this.m02);
        floatBuffer.put(bufferIndex(0, 1), this.m10);
        floatBuffer.put(bufferIndex(1, 1), this.m11);
        floatBuffer.put(bufferIndex(2, 1), this.m12);
        floatBuffer.put(bufferIndex(0, 2), this.m20);
        floatBuffer.put(bufferIndex(1, 2), this.m21);
        floatBuffer.put(bufferIndex(2, 2), this.m22);
    }

    public void store(FloatBuffer floatBuffer, boolean bl) {
        if (bl) {
            this.storeTransposed(floatBuffer);
        } else {
            this.store(floatBuffer);
        }

    }

    public void setIdentity() {
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m12 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
    }

    public float adjugateAndDet() {
        float f = this.m11 * this.m22 - this.m12 * this.m21;
        float g = -(this.m10 * this.m22 - this.m12 * this.m20);
        float h = this.m10 * this.m21 - this.m11 * this.m20;
        float i = -(this.m01 * this.m22 - this.m02 * this.m21);
        float j = this.m00 * this.m22 - this.m02 * this.m20;
        float k = -(this.m00 * this.m21 - this.m01 * this.m20);
        float l = this.m01 * this.m12 - this.m02 * this.m11;
        float m = -(this.m00 * this.m12 - this.m02 * this.m10);
        float n = this.m00 * this.m11 - this.m01 * this.m10;
        float o = this.m00 * f + this.m01 * g + this.m02 * h;
        this.m00 = f;
        this.m10 = g;
        this.m20 = h;
        this.m01 = i;
        this.m11 = j;
        this.m21 = k;
        this.m02 = l;
        this.m12 = m;
        this.m22 = n;
        return o;
    }

    public float determinant() {
        float f = this.m11 * this.m22 - this.m12 * this.m21;
        float g = -(this.m10 * this.m22 - this.m12 * this.m20);
        float h = this.m10 * this.m21 - this.m11 * this.m20;
        return this.m00 * f + this.m01 * g + this.m02 * h;
    }

    public boolean invert() {
        float f = this.adjugateAndDet();
        if (Math.abs(f) > 1.0E-6F) {
            this.mul(f);
            return true;
        } else {
            return false;
        }
    }

    public void set(int i, int j, float f) {
        if (i == 0) {
            if (j == 0) {
                this.m00 = f;
            } else if (j == 1) {
                this.m01 = f;
            } else {
                this.m02 = f;
            }
        } else if (i == 1) {
            if (j == 0) {
                this.m10 = f;
            } else if (j == 1) {
                this.m11 = f;
            } else {
                this.m12 = f;
            }
        } else if (j == 0) {
            this.m20 = f;
        } else if (j == 1) {
            this.m21 = f;
        } else {
            this.m22 = f;
        }

    }

    public void mul(Mat3f Mat3f) {
        float f = this.m00 * Mat3f.m00 + this.m01 * Mat3f.m10 + this.m02 * Mat3f.m20;
        float g = this.m00 * Mat3f.m01 + this.m01 * Mat3f.m11 + this.m02 * Mat3f.m21;
        float h = this.m00 * Mat3f.m02 + this.m01 * Mat3f.m12 + this.m02 * Mat3f.m22;
        float i = this.m10 * Mat3f.m00 + this.m11 * Mat3f.m10 + this.m12 * Mat3f.m20;
        float j = this.m10 * Mat3f.m01 + this.m11 * Mat3f.m11 + this.m12 * Mat3f.m21;
        float k = this.m10 * Mat3f.m02 + this.m11 * Mat3f.m12 + this.m12 * Mat3f.m22;
        float l = this.m20 * Mat3f.m00 + this.m21 * Mat3f.m10 + this.m22 * Mat3f.m20;
        float m = this.m20 * Mat3f.m01 + this.m21 * Mat3f.m11 + this.m22 * Mat3f.m21;
        float n = this.m20 * Mat3f.m02 + this.m21 * Mat3f.m12 + this.m22 * Mat3f.m22;
        this.m00 = f;
        this.m01 = g;
        this.m02 = h;
        this.m10 = i;
        this.m11 = j;
        this.m12 = k;
        this.m20 = l;
        this.m21 = m;
        this.m22 = n;
    }

    public void mul(QuaternionF quaternion) {
        this.mul(new Mat3f(quaternion));
    }

    public void mul(float f) {
        this.m00 *= f;
        this.m01 *= f;
        this.m02 *= f;
        this.m10 *= f;
        this.m11 *= f;
        this.m12 *= f;
        this.m20 *= f;
        this.m21 *= f;
        this.m22 *= f;
    }

    public void add(Mat3f Mat3f) {
        this.m00 += Mat3f.m00;
        this.m01 += Mat3f.m01;
        this.m02 += Mat3f.m02;
        this.m10 += Mat3f.m10;
        this.m11 += Mat3f.m11;
        this.m12 += Mat3f.m12;
        this.m20 += Mat3f.m20;
        this.m21 += Mat3f.m21;
        this.m22 += Mat3f.m22;
    }

    public void sub(Mat3f Mat3f) {
        this.m00 -= Mat3f.m00;
        this.m01 -= Mat3f.m01;
        this.m02 -= Mat3f.m02;
        this.m10 -= Mat3f.m10;
        this.m11 -= Mat3f.m11;
        this.m12 -= Mat3f.m12;
        this.m20 -= Mat3f.m20;
        this.m21 -= Mat3f.m21;
        this.m22 -= Mat3f.m22;
    }

    public float trace() {
        return this.m00 + this.m11 + this.m22;
    }

    public Mat3f copy() {
        return new Mat3f(this);
    }

    public Matrix3f convert() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        buffer.put(bufferIndex(0, 0), m00);
        buffer.put(bufferIndex(0, 1), m01);
        buffer.put(bufferIndex(0, 2), m02);
        buffer.put(bufferIndex(1, 0), m10);
        buffer.put(bufferIndex(1, 1), m11);
        buffer.put(bufferIndex(1, 2), m12);
        buffer.put(bufferIndex(2, 0), m20);
        buffer.put(bufferIndex(2, 1), m21);
        buffer.put(bufferIndex(2, 2), m22);
        return new Matrix3f(buffer);
    }
}

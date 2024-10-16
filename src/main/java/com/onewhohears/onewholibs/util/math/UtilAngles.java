package com.onewhohears.onewholibs.util.math;

import java.util.Random;




import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * @author 1whohears
 */
public class UtilAngles {
	
	public static double getHorizontalDistanceSqr(Vec3 vec3) {
        return Math.sqrt((vec3.x * vec3.x) + (vec3.z * vec3.z));
    }

    public static double normalizedDotProduct(Vec3 v1, Vec3 v2) {
        return v1.dot(v2) / (v1.length() * v2.length());
    }

    public static float getPitch(Vec3 motion) {
        double y = -motion.y;
        return (float) Math.toDegrees(Math.atan2(y, Math.sqrt(motion.x * motion.x + motion.z * motion.z)));
    }

    public static float getYaw(Vec3 motion) {
        return (float) Math.toDegrees(Math.atan2(-motion.x, motion.z));
    }

    public static float lerpAngle(float perc, float start, float end) {
        return start + perc * Mth.wrapDegrees(end - start);
    }

    public static float lerpAngle180(float perc, float start, float end) {
        if (degreesDifferenceAbs(start, end) > 90)
            end += 180;
        return start + perc * Mth.wrapDegrees(end - start);
    }

    public static double lerpAngle180(double perc, double start, double end) {
        if (degreesDifferenceAbs(start, end) > 90)
            end += 180;
        return start + perc * Mth.wrapDegrees(end - start);
    }

    public static double lerpAngle(double perc, double start, double end) {
        return start + perc * Mth.wrapDegrees(end - start);
    }

    public static double degreesDifferenceAbs(double p_203301_0_, double p_203301_1_) {
        return Math.abs(wrapSubtractDegrees(p_203301_0_, p_203301_1_));
    }

    public static double wrapSubtractDegrees(double p_203302_0_, double p_203302_1_) {
        return Mth.wrapDegrees(p_203302_1_ - p_203302_0_);
    }
    
    public static float rotLerp(float currentAngle, float targetAngle, float stepSize) {
    	float f = Mth.wrapDegrees(targetAngle - currentAngle);
    	if (f > stepSize) f = stepSize;
    	if (f < -stepSize) f = -stepSize;
        return currentAngle + f;
     }

    public static Vec3 rotationToVector(double yaw, double pitch) {
        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        double xzLen = Math.cos(pitch);
        double x = -xzLen * Math.sin(yaw);
        double y = Math.sin(-pitch);
        double z = xzLen * Math.cos(-yaw);
        return new Vec3(x, y, z);
    }

    public static Vec3 rotationToVector(double yaw, double pitch, double size) {
        Vec3 vec = rotationToVector(yaw, pitch);
        return vec.scale(size / vec.length());
    }
    
    public static Vec3 inaccurateDirection(Vec3 dir, float inaccuracy) {
    	float yaw = getYaw(dir);
    	float pitch = getPitch(dir);
    	Random r = new Random();
    	yaw = yaw + (r.nextFloat()-0.5f) * 2f * inaccuracy;
		pitch = pitch + (r.nextFloat()-0.5f) * 2f * inaccuracy;
		return rotationToVector(yaw, pitch);
    }
    
    public static void normalizeRCloseToONE(Quaternionf q, float d) {
    	q.normalize();
    	if (Mth.abs(Mth.abs(q.w)-1) < d) q.set(0, 0, 0, 1);
    }

    public static Quaternionf qDiff(Quaternionf q1, Quaternionf q2) {
        if (q1.equals(q2)) return new Quaternionf();  // Identity quaternion

        Quaternionf iq2 = new Quaternionf(q2).conjugate();  // Copy and conjugate q2
        Quaternionf d = new Quaternionf(q1).mul(iq2);  // Copy q1 and multiply by iq2
        d.normalize();  // Normalize the result

        return d;
    }

    public static EulerAngles toRadians(Quaternionf q) {
        EulerAngles angles = new EulerAngles();

        double xx = q.x() * q.x();
        double yy = q.y() * q.y();
        double zz = q.z() * q.z();
        double ww = q.w() * q.w();

        // Roll (x-axis rotation)
        double sinr_cosp = 2 * (q.w() * q.x() + q.y() * q.z());
        double cosr_cosp = ww - xx - yy + zz;
        angles.roll = Math.atan2(sinr_cosp, cosr_cosp);

        // Pitch (y-axis rotation)
        double sinp = 2 * (q.w() * q.y() - q.z() * q.x());
        final double threshold = 0.999;  // Tolerance for gimbal lock scenario
        if (Math.abs(sinp) >= threshold) {
            angles.pitch = Math.copySign(Math.PI / 2, sinp);  // Use 90 degrees for gimbal lock
        } else {
            angles.pitch = Math.asin(sinp);
        }

        // Yaw (z-axis rotation)
        double siny_cosp = 2 * (q.w() * q.z() + q.x() * q.y());
        double cosy_cosp = ww + xx - yy - zz;
        angles.yaw = Math.atan2(siny_cosp, cosy_cosp);  // Adjust this based on the coordinate system

        return angles;
    }


    public static EulerAngles toDegrees(Quaternionf q) {
    	EulerAngles angles = toRadians(q);
    	angles.roll = Math.toDegrees(angles.roll);
    	angles.pitch = Math.toDegrees(angles.pitch);
    	angles.yaw = Math.toDegrees(angles.yaw);
    	return angles;
    }

    public static float fastInvSqrt(float number) {
        float f = 0.5F * number;
        int i = Float.floatToIntBits(number);
        i = 1597463007 - (i >> 1);
        number = Float.intBitsToFloat(i);
        return number * (1.5F - f * number * number);
    }

    public static Quaternionf normalizeQuaternion(Quaternionf q) {
        float f = q.x() * q.x() + q.y() * q.y() + q.z() * q.z() + q.w() * q.w();
        float x = q.x();
        float y = q.y();
        float z = q.z();
        float w = q.w();
        if (f > 1.0E-6F) {
            float f1 = fastInvSqrt(f);
            x *= f1;
            y *= f1;
            z *= f1;
            w *= f1;
            return new Quaternionf(x, y, z, w);
        } else {
            return new Quaternionf(0, 0, 0, 0);
        }
    }
    
    /**
     * @param yaw degrees
     * @param pitch degrees
     * @param roll degrees
     * @return
     */
    public static Quaternionf toQuaternion(double yaw, double pitch, double roll) {
        yaw = -Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        roll = Math.toRadians(roll);

        double cy = Math.cos(yaw * 0.5);
        double sy = Math.sin(yaw * 0.5);
        double cp = Math.cos(pitch * 0.5);
        double sp = Math.sin(pitch * 0.5);
        double cr = Math.cos(roll * 0.5);
        double sr = Math.sin(roll * 0.5);

        float w = (float) (cr * cp * cy + sr * sp * sy);
        float z = (float) (sr * cp * cy - cr * sp * sy);
        float x = (float) (cr * sp * cy + sr * cp * sy);
        float y = (float) (cr * cp * sy - sr * sp * cy);
        
        return new Quaternionf(x, y, z, w);
    }
    
    public static Quaternionf lerpQ(float perc, Quaternionf start, Quaternionf end) {
    	// HOW 6 normalizing causes compounding precision errors at yaw 90 and 270. everything seems to work without normalizing though?
    	return lerpQ(perc, start, end, false);
    }

    public static Quaternionf lerpQ(float perc, Quaternionf start, Quaternionf end, boolean normalize) {
        // Always normalize start and end quaternions
        start = normalizeQuaternion(start);
        end = normalizeQuaternion(end);

        // Compute the cosine of the angle between the two vectors.
        double dot = start.x() * end.x() + start.y() * end.y() + start.z() * end.z() + start.w() * end.w();

        // If the dot product is negative, reverse one quaternion to take the shorter path
        if (dot < 0.0f) {
            end = new Quaternionf(-end.x(), -end.y(), -end.z(), -end.w());
            dot = -dot;
        }

        // Adjust the threshold to avoid instability
        double DOT_THRESHOLD = 0.9995;
        if (dot > DOT_THRESHOLD) {
            // Linearly interpolate and normalize
            Quaternionf quaternion = new Quaternionf(
                    start.x() * (1 - perc) + end.x() * perc,
                    start.y() * (1 - perc) + end.y() * perc,
                    start.z() * (1 - perc) + end.z() * perc,
                    start.w() * (1 - perc) + end.w() * perc
            );
            return normalizeQuaternion(quaternion); // Always normalize the result to avoid issues
        }

        // Spherical linear interpolation (slerp)
        double theta_0 = Math.acos(dot);        // Angle between input vectors
        double theta = theta_0 * perc;          // Scaled angle for interpolation
        double sin_theta = Math.sin(theta);     // Calculate sin of angle
        double sin_theta_0 = Math.sin(theta_0); // Calculate sin of base angle

        float s0 = (float) (Math.cos(theta) - dot * sin_theta / sin_theta_0);  // Contribution from start
        float s1 = (float) (sin_theta / sin_theta_0);                          // Contribution from end

        Quaternionf quaternion = new Quaternionf(
                start.x() * s0 + end.x() * s1,
                start.y() * s0 + end.y() * s1,
                start.z() * s0 + end.z() * s1,
                start.w() * s0 + end.w() * s1
        );

        return normalizeQuaternion(quaternion);  // Always normalize to avoid precision errors
    }


    public static class EulerAngles {
        public double pitch, yaw, roll;

        public EulerAngles() {}

        private EulerAngles(EulerAngles a) {
            this.pitch = a.pitch;
            this.yaw = a.yaw;
            this.roll = a.roll;
        }

        public EulerAngles copy() {
            return new EulerAngles(this);
        }

        @Override
        public String toString() {
            return "EulerAngles{" +
                "pitch=" + pitch +
                ", yaw=" + yaw +
                ", roll=" + roll +
                '}';
        }
    }
    
    public static Vec3 getRollAxis(Quaternionf q) {
    	EulerAngles a = toRadians(q);
    	return getRollAxis(a.pitch, a.yaw);
    }
    
    public static Vec3 getRollAxis(double pitchRad, double yawRad) {
		return new Vec3(-Math.sin(yawRad)*Math.cos(pitchRad), 
							Math.sin(-pitchRad), 
							Math.cos(yawRad)*Math.cos(pitchRad));
	}
    
    public static Vec3 getPitchAxis(Quaternionf q) {
    	EulerAngles a = toRadians(q);
    	return getPitchAxis(a.pitch, a.yaw, a.roll);
    }
    
    public static Vec3 getPitchAxis(double pitchRad, double yawRad, double rollRad) {
		double CP = Math.cos(-pitchRad);
		double SP = Math.sin(-pitchRad);
		double CY = Math.cos(yawRad);
		double SY = Math.sin(yawRad);
		double CR = Math.cos(rollRad);
		double SR = Math.sin(rollRad);
		return new Vec3(CY*CR+SY*SP*SR,
						CP*SR,
						-(CY*SP*SR-CR*SY));
	}
    
    public static Vec3 getYawAxis(Quaternionf q) {
    	EulerAngles a = toRadians(q);
    	return getYawAxis(a.pitch, a.yaw, a.roll);
    }
    
    public static Vec3 getYawAxis(double pitchRad, double yawRad, double rollRad) {
    	double CP = Math.cos(-pitchRad);
		double SP = Math.sin(-pitchRad);
		double CY = Math.cos(yawRad);
		double SY = Math.sin(yawRad);
		double CR = Math.cos(rollRad);
		double SR = Math.sin(rollRad);
		return new Vec3(CR*SY*SP-CY*SR,
						CP*CR,
						-(SY*SR+CY*SP*CR));
	}

    public static Vector3f rotateVector(Vector3f n, Quaternionf q) {
        float p = 1000f, pi = 0.001f;
        Quaternionf nq = new Quaternionf(n.x() * p, n.y() * p, n.z() * p, 0f);
        Quaternionf cq = new Quaternionf(q).conjugate();
        Quaternionf q1 = new Quaternionf(q).mul(nq).mul(cq);
        return new Vector3f(q1.x() * pi, q1.y() * pi, q1.z() * pi);
    }


    public static Vector3f rotateVectorInverse(Vector3f n, Quaternionf q) {
        Quaternionf q1 = new Quaternionf(q).conjugate();
        return rotateVector(n, q1);
    }


    public static Vec3 rotateVector(Vec3 n, Quaternionf q) {
        Quaternionf nq = new Quaternionf((float) n.x, (float) n.y, (float) n.z, 0);
        Quaternionf cq = new Quaternionf(q).conjugate();
        Quaternionf q1 = new Quaternionf(q).mul(nq).mul(cq);
        return new Vec3(q1.x(), q1.y(), q1.z());
    }


    public static Vec3 rotateVectorInverse(Vec3 n, Quaternionf q) {
        Quaternionf q1 = new Quaternionf(q).conjugate();
        return rotateVector(n, q1);
    }

    
    public static float[] globalToRelativeDegrees(float gx, float gy, Quaternionf ra) {
    	Vec3 dir = rotationToVector(gy, gx);
    	EulerAngles ea = toRadians(ra);
    	Vec3 yaxis = getYawAxis(ea.pitch, ea.yaw, ea.roll).scale(-1);
    	Vec3 zaxis = getRollAxis(ea.pitch, ea.yaw);
    	Vec3 xaxis = getPitchAxis(ea.pitch, ea.yaw, ea.roll).scale(-1);
    	float rx = (float) UtilGeometry.angleBetweenVecPlaneDegrees(dir, yaxis);
    	double xc = UtilGeometry.vecCompMagDirByNormAxis(dir, xaxis);
    	double zc = UtilGeometry.vecCompMagDirByNormAxis(dir, zaxis);
    	float ry = (float) Math.toDegrees(Math.atan2(xc, zc));
    	return new float[] {rx, ry};
    }

    public static float[] relativeToGlobalDegrees(float rx, float ry, Quaternionf ra) {
        // Create a new quaternion from ra
        Quaternionf r = new Quaternionf(ra);

        r.mul(VectorUtils.rotationQuaternion(VectorUtils.POSITIVE_Z, ry)); // Rotate around Z-axis (negative Y)
        r.mul(VectorUtils.rotationQuaternion(VectorUtils.POSITIVE_X, rx)); // Rotate around X-axis (positive X)

        EulerAngles ea = toDegrees(r);
        return new float[] {(float)ea.pitch, (float)ea.yaw};
    }

    
    public static Matrix4f pivotRot(Vector3f pivot, Quaternionf rot) {
    	return pivotRot(pivot.x(), pivot.y(), pivot.z(), rot);
    }
    
    public static Matrix4f pivotInvRot(Vector3f pivot, Quaternionf rot) {
    	return pivotRot(-pivot.x(), -pivot.y(), -pivot.z(), rot);
    }

    public static Matrix4f pivotRot(float x, float y, float z, Quaternionf rot) {
        Matrix4f translateToPivot = new Matrix4f().translate(x, y, z);
        Matrix4f rotationMatrix = new Matrix4f().rotation(rot);
        Matrix4f translateBack = new Matrix4f().translate(-x, -y, -z);
        return translateBack.mul(rotationMatrix).mul(translateToPivot);
    }


    public static Matrix4f pivotRotX(float x, float y, float z, float degrees) {
        return pivotRot(x, y, z, VectorUtils.rotationQuaternion(VectorUtils.POSITIVE_X, degrees));
    }

    public static Matrix4f pivotRotY(float x, float y, float z, float degrees) {
        return pivotRot(x, y, z, VectorUtils.rotationQuaternion(VectorUtils.POSITIVE_Z, degrees)); // Change to POSITIVE_Y for Y rotation
    }

    public static Matrix4f pivotRotZ(float x, float y, float z, float degrees) {
        return pivotRot(x, y, z, VectorUtils.rotationQuaternion(VectorUtils.POSITIVE_Z, degrees));
    }

    public static Matrix4f pivotPixelsRot(float x, float y, float z, Quaternionf rot) {
        return pivotRot(x * 0.0625f, y * 0.0625f, z * 0.0625f, rot);
    }

    public static Matrix4f pivotPixelsRotX(float x, float y, float z, float degrees) {
        return pivotPixelsRot(x, y, z, VectorUtils.rotationQuaternion(VectorUtils.POSITIVE_X, degrees));
    }

    public static Matrix4f pivotPixelsRotY(float x, float y, float z, float degrees) {
        return pivotPixelsRot(x, y, z, VectorUtils.rotationQuaternion(VectorUtils.POSITIVE_Y, degrees)); // Change to POSITIVE_Y for Y rotation
    }

    public static Matrix4f pivotPixelsRotZ(float x, float y, float z, float degrees) {
        return pivotPixelsRot(x, y, z, VectorUtils.rotationQuaternion(VectorUtils.POSITIVE_Z, degrees));
    }

}

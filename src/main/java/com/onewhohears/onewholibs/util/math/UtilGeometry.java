package com.onewhohears.onewholibs.util.math;

import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * @author 1whohears
 */
public class UtilGeometry {
	
	public static boolean isPointInsideCone(Vec3 point, Vec3 origin, Vec3 direction, double maxAngle, double maxDistance) {
		Vec3 diff = point.subtract(origin);
		double dist = diff.length();
		if (dist > maxDistance) {
			//System.out.println(dist+" > max dist "+maxDistance);
			return false;
		}
		double dot = diff.dot(direction);
		double mag = dist * direction.length();
		double angle = Math.acos(dot / mag);
		angle = Math.toDegrees(angle);
		if (angle > maxAngle) {
			//System.out.println(angle+" > max angle "+maxAngle);
			return false;
		}
		return true;
	}
	
	public static double angleBetween(Vec3 dir, Vec3 base) {
		double dot = dir.dot(base);
		double mag = Math.sqrt(dir.lengthSqr() * base.lengthSqr());
		return Math.acos(dot / mag);
	}
	
	public static double angleBetweenDegrees(Vec3 dir, Vec3 base) {
		return Math.toDegrees(angleBetween(dir, base));
	}
	
	public static double angleBetweenVecPlane(Vec3 dir, Vec3 planeNormal) {
		double a = angleBetween(dir, planeNormal);
		return Math.PI/2 - a;
	}
	
	public static double angleBetweenVecPlaneDegrees(Vec3 dir, Vec3 planeNormal) {
		return Math.toDegrees(angleBetweenVecPlane(dir, planeNormal));
	}
	
	public static Vec3 interceptPos(Vec3 mPos, Vec3 mVel, Vec3 tPos, Vec3 tVel) {
		double x = interceptComponent(mPos.x, tPos.x, mVel.x, tVel.x);
		double y = interceptComponent(mPos.y, tPos.y, mVel.y, tVel.y);
		double z = interceptComponent(mPos.z, tPos.z, mVel.z, tVel.z);
		return new Vec3(x, y, z);
	}
	
	private static double interceptComponent(double mPos, double tPos, double mVel, double tVel) {
		double dp = tPos - mPos;
		double dv = tVel - mVel;
		if ((dp > 0 && dv < 0) || (dp < 0 && dv > 0))
			tPos += tVel * dp / -dv;
		return tPos;
	}
	
	public static Vec3 vecCompByAxis(Vec3 u, Vec3 v) {
		if (isZero(v)) return Vec3.ZERO;
		return v.scale(u.dot(v) / v.lengthSqr());
	}
	
	public static Vec3 vecCompByNormAxis(Vec3 u, Vec3 n) {
		return n.scale(u.dot(n));
	}
	
	public static double vecCompMagSqrDirByAxis(Vec3 u, Vec3 v) {
		if (isZero(v)) return 0;
		double dot = u.dot(v);
		double vl2 = v.lengthSqr();
		Vec3 vec = v.scale(dot / vl2);
		return vec.lengthSqr() * Math.signum(dot);
	}
	
	public static double vecCompMagSqrDirByNormAxis(Vec3 u, Vec3 n) {
		double dot = u.dot(n);
		Vec3 vec = n.scale(dot);
		return vec.lengthSqr() * Math.signum(dot);
	}
	
	public static double vecCompMagDirByAxis(Vec3 u, Vec3 v) {
		if (isZero(v)) return 0;
		double dot = u.dot(v);
		double vl2 = v.lengthSqr();
		Vec3 vec = v.scale(dot / vl2);
		return vec.length() * Math.signum(dot);
	}
	
	public static double vecCompMagDirByNormAxis(Vec3 u, Vec3 n) {
		double dot = u.dot(n);
		Vec3 vec = n.scale(dot);
		return vec.length() * Math.signum(dot);
	}
	
	public static boolean isZero(Vec3 v) {
		return v.x == 0 && v.y == 0 && v.z == 0;
	}
	
	public static boolean isZero(Vector3f v) {
		return v.x() == 0 && v.y() == 0 && v.z() == 0;
	}
	
	public static Vec3 getClosestPointOnAABB(Vec3 pos, AABB aabb) {
		if (pos == null) return aabb.getCenter();
		double rx = pos.x, ry = pos.y, rz = pos.z;
		if (rx >= aabb.maxX) rx = aabb.maxX;
		else if (rx <= aabb.minX) rx = aabb.minX;
		if (ry >= aabb.maxY) ry = aabb.maxY;
		else if (ry <= aabb.minY) ry = aabb.minY;
		if (rz >= aabb.maxZ) rz = aabb.maxZ;
		else if (rz <= aabb.minZ) rz = aabb.minZ;
		return new Vec3(rx, ry, rz);
	}
	
	public static boolean vec3NAN(Vec3 v) {
		return Double.isNaN(v.x) || Double.isNaN(v.y) || Double.isNaN(v.z);
	}
	
	public static int[] worldToScreenPosInt(Vec3 world_pos, Matrix4f view_mat, Matrix4f proj_mat, int width, int height) {
		float[] sp = worldToScreenPos(world_pos, view_mat, proj_mat, width, height);
		return new int[] {(int)sp[0], (int)sp[1]};
	}
	
	public static float[] worldToScreenPos(Vec3 world_pos, Matrix4f view_mat, Matrix4f proj_mat, int width, int height) {
		Vector4f clipSpace = new Vector4f((float)world_pos.x, (float)world_pos.y, (float)world_pos.z, 1f);
		clipSpace.transform(view_mat);
		clipSpace.transform(proj_mat);
		if (clipSpace.w() <= 0) return new float[] {-1,-1};
		Vector3f ndcSpace = new Vector3f(clipSpace);
		ndcSpace.mul(1/clipSpace.w());
		float win_x = (ndcSpace.x()+1f)/2f*width;
		float win_y = (ndcSpace.y()+1f)/2f*height;
		return new float[] {win_x, height - win_y};
	}
	
	public static Vector3f convertVector(Vec3 v) {
		return new Vector3f((float)v.x, (float)v.y, (float)v.z);
	}
	
	public static Vec3 convertVector(Vector3f v) {
		return new Vec3(v.x(), v.y(), v.z());
	}
	
	public static Vec3 getBBFeet(AABB bb) {
		return new Vec3(bb.getCenter().x, bb.minY, bb.getCenter().z);
	}
	
	public static Vec3 toFloats(Vec3 v) {
		return new Vec3((float)v.x, (float)v.y, (float)v.z);
	}
	
	public static Vec3 toVec3(BlockPos pos) {
		return new Vec3(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static final Random RANDOM = new Random();
	
	public static Vec3 inaccurateTargetPos(Vec3 origin, Vec3 targetPos, float inaccuracy) {
		double dist = origin.distanceTo(targetPos);
		double i = dist*Math.tan(Mth.DEG_TO_RAD*inaccuracy);
		return targetPos.add((RANDOM.nextDouble()-0.5)*i, (RANDOM.nextDouble()-0.5)*i, (RANDOM.nextDouble()-0.5)*i);
	}
	
	public static boolean isEqual(Vec3 v1, Vec3 v2) {
		return v1.x == v2.x && v1.y == v2.y && v1.z == v2.z;
	}
	
	public static boolean isEqual(Vec3 a, Vec3 b, double maxDiff) {
		return Math.abs(a.x-b.x) <= maxDiff && Math.abs(a.y-b.y) <= maxDiff && Math.abs(a.z-b.z) <= maxDiff;
	}
	
	/**
	 * @return double array size 4 of roots. root 1 real, root 1 imaginary, root2 real, root2 imaginary
	 */
	public static double[] roots(double a, double b, double c) {
		double[] roots = new double[4];
		double d = b*b - 4*a*c;
		if (d > 0) {
			double sqrtD = Math.sqrt(d);
			roots[0] = (-b + sqrtD) / (2*a);
			roots[1] = 0;
			roots[2] = (-b - sqrtD) / (2*a);
			roots[3] = 0;
		} else if (d == 0) {
			double root = -b / (2*a);
			roots[0] = root;
			roots[1] = 0;
			roots[2] = root;
			roots[3] = 0;
		} else {
			double real = -b / (2*a);
			double imaginary = Math.sqrt(-d) / (2 * a);
			roots[0] = real;
			roots[1] = imaginary;
			roots[2] = real;
			roots[3] = -imaginary;
		}
		return roots;
	}
	
	/**
	 * @return double array size 2 of roots. null if imaginary.
	 */
	@Nullable
	public static double[] rootsNoI(double a, double b, double c) {
		double d = b*b - 4*a*c;
		if (d < 0) return null;
		double[] roots = new double[2];
		if (d == 0) {
			double root = -b / (2*a);
			roots[0] = root;
			roots[1] = root;
		} else {
			double sqrtD = Math.sqrt(d);
			roots[0] = (-b + sqrtD) / (2*a);
			roots[1] = (-b - sqrtD) / (2*a);
		}
		return roots;
	}
	
	public static int numTrue(boolean[] bools) {
		int num = 0;
		for (int i = 0; i < bools.length; ++i) 
			if (bools[i]) ++num;
		return num;
	}
	
	public static int getMaxYIndex(Vec3[] array) {
		if (array.length == 0) return -1;
		int maxIndex = 0;
		double max = array[0].y;
		for (int i = 1; i < array.length; ++i) {
			if (array[i].y > max) {
				maxIndex = i;
				max = array[i].y;
			}
		}
		return maxIndex;
	}
	
	public static int getMinYIndex(Vec3[] array) {
		if (array.length == 0) return -1;
		int minIndex = 0;
		double min = array[0].y;
		for (int i = 1; i < array.length; ++i) {
			if (array[i].y < min) {
				minIndex = i;
				min = array[i].y;
			}
		}
		return minIndex;
	}
	
	public static int getMaxXIndex(Vec3[] array) {
		if (array.length == 0) return -1;
		int maxIndex = 0;
		double max = array[0].x;
		for (int i = 1; i < array.length; ++i) {
			if (array[i].x > max) {
				maxIndex = i;
				max = array[i].x;
			}
		}
		return maxIndex;
	}
	
	public static int getMinXIndex(Vec3[] array) {
		if (array.length == 0) return -1;
		int minIndex = 0;
		double min = array[0].x;
		for (int i = 1; i < array.length; ++i) {
			if (array[i].x < min) {
				minIndex = i;
				min = array[i].x;
			}
		}
		return minIndex;
	}
	
	public static int getMaxZIndex(Vec3[] array) {
		if (array.length == 0) return -1;
		int maxIndex = 0;
		double max = array[0].z;
		for (int i = 1; i < array.length; ++i) {
			if (array[i].z > max) {
				maxIndex = i;
				max = array[i].z;
			}
		}
		return maxIndex;
	}
	
	public static int getMinZIndex(Vec3[] array) {
		if (array.length == 0) return -1;
		int minIndex = 0;
		double min = array[0].z;
		for (int i = 1; i < array.length; ++i) {
			if (array[i].z < min) {
				minIndex = i;
				min = array[i].z;
			}
		}
		return minIndex;
	}
	
	public static int getMinIndex(double[] array) {
		if (array.length == 0) return -1;
		int minIndex = 0;
		double min = array[0];
		for (int i = 1; i < array.length; ++i) {
			if (array[i] < min) {
				minIndex = i;
				min = array[i];
			}
		}
		return minIndex;
	}
	
	public static float crossProduct(Vec2 v1, Vec2 v2) {
		return v1.x * v2.y - v1.y * v2.x;
	}
	
	public static boolean isIn2DTriangle(Vec2 p, Vec2[] v) {
		if (v.length < 3) return false;
		float area = 0.5f * (-v[1].y*v[2].x + v[0].y*(-v[1].x+v[2].x) + v[0].x*(v[1].y-v[2].y) + v[1].x*v[2].y);
		float a = 1/(2*area);
		float s0 = a * (v[0].y*v[2].x - v[0].x*v[2].y + (v[2].y-v[0].y)*p.x + (v[0].x-v[2].x)*p.y);
		float t0 = a * (v[0].x*v[1].y - v[0].y*v[1].x + (v[0].y-v[1].y)*p.x + (v[1].x-v[0].x)*p.y);
		return s0 >= 0 && t0 >= 0 && 1-s0-t0 >= 0;
	}
	
	public static boolean isIn2DQuad(Vec2 p, Vec2[] v) {
		if (v.length < 4) return false;
		Vec2[] t1 = new Vec2[] {v[0], v[1], v[2]};
		Vec2[] t2 = new Vec2[] {v[0], v[2], v[3]};
		return isIn2DTriangle(p, t1) || isIn2DTriangle(p, t2); 
	}
	
	public static int add(int n, int a, int max) {
		n += a;
		double r = (double)n / (double)max;
		n -= (int)r * max;
		return n;
	}
	
	@Nullable
	public static Vec2 intersect(Vec2 as, Vec2 ae, Vec2 bs, Vec2 be) {
		Vec2 ad = ae.add(as.negated());
		Vec2 bd = be.add(bs.negated());
		float det = crossProduct(bd, ad);
		if (det == 0) return null;
		Vec2 D = bs.add(as.negated());
		float u = (D.y * bd.x - D.x * bd.y) / det;
		return as.add(ad.scale(u));
	}

	public static float[] calcCatmullromTs(float a, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
		if (a < 0) a = 0;
		else if (a > 1) a = 1;
		float t0 = 0;
		float t1 = calcT(x0, x1, y0, y1, t0, a);
		float t2 = calcT(x1, x2, y1, y2, t1, a);
		float t3 = calcT(x2, x3, y2, y3, t2, a);
		return new float[] {t0, t1, t2, t3};
	}

	public static float[] calcCatmullromTs(float a, Vec2 P0, Vec2 P1, Vec2 P2, Vec2 P3) {
		return calcCatmullromTs(a, P0.x, P0.y, P1.x, P1.y, P2.x, P2.y, P3.x, P3.y);
	}

	public static float findYInCatmullromArray(float x, Vec2[] cmr) {
		if (cmr.length == 0) return 0;
		if (x <= cmr[0].x) return cmr[0].y;
		for (int i = 1; i < cmr.length; ++i) {
			Vec2 now = cmr[i];
			if (x == now.x) return now.y;
			if (x > now.x) continue;
			Vec2 prev = cmr[i-1];
			float p = (x - prev.x) / (now.x / prev.x);
			return p * (now.y - prev.y) + prev.y;
		}
		return cmr[cmr.length-1].y;
	}

	public static Vec2[] catmullromArray(int points, float a, Vec2 P0, Vec2 P1, Vec2 P2, Vec2 P3) {
		if (a < 0) a = 0;
		else if (a > 1) a = 1;
		float[] ts = calcCatmullromTs(a, P0, P1, P2, P3);
		float t1 = ts[1], t2 = ts[2];
		float tDiff = t2 - t1;
		float tStep = tDiff / (float)points;
		Vec2[] array = new Vec2[points];
		for (int i = 0; i < array.length; ++i)
			array[i] = catmullrom(t1 + tStep*i, P0, P1, P2, P3, ts);
		return array;
	}

	public static Vec2[] catmullromArray(int points, float a, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
		return catmullromArray(points, a, new Vec2(x0, y0), new Vec2(x1, y1), new Vec2(x2, y2), new Vec2(x3, y3));
	}

	public static Vec2 catmullrom(float t, float a, Vec2 P0, Vec2 P1, Vec2 P2, Vec2 P3) {
		if (a < 0) a = 0;
		else if (a > 1) a = 1;
		float[] ts = calcCatmullromTs(a, P0, P1, P2, P3);
		return catmullrom(t, P0, P1, P2, P3, ts);
	}

	private static Vec2 catmullrom(float t, Vec2 P0, Vec2 P1, Vec2 P2, Vec2 P3, float[] ts) {
		float t0 = ts[0], t1 = ts[1], t2 = ts[2], t3 = ts[3];
		if (t == t1) return P1;
		if (t == t2) return P2;
		Vec2 A1 = P0.scale(slope(t1, t, t1, t0)).add(P1.scale(slope(t, t0, t1, t0)));
		Vec2 A2 = P1.scale(slope(t2, t, t2, t1)).add(P2.scale(slope(t, t1, t2, t1)));
		Vec2 A3 = P2.scale(slope(t3, t, t3, t2)).add(P3.scale(slope(t, t2, t3, t2)));
		Vec2 B1 = A1.scale(slope(t2, t, t2, t0)).add(A2.scale(slope(t, t0, t2, t0)));
		Vec2 B2 = A2.scale(slope(t3, t, t3, t1)).add(A3.scale(slope(t, t1, t3, t1)));
		return B1.scale(slope(t2, t, t2, t1)).add(B2.scale(slope(t, t1, t2, t1)));
	}

	private static float slope(float y1, float y0, float x1, float x0) {
		return (y1 - y0) / (x1 - x0);
	}

	private static float calcT(float x0, float x1, float y0, float y1, float t0, float a) {
		return (float)Math.pow(Mth.sqrt((float)(Math.pow(x1-x0, 2) + Math.pow(y1-y0, 2))), a) + t0;
	}

}

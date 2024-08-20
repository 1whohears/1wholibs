package com.onewhohears.onewholibs.client.model.obj.customanims;

import com.google.gson.JsonObject;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.onewhohears.onewholibs.util.UtilParse;
import com.onewhohears.onewholibs.util.math.UtilAngles;

import net.minecraft.world.entity.Entity;

/**
 * Forge's obj model render allows the manipulation of individual groups/folders/bones within the model.
 * See {@link net.minecraftforge.client.model.renderable.CompositeRenderable#render} and
 * {@link net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms}.
 * I call the names of these groups the {@link #model_part_key}.
 * Instead of hard coding these Transforms, my CustomAnims system allows you to define animations with
 * json assets. 1wholibs doesn't come with many premade animations, but it should be fairly straight forward
 * to make your own. Simply call {@link CustomAnims#addAnim(String, CustomAnims.AnimationFactory)} in
 * {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
 * You'll also have to set up a {@link com.onewhohears.onewholibs.data.jsonpreset.JsonPresetAssetReader}
 * using {@link com.onewhohears.onewholibs.data.jsonpreset.CustomAnimStats} to read the anims.
 * Then {@link CustomAnimsEntityModel} can use
 * {@link com.onewhohears.onewholibs.data.jsonpreset.CustomAnimStats#getModel()}.
 * See Diamond Star Combat for additional custom animation examples.
 * @author 1whohears
 */
public abstract class EntityModelTransform<T extends Entity> {
	
	public static final Matrix4f INVISIBLE = Matrix4f.createScaleMatrix(0, 0, 0);
	public static final Matrix4f NOTHING = Matrix4f.createScaleMatrix(1, 1, 1);
	
	private final String model_part_key;
	public EntityModelTransform(JsonObject data) {
		this(data.get("model_part_key").getAsString());
	}
	protected EntityModelTransform(String model_part_key) {
		this.model_part_key = model_part_key;
	}
	public String getKey() {
		return model_part_key;
	}
	public abstract Matrix4f getTransform(T entity, float partialTicks);
	public void addTransform(EntityModelTransform<T> transform) {}
	public boolean isGroup() {
		return false;
	}

	public static class AlwaysHide<T extends Entity> extends EntityModelTransform<T> {
		public AlwaysHide(JsonObject data) {
			super(data);
		}
		@Override
		public Matrix4f getTransform(T entity, float partialTicks) {
			return INVISIBLE;
		}
	}
	
	public static abstract class Translation<T extends Entity> extends EntityModelTransform<T> {
		private final Vector3f bounds;
		public Translation(JsonObject data) {
			super(data);
			bounds = UtilParse.readVec3f(data, "bounds");
		}
		public Vector3f getBounds() {
			return bounds;
		}
		public abstract float getTranslationProgress(T entity, float partialTicks);
		@Override
		public Matrix4f getTransform(T entity, float partialTicks) {
			float p = getTranslationProgress(entity, partialTicks);
			if (p == 0) return NOTHING;
			return Matrix4f.createTranslateMatrix(bounds.x() * p, bounds.y() * p, bounds.z() * p);
		}
	}
	
	public static abstract class Pivot<T extends Entity> extends EntityModelTransform<T> {
		private final Vector3f pivot;
		public Pivot(JsonObject data) {
			super(data);
			pivot = UtilParse.readVec3f(data, "pivot");
		}
		public Vector3f getPivot() {
			return pivot;
		}
	}
	
	public static enum RotationAxis {
		X, Y, Z
	}
	
	public abstract static class AxisRotation<T extends Entity> extends Pivot<T> {
		private final RotationAxis rot_axis;
		public AxisRotation(JsonObject data) {
			super(data);
			rot_axis = UtilParse.getEnumSafe(data, "rot_axis", RotationAxis.class);
		}
		public RotationAxis getRotAxis() {
			return rot_axis;
		}
		public abstract float getRotDeg(T entity, float partialTicks);
		@Override
		public Matrix4f getTransform(T entity, float partialTicks) {
			float degrees = getRotDeg(entity, partialTicks);
			if (degrees == 0) return NOTHING;
			switch (rot_axis) {
			case X: return UtilAngles.pivotRotX(getPivot().x(), getPivot().y(), getPivot().z(), degrees);
			case Y: return UtilAngles.pivotRotY(getPivot().x(), getPivot().y(), getPivot().z(), degrees);
			case Z: return UtilAngles.pivotRotZ(getPivot().x(), getPivot().y(), getPivot().z(), degrees);
			}
			return null;
		}
	}
	
	public static class ContinuousRotation<T extends Entity> extends AxisRotation<T> {
		private final float rot_rate;
		public ContinuousRotation(JsonObject data) {
			super(data);
			rot_rate = UtilParse.getFloatSafe(data, "rot_rate", 0);
		}
		public float getRotRate() {
			return rot_rate;
		}
		@Override
		public float getRotDeg(T entity, float partialTicks) {
			return UtilAngles.lerpAngle(partialTicks, entity.tickCount * getRotRate(), (entity.tickCount + 1) * getRotRate());
		}
	}
	
}

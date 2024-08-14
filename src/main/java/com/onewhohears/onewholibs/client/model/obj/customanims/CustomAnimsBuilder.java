package com.onewhohears.onewholibs.client.model.obj.customanims;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import com.onewhohears.onewholibs.client.model.obj.customanims.EntityModelTransform.RotationAxis;
import com.onewhohears.onewholibs.util.UtilParse;

public class CustomAnimsBuilder {
	
	public static CustomAnimsBuilder create() {
		return new CustomAnimsBuilder();
	}
	
	private final JsonArray anims = new JsonArray();
	
	private CustomAnimsBuilder() {
	}
	
	public JsonArray build() {
		return anims;
	}
	
	protected JsonObject createAnimJson(String model_part_key) {
		JsonObject anim = new JsonObject();
		anim.addProperty("model_part_key", model_part_key);
		anims.add(anim);
		return anim;
	}
	
	protected void fillAxisRotationParams(JsonObject anim, float pivotX, float pivotY, float pivotZ, RotationAxis rot_axis) {
		UtilParse.writeVec3f(anim, "pivot", new Vector3f(pivotX, pivotY, pivotZ));
		UtilParse.writeEnum(anim, "rot_axis", rot_axis);
	}

	protected void fillAxisRotationPixelParams(JsonObject anim, float pivotX, float pivotY, float pivotZ, RotationAxis rot_axis) {
		fillAxisRotationParams(anim, pivotX/16f, pivotY/16f, pivotZ/16f, rot_axis);
	}
	
	public CustomAnimsBuilder addContinuousRotAnim(String model_part_key, float pivotX, float pivotY, float pivotZ, RotationAxis rot_axis, float rot_rate) {
		JsonObject anim = createAnimJson(model_part_key);
		anim.addProperty("anim_id", "continuous_rotation");
		fillAxisRotationParams(anim, pivotX, pivotY, pivotZ, rot_axis);
		anim.addProperty("rot_rate", rot_rate);
		return this;
	}

	public CustomAnimsBuilder addContinuousRotPixelAnim(String model_part_key, float pivotX, float pivotY, float pivotZ, RotationAxis rot_axis, float rot_rate) {
		JsonObject anim = createAnimJson(model_part_key);
		anim.addProperty("anim_id", "continuous_rotation");
		fillAxisRotationPixelParams(anim, pivotX, pivotY, pivotZ, rot_axis);
		anim.addProperty("rot_rate", rot_rate);
		return this;
	}
	
}

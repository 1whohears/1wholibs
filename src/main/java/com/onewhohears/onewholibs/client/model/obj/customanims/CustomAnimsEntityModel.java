package com.onewhohears.onewholibs.client.model.obj.customanims;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Matrix4f;

import com.onewhohears.onewholibs.client.model.obj.ObjEntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms;

public class CustomAnimsEntityModel<T extends Entity> extends ObjEntityModel<T> {
	
	private final Map<String, EntityModelTransform<T>> transforms = new HashMap<>();
	
	public CustomAnimsEntityModel(String model_id, JsonArray anims) {
		super(model_id);
		for (int i = 0; i < anims.size(); ++i) {
			JsonObject anim = anims.get(i).getAsJsonObject();
			EntityModelTransform<T> t = CustomAnims.get(anim);
			if (t == null) continue;
			String model_part_key = t.getKey();
			if (transforms.containsKey(model_part_key)) {
				EntityModelTransform<T> t0 = transforms.get(model_part_key);
				if (t0.isGroup()) t0.addTransform(t);
				else {
					EntityModelTransformGroup<T> tg = new EntityModelTransformGroup<>(model_part_key, t0, t);
					transforms.put(model_part_key, tg);
				}
			} else transforms.put(model_part_key, t);
		}
	}
	
	@Override
	protected Transforms getComponentTransforms(T entity, float partialTicks) {
		ImmutableMap.Builder<String, Matrix4f> builder = ImmutableMap.<String, Matrix4f>builder();
		for (EntityModelTransform<T> trans : transforms.values())
			builder.put(trans.getKey(), trans.getTransform(entity, partialTicks));
		return Transforms.of(builder.build());
	}
	
}

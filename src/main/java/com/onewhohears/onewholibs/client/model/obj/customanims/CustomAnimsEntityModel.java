package com.onewhohears.onewholibs.client.model.obj.customanims;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms;
import org.joml.Matrix4f;

/**
 * Use this instead of {@link ObjEntityModel} if your entity has custom animations
 * defined by json asset files. See {@link EntityModelTransform}.
 * @author 1whohears
 */
public class CustomAnimsEntityModel<T extends Entity> extends ObjEntityModel<T> {
	
	protected final Map<String, EntityModelTransform<T>> transforms = new HashMap<>();
	
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
	protected void addComponentTransforms(Map<String, Matrix4f> transforms, T entity, float partialTicks) {
		super.addComponentTransforms(transforms, entity, partialTicks);
		for (EntityModelTransform<T> trans : this.transforms.values())
			transforms.put(trans.getKey(), trans.getTransform(entity, partialTicks));
	}

}

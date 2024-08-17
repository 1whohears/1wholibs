package com.onewhohears.onewholibs.client.model.obj.customanims;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.onewhohears.onewholibs.util.UtilParse;
import net.minecraft.world.entity.Entity;

/**
 * @author 1whohears
 */
public class CustomAnims {
	
	private static final Map<String, AnimationFactory> map = new HashMap<>();

	/**
	 * Add custom {@link EntityModelTransform} in
	 * {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}
	 */
	public static void addAnim(String id, AnimationFactory animationFactory) {
		map.put(id, animationFactory);
	}
	
	@Nullable
	public static <T extends Entity> EntityModelTransform<T> get(JsonObject json) {
		String anim_id = UtilParse.getStringSafe(json, "anim_id", "");
		if (anim_id.isEmpty() || !map.containsKey(anim_id)) return null;
		return map.get(anim_id).create(json);
	}
	
	public interface AnimationFactory<T extends Entity> {
		EntityModelTransform<T> create(JsonObject json);
	}
}

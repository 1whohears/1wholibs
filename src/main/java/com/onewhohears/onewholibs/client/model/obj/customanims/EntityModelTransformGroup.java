package com.onewhohears.onewholibs.client.model.obj.customanims;

import java.util.ArrayList;
import java.util.List;


import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;

/**
 * You will not need to override this ever. {@link CustomAnimsEntityModel} combines
 * {@link EntityModelTransform} automatically if multiple transforms manipulate the same model part.
 * @author 1whohears
 */
public class EntityModelTransformGroup<T extends Entity> extends EntityModelTransform<T> {
	
	private final List<EntityModelTransform<T>> transforms = new ArrayList<>();
	
	public EntityModelTransformGroup(String model_part_key, EntityModelTransform<T> t1, EntityModelTransform<T> t2) {
		super(model_part_key);
		transforms.add(t1);
		transforms.add(t2);
	}
	
	@Override
	public void addTransform(EntityModelTransform<T> transform) {
		transforms.add(transform);
	}

	@Override
	public Matrix4f getTransform(T entity, float partialTicks) {
		Matrix4f trans = new Matrix4f(transforms.get(0).getTransform(entity, partialTicks));
		for (int i = 1; i < transforms.size(); ++i) {
			trans.mul(transforms.get(i).getTransform(entity, partialTicks));
		}

		return trans;
	}

	
	@Override
	public boolean isGroup() {
		return true;
	}

}

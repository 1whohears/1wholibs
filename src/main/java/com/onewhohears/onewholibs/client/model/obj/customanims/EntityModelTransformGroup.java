package com.onewhohears.onewholibs.client.model.obj.customanims;

import java.util.ArrayList;
import java.util.List;

import com.mojang.math.Matrix4f;
import net.minecraft.world.entity.Entity;

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
		Matrix4f trans = transforms.get(0).getTransform(entity, partialTicks).copy();
		for (int i = 1; i < transforms.size(); ++i) 
			trans.multiply(transforms.get(i).getTransform(entity, partialTicks));
		return trans;
	}
	
	@Override
	public boolean isGroup() {
		return true;
	}

}

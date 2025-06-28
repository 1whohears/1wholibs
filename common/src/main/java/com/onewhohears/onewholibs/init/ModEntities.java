package com.onewhohears.onewholibs.init;

import com.google.common.collect.ImmutableSet;
import com.onewhohears.onewholibs.entity.TestEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class ModEntities {
    public static Supplier<EntityType<TestEntity>> testEntity() {
        return () -> createEntityType(TestEntity::new, EntityDimensions.fixed(1, 2));
    }

    private static <T extends Entity> EntityType<T> createEntityType(
            EntityType.EntityFactory<T> factory, EntityDimensions size
    ) {
        return new EntityType<>(
                factory, MobCategory.MISC,
                true, true, false, true,
                ImmutableSet.of(), size, 5, 3
        );
    }
}

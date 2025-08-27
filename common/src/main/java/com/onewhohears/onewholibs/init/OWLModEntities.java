package com.onewhohears.onewholibs.init;

import com.google.common.collect.ImmutableSet;
import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.entity.TestEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;

import java.util.function.Supplier;

public class OWLModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            OWLMod.MOD_ID, Registries.ENTITY_TYPE);

    public static RegistrySupplier<EntityType<TestEntity>> TEST = ENTITY_TYPES.register("test", testEntity());

    public static Supplier<EntityType<TestEntity>> testEntity() {
        return () -> createEntityType(TestEntity::new, EntityDimensions.fixed(1, 2));
    }

    private static <T extends Entity> EntityType<T> createEntityType(
            EntityType.EntityFactory<T> factory, EntityDimensions size
    ) {
        return new EntityType<>(
                factory, MobCategory.MISC,
                true, true, false, true,
                ImmutableSet.of(),
                size, 5, 3,
                FeatureFlagSet.of()
        );
    }

    public static void init() {
        ENTITY_TYPES.register();
    }
}

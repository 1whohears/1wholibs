package com.onewhohears.onewholibs.init;

import com.google.common.collect.ImmutableSet;
import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.entity.TestEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OWLMod.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = ENTITIES.register("test",
        () -> createEntityType(TestEntity::new, EntityDimensions.fixed(1, 2)));

    private static <T extends Entity> EntityType<T> createEntityType(EntityType.EntityFactory<T> factory, EntityDimensions size) {
        return new EntityType<>(factory, MobCategory.MISC, true, true, false,
                true, ImmutableSet.of(), size, 5, 3);
    }

}

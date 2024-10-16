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

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OWLMod.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    // Register the test entity using EntityType.Builder
    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = ENTITIES.register("test",
            () -> EntityType.Builder.<TestEntity>of(TestEntity::new, MobCategory.MISC)
                    .sized(1.0f, 2.0f)
                    .build("test"));

}

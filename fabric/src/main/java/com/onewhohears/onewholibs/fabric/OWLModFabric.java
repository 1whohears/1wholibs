package com.onewhohears.onewholibs.fabric;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.entity.TestEntity;
import com.onewhohears.onewholibs.init.OWLModEntities;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;

public final class OWLModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Make sure to reference this particular instance instead of the Supplier itself since a new instance of the
        // supplied object is created on each call of #get()
        EntityType<TestEntity> testEntityType = OWLModEntities.testEntity().get();

        Registry.register(Registry.ENTITY_TYPE, "test", testEntityType);

        OWLMod.init();
    }
}

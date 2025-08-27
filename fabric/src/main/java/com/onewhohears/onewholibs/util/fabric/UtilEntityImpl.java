package com.onewhohears.onewholibs.util.fabric;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.NoSuchElementException;

public class UtilEntityImpl {

    public static EntityType<?> getEntityType(String entityTypeKey, EntityType<?> alt) {
        if (entityTypeKey == null || entityTypeKey.isEmpty()) return alt;
        try {
            ResourceLocation rl = ResourceLocation.tryParse(entityTypeKey);
            if (rl == null) return alt;
            return Registry.ENTITY_TYPE.get(rl);
        } catch(NoSuchElementException e) {
            return alt;
        }
    }

    public static boolean doesEntityTypeExist(String entityTypeKey) {
        ResourceLocation rl = ResourceLocation.tryParse(entityTypeKey);
        if (rl == null) return false;
        return Registry.ENTITY_TYPE.containsKey(rl);
    }

}

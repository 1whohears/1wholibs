package com.onewhohears.onewholibs.util.forge;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Optional;

public class UtilEntityImpl {

    public static EntityType<?> getEntityType(String entityTypeKey, EntityType<?> alt) {
        if (entityTypeKey == null || entityTypeKey.isEmpty()) return alt;
        try {
            ResourceLocation rl = ResourceLocation.tryParse(entityTypeKey);
            if (rl == null) return alt;
            @NotNull Optional<Holder.Reference<EntityType<?>>> ref = ForgeRegistries.ENTITY_TYPES.getDelegate(rl);
            if (ref.isEmpty()) return alt;
            return ref.get().get();
        } catch(NoSuchElementException e) {
            return alt;
        }
    }

    public static boolean doesEntityTypeExist(String entityTypeKey) {
        ResourceLocation rl = ResourceLocation.tryParse(entityTypeKey);
        if (rl == null) return false;
        return ForgeRegistries.ENTITY_TYPES.containsKey(rl);
    }

}

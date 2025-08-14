package com.onewhohears.onewholibs.util.forge;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Optional;

public class UtilItemImpl {

    public static Item getItem(String itemKey, Item alt) {
        try {
            ResourceLocation rl = ResourceLocation.tryParse(itemKey);
            if (rl == null) return alt;
            @NotNull Optional<Holder.Reference<Item>> ref = ForgeRegistries.ITEMS.getDelegate(rl);
            return ref.<Item>map(Holder::get).orElse(alt);
        } catch(NoSuchElementException e) { return alt; }
    }

    public static ResourceLocation getItemKey(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

}

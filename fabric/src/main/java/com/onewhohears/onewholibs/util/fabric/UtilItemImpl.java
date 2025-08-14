package com.onewhohears.onewholibs.util.fabric;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.NoSuchElementException;

public class UtilItemImpl {

    public static Item getItem(String itemKey, Item alt) {
        try {
            ResourceLocation rl = ResourceLocation.tryParse(itemKey);
            if (rl == null) return alt;
            return Registry.ITEM.get(rl);
        } catch(NoSuchElementException e) { return alt; }
    }

    public static ResourceLocation getItemKey(Item item) {
        return Registry.ITEM.getKey(item);
    }

}

package com.onewhohears.onewholibs.util.forge;

import com.onewhohears.onewholibs.client.renderer.RendererObjModelItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

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

    private static final IClientItemExtensions OBJ_EXTENSION = new IClientItemExtensions() {
        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return RendererObjModelItems.get();
        }
    };

    public static void onObjModelItemInitClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(OBJ_EXTENSION);
    }

}

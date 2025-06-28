package com.onewhohears.onewholibs.item;

import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import com.onewhohears.onewholibs.client.renderer.RendererObjModelItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface ObjModelItem {
    /**
     * get a preset id for a {@link com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener}
     */
    @NotNull String getPreset(@NotNull ItemStack stack);
    /**
     * get a model id based on the preset id.
     */
    @NotNull String getObjModelId(@NotNull String preset);

    /**
     * this method is meant to be explicitly called by the inheriting item in an
     * {@link net.minecraft.world.item.Item#initializeClient(Consumer)} override.
     */
    default void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return RendererObjModelItems.get();
            }
        });
    }

    /**
     * if the automatic scaling system doesn't work return something other than
     * {@link com.onewhohears.onewholibs.client.model.obj.ObjEntityModels#NO_OVERRIDES}
     */
    @NotNull ObjEntityModels.ModelOverrides getItemModelOverrides(@NotNull String preset);
}

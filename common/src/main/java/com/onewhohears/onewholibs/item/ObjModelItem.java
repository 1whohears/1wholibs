package com.onewhohears.onewholibs.item;

import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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
     * if the automatic scaling system doesn't work return something other than
     * {@link com.onewhohears.onewholibs.client.model.obj.ObjEntityModels#NO_OVERRIDES}
     */
    @NotNull ObjEntityModels.ModelOverrides getItemModelOverrides(@NotNull String preset);
}

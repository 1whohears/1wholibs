package com.onewhohears.onewholibs.util.forge;

import com.onewhohears.onewholibs.client.renderer.RendererObjModelItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class UtilItemClient {

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

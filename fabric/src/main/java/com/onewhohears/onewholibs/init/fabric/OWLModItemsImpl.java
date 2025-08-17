package com.onewhohears.onewholibs.init.fabric;

import dev.architectury.registry.registries.RegistrySupplier;
import dev.felnull.specialmodelloader.api.SpecialModelLoaderAPI;
import dev.felnull.specialmodelloader.api.model.LoadedResource;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.world.item.Item;

public class OWLModItemsImpl {

    public static void registerObjItemModel(RegistrySupplier<Item> reg) {
        // FIXME why does BuiltinItemRendererRegistry not exist????
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(manager ->
                (id, ctx) -> {
            if (!id.equals(reg.getId())) return null;
            // FIXME this does not allow for item models to change based on nbt
            LoadedResource lr = SpecialModelLoaderAPI.getInstance().loadResource(manager, id);
            if (lr == null) return null;
            return SpecialModelLoaderAPI.getInstance().makeModel(lr);
        });
    }

}

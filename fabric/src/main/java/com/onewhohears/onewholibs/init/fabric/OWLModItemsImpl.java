package com.onewhohears.onewholibs.init.fabric;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.world.item.Item;

public class OWLModItemsImpl {

    public static void registerObjItemModel(RegistrySupplier<Item> reg) {
        // FIXME why does BuiltinItemRendererRegistry not exist????
        /*ModelLoadingRegistry.INSTANCE.registerResourceProvider(manager ->
                (id, ctx) -> {
            if (!id.equals(reg.getId())) {
                return null;
            } else {
                return null;
            }
            // FIXME render obj models over items in fabric
        });*/
    }

}

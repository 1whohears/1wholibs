package com.onewhohears.onewholibs.init;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.item.ObjModelItem;
import com.onewhohears.onewholibs.item.TestItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public class OWLModItems {

    public static final Registrar<Item> ITEMS = OWLMod.REGISTRIES.get().get(Registry.ITEM_REGISTRY);

    public static final RegistrySupplier<Item> TEST_ITEM = register("test_item", TestItem::create);

    public static RegistrySupplier<Item> register(String itemId, Supplier<Item> item) {
        RegistrySupplier<Item> reg = ITEMS.register(ResourceLocation.tryBuild(OWLMod.MOD_ID, itemId), item);
        if (item.get() instanceof ObjModelItem) registerObjItemModel(reg);
        return reg;
    }

    @ExpectPlatform
    public static void registerObjItemModel(RegistrySupplier<Item> reg) {

    }

}

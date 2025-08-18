package com.onewhohears.onewholibs.init;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.item.ObjModelItem;
import com.onewhohears.onewholibs.item.TestItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public class OWLModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(OWLMod.MOD_ID, Registry.ITEM_REGISTRY);

    public static final RegistrySupplier<Item> TEST_ITEM = register("test_obj_model_item", TestItem::create);

    public static RegistrySupplier<Item> register(String itemId, Supplier<Item> item) {
        return ITEMS.register(ResourceLocation.tryBuild(OWLMod.MOD_ID, itemId), item);
    }

    @ExpectPlatform
    public static void registerObjItemModel(RegistrySupplier<Item> reg) {
        throw new AssertionError();
    }

    public static void init() {
        ITEMS.register();
    }

    public static void registerAllObjItemModels() {
        ITEMS.forEach(reg -> {
            if (reg.get() instanceof ObjModelItem)
                registerObjItemModel(reg);
        });
    }

}

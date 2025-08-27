package com.onewhohears.onewholibs.item;

import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestItem extends Item implements ObjModelItem {

    @ExpectPlatform
    public static TestItem create() {
        return new TestItem();
    }

    public TestItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull String getPreset(@NotNull ItemStack stack) {
        if (!stack.hasTag()) return "simple_test";
        return stack.getOrCreateTag().getString("model");
    }

    @Override
    public @NotNull String getObjModelId(@NotNull String preset) {
        return preset;
    }

    @Override
    public @NotNull ObjEntityModels.ModelOverrides getItemModelOverrides(@NotNull String preset) {
        return ObjEntityModels.NO_OVERRIDES;
    }
}

package com.onewhohears.onewholibs.mixin;

import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.class)
public interface IngredientAccessor {
    @Accessor(value = "values")
    Ingredient.Value[] getValues();
}

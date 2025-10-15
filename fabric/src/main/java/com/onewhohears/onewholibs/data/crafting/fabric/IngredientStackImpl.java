package com.onewhohears.onewholibs.data.crafting.fabric;

import com.onewhohears.onewholibs.data.crafting.IngredientStack;
import com.onewhohears.onewholibs.mixin.IngredientAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

public class IngredientStackImpl {

    public static void dissolve(Ingredient ingredient) {
        if (ingredient.itemStacks == null) {
            ingredient.itemStacks = Arrays.stream(((IngredientAccessor)ingredient).getValues())
                    .flatMap((value) -> value.getItems().stream())
                    .distinct().toArray(ItemStack[]::new);
            if (ingredient instanceof IngredientStack stack)
                for (ItemStack item : ingredient.itemStacks) item.setCount(stack.cost);
        }
    }

}

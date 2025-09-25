package com.onewhohears.onewholibs.data.crafting;

import com.onewhohears.onewholibs.util.UtilItem;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * An {@link Ingredient} with an additional {@link #cost} field.
 * See {@link IngredientStackBuilder} to build presets with this ingredient format.
 * @author 1whohears
 */
public class IngredientStack extends Ingredient {
	
	public static IngredientStack fromItem(String itemId, int cost) {
		return new IngredientStack(new Ingredient.ItemValue(UtilItem.getItem(itemId).getDefaultInstance()), cost);
	}
	
	public static IngredientStack fromTag(String tagId, int cost) {
		return new IngredientStack(new Ingredient.TagValue(createItemTag(tagId)), cost);
	}

    public static TagKey<Item> createItemTag(String id) {
        return TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation.tryParse(id));
    }

	public static IngredientStack fromIngredient(Ingredient ingredient) {
        Ingredient.Value[] values = new Ingredient.Value[ingredient.getItems().length];
		for (int i = 0; i < values.length; ++i)
			values[i] = new Ingredient.ItemValue(ingredient.getItems()[i]);
		return new IngredientStack(Stream.of(values), 1);
	}
	
	public final int cost;
	
	protected IngredientStack(Ingredient.Value value, int cost) {
		this(Stream.of(value), cost);
	}

	protected IngredientStack(Stream<Ingredient.Value> values, int cost) {
		super(values);
		this.cost = cost;
	}
	/**
	 * @return true if stack is the same item and {@link ItemStack#getCount()} >= {@link #cost}
	 */
	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) return false;
		ItemStack[] items = getItems();
		if (items.length == 0) return stack.isEmpty();
		for(ItemStack itemstack : items) 
			if (itemstack.is(stack.getItem()) && stack.getCount() >= cost) 
				return true;
		return false;
	}
	
	@Override
	public ItemStack @NotNull [] getItems() {
		ItemStack[] items = super.getItems();
		for (ItemStack item : items) item.setCount(cost);
		return items;
	}
}

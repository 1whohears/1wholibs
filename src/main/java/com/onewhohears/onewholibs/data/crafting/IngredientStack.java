package com.onewhohears.onewholibs.data.crafting;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.onewhohears.onewholibs.util.UtilItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * An {@link Ingredient} with an additional {@link #cost} field.
 * See {@link IngredientStackBuilder} to build presets with this ingredient format.
 * @author 1whohears
 */
public class IngredientStack extends Ingredient {
	
	public static IngredientStack fromItem(String itemId, int cost) {
		return new IngredientStack(new ItemValue(UtilItem.getItem(itemId).getDefaultInstance()), cost);
	}
	
	public static IngredientStack fromTag(String tagId, int cost) {
		return new IngredientStack(new TagValue(ItemTags.create(new ResourceLocation(tagId))), cost);
	}

	public static IngredientStack fromIngredient(Ingredient ingredient) {
		Value[] values = new Value[ingredient.getItems().length];
		for (int i = 0; i < values.length; ++i)
			values[i] = new ItemValue(ingredient.getItems()[i]);
		return new IngredientStack(Stream.of(values), 1);
	}
	
	public final int cost;
	
	protected IngredientStack(Value value, int cost) {
		this(Stream.of(value), cost);
	}

	protected IngredientStack(Stream<Value> values, int cost) {
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
	public ItemStack[] getItems() {
		ItemStack[] items = super.getItems();
		for (ItemStack item : items) item.setCount(cost);
		return items;
	}

}

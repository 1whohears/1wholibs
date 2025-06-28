package com.onewhohears.onewholibs.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.onewhohears.onewholibs.data.crafting.IngredientStack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author 1whohears
 */
public class UtilItem {

	public static Item getItem(String itemKey, Item alt) {
		try {
			return ForgeRegistries.ITEMS.getDelegate(
				new ResourceLocation(itemKey)).get().get();
		} catch(NoSuchElementException e) { return alt; }
	}
	
	public static Item getItem(String itemKey) {
		return getItem(itemKey, Items.AIR);
	}
	
	public static ResourceLocation getItemKey(Item item) {
		return ForgeRegistries.ITEMS.getKey(item);
	}
	
	public static String getItemKeyString(Item item) {
		return getItemKey(item).toString();
	}

	/**
	 * designed to be compatible with {@link IngredientStack}
	 * @return true if the container has all the ingredients in recipeItems
	 */
	public static boolean testRecipe(NonNullList<Ingredient> recipeItems, Container container) {
		for (int i = 0; i < recipeItems.size(); ++i) {
			Ingredient ingredient = recipeItems.get(i);
			boolean found = false;
			for (int j = 0; j < container.getContainerSize(); ++j) {
				if (ingredient.test(container.getItem(j))) {
					found = true;
					break;
				}
			}
			if (found) continue;
			return false;
		}
		return true;
	}
	/**
	 * designed to be compatible with {@link IngredientStack}
	 * @return returns the indexes of items within recipeItems that were not found in container
	 */
	public static List<Integer> testRecipeFails(NonNullList<Ingredient> recipeItems, Container container) {
		List<Integer> fails = new ArrayList<Integer>();
		for (int i = 0; i < recipeItems.size(); ++i) {
			Ingredient ingredient = recipeItems.get(i);
			boolean found = false;
			for (int j = 0; j < container.getContainerSize(); ++j) {
				if (ingredient.test(container.getItem(j))) {
					found = true;
					break;
				}
			}
			if (!found) fails.add(i);
		}
		return fails;
	}
	/**
	 * test the recipe with {@link #testRecipe(NonNullList, Container)} before use!
	 * designed to be compatible with {@link IngredientStack}
	 * @return a list of items that is the same as container, but items from ingredients are removed.
	 */
	public static NonNullList<ItemStack> getRemainingItemsStackIngredients(Container container, NonNullList<Ingredient> ingredients) {
		NonNullList<ItemStack> remainItems = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
		for(int i = 0; i < remainItems.size(); ++i) remainItems.set(i, container.getItem(i));
		for (int i = 0; i < ingredients.size(); ++i) {
			Ingredient ingredient = ingredients.get(i);
			IngredientStack ing;
			if (ingredient.getClass() == IngredientStack.class) ing = (IngredientStack) ingredient;
			else ing = IngredientStack.fromIngredient(ingredient);
			int found = 0;
			for (int j = 0; j < remainItems.size(); ++j) {
				ItemStack item = remainItems.get(j);
				if (item.isEmpty()) continue;
				if (ing.test(item)) {
					found += item.getCount();
					if (found >= ing.cost) {
						item.setCount(found - ing.cost);
						break;
					} else item.setCount(0);
				}
			}
			if (found < ing.cost) break;
		}
		return remainItems;
	}

	/**
	 * If player has recipe items in their inventory, take the recipe items out of the player inventory
	 * and spawn the result item at pos.
	 * designed to be compatible with {@link IngredientStack}.
	 * @param player check player's inventory for the recipe items
	 * @param recipe check player's inventory for the recipe's items
	 * @param pos the position the crafted item should land
	 */
	public static void handleInventoryRecipe(Player player, Recipe<Inventory> recipe, BlockPos pos) {
		if (!recipe.matches(player.getInventory(), player.level)) return;
		NonNullList<ItemStack> remainingItems = getRemainingItemsStackIngredients(player.getInventory(), recipe.getIngredients());
		for (int i = 0; i < player.getInventory().getContainerSize(); ++i) 
			player.getInventory().setItem(i, remainingItems.get(i));
		ItemStack stack = recipe.assemble(player.getInventory());
		Containers.dropItemStack(player.level, pos.getX()+0.5, 
			pos.getY()+1.125, pos.getZ()+0.5, stack);
	}
	
	public static MenuType<?> getChestMenuTypeByRows(int rows) {
		if (rows == 1) return MenuType.GENERIC_9x1;
		else if (rows == 2) return MenuType.GENERIC_9x2;
		else if (rows == 3) return MenuType.GENERIC_9x3;
		else if (rows == 4) return MenuType.GENERIC_9x4;
		else if (rows == 5) return MenuType.GENERIC_9x5;
		else if (rows == 6) return MenuType.GENERIC_9x6;
		return MenuType.GENERIC_9x6;
	}
	
}

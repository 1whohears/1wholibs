package com.onewhohears.onewholibs.data.crafting;

import com.onewhohears.onewholibs.util.UtilItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
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

    public static IngredientStack fromItem(ItemStack item, int cost) {
        return new IngredientStack(new Ingredient.ItemValue(item), cost);
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

    public static @NotNull IngredientStack fromNetwork(FriendlyByteBuf buffer) {
        Stream<Ingredient.Value> stream = buffer.readList(FriendlyByteBuf::readItem).stream().map(ItemValue::new);
        int cost = buffer.readInt();
        return new IngredientStack(stream, cost);
    }

    public static void toNetwork(FriendlyByteBuf buffer, Ingredient ingredient) {
        dissolve(ingredient);
        buffer.writeCollection(Arrays.asList(ingredient.itemStacks), FriendlyByteBuf::writeItem);
        if (ingredient instanceof IngredientStack stack) buffer.writeInt(stack.cost);
        else buffer.writeInt(1);
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
    public void dissolve() {
        super.dissolve();
        for (ItemStack item : itemStacks) item.setCount(cost);
    }

    @ExpectPlatform
    public static void dissolve(Ingredient ingredient) {
        throw new AssertionError();
    }
}

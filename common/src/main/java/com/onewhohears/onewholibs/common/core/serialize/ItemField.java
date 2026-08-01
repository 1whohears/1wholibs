package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.onewhohears.onewholibs.util.UtilItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class ItemField extends SerialField<Item> {

    public ItemField(@NotNull String name, @NotNull Item defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull Item value) {
        return new JsonPrimitive(UtilItem.getItemKeyString(value));
    }

    @Override
    protected Item read(@NotNull JsonElement valueJson) {
        return UtilItem.getItem(valueJson.getAsString(), getDefaultValue());
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull Item value, boolean encodeAll) {
        buffer.writeUtf(UtilItem.getItemKeyString(value));
    }

    @Override
    protected Item read(@NotNull FriendlyByteBuf buffer) {
        return UtilItem.getItem(buffer.readUtf(), getDefaultValue());
    }
}

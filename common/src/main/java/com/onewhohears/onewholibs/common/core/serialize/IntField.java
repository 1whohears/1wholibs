package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class IntField extends SerialField<Integer> {

    public IntField(@NotNull String name, @NotNull Integer defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull Integer value) {
        return new JsonPrimitive(value);
    }

    @Override
    protected Integer read(@NotNull JsonElement valueJson) {
        return valueJson.getAsInt();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull Integer value, boolean encodeAll) {
        buffer.writeInt(value);
    }

    @Override
    protected Integer read(@NotNull FriendlyByteBuf buffer) {
        return buffer.readInt();
    }

}

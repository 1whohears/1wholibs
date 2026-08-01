package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class LongField extends SerialField<Long> {

    public LongField(@NotNull String name, @NotNull Long defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull Long value) {
        return new JsonPrimitive(value);
    }

    @Override
    protected Long read(@NotNull JsonElement valueJson) {
        return valueJson.getAsLong();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull Long value, boolean encodeAll) {
        buffer.writeLong(value);
    }

    @Override
    protected Long read(@NotNull FriendlyByteBuf buffer) {
        return buffer.readLong();
    }
}

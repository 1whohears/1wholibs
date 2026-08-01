package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class BoolField extends SerialField<Boolean> {

    public BoolField(@NotNull String name, @NotNull Boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull Boolean value) {
        return new JsonPrimitive(value);
    }

    @Override
    protected Boolean read(@NotNull JsonElement valueJson) {
        return valueJson.getAsBoolean();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull Boolean value, boolean encodeAll) {
        buffer.writeBoolean(value);
    }

    @Override
    protected Boolean read(@NotNull FriendlyByteBuf buffer) {
        return buffer.readBoolean();
    }
}

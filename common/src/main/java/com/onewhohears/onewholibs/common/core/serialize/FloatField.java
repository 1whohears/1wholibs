package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class FloatField extends SerialField<Float> {

    public FloatField(@NotNull String name, @NotNull Float defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull Float value) {
        return new JsonPrimitive(value);
    }

    @Override
    protected Float read(@NotNull JsonElement valueJson) {
        return valueJson.getAsFloat();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull Float value, boolean encodeAll) {
        buffer.writeFloat(value);
    }

    @Override
    protected Float read(@NotNull FriendlyByteBuf buffer) {
        return buffer.readFloat();
    }
}

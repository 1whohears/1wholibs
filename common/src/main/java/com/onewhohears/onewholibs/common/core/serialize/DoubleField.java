package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class DoubleField extends SerialField<Double> {

    public DoubleField(@NotNull String name, @NotNull Double defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull Double value) {
        return new JsonPrimitive(value);
    }

    @Override
    protected Double read(@NotNull JsonElement valueJson) {
        return valueJson.getAsDouble();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull Double value, boolean encodeAll) {
        buffer.writeDouble(value);
    }

    @Override
    protected Double read(@NotNull FriendlyByteBuf buffer) {
        return buffer.readDouble();
    }
}

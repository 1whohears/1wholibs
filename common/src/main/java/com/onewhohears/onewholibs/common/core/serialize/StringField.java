package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class StringField extends SerialField<String> {

    public StringField(@NotNull String name, @NotNull String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull String value) {
        return new JsonPrimitive(value);
    }

    @Override
    protected String read(@NotNull JsonElement valueJson) {
        return valueJson.getAsString();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull String value, boolean encodeAll) {
        buffer.writeUtf(value);
    }

    @Override
    protected String read(@NotNull FriendlyByteBuf buffer) {
        return buffer.readUtf();
    }
}

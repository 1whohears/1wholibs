package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDField extends SerialField<UUID> {

    public UUIDField(@NotNull String name, @NotNull UUID defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull UUID value) {
        return new JsonPrimitive(value.toString());
    }

    @Override
    protected UUID read(@NotNull JsonElement valueJson) {
        String uuidStr = valueJson.getAsString();
        return UUID.fromString(uuidStr);
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull UUID value, boolean encodeAll) {
        buffer.writeUUID(value);
    }

    @Override
    protected UUID read(@NotNull FriendlyByteBuf buffer) {
        return buffer.readUUID();
    }
}

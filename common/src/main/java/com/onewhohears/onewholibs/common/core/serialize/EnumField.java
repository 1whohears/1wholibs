package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class EnumField<E extends Enum<E>> extends SerialField<E> {

    public EnumField(@NotNull String name, @NotNull E defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull E value) {
        return new JsonPrimitive(value.toString());
    }

    @Override
    protected E read(@NotNull JsonElement valueJson) {
        E[] enums = get().getDeclaringClass().getEnumConstants();
        if (enums.length == 0) return getDefaultValue();
        String enumName = valueJson.getAsString();
        for (E anEnum : enums)
            if (anEnum.name().equals(enumName))
                return anEnum;
        return getDefaultValue();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull E value, boolean encodeAll) {
        buffer.writeEnum(value);
    }

    @Override
    protected E read(@NotNull FriendlyByteBuf buffer) {
        return buffer.readEnum(get().getDeclaringClass());
    }
}

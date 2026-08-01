package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class NbtField extends SerialField<CompoundTag> {

    public NbtField(@NotNull String name, @NotNull CompoundTag defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull CompoundTag value) {
        return new JsonPrimitive(NbtUtils.structureToSnbt(value));
    }

    @Override
    protected CompoundTag read(@NotNull JsonElement valueJson) {
        String nbtStr = valueJson.getAsString();
        try { return NbtUtils.snbtToStructure(nbtStr); }
        catch (CommandSyntaxException e) { return getDefaultValue(); }
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull CompoundTag value, boolean encodeAll) {
        buffer.writeUtf(NbtUtils.structureToSnbt(value));
    }

    @Override
    protected CompoundTag read(@NotNull FriendlyByteBuf buffer) {
        String nbtStr = buffer.readUtf();
        try { return NbtUtils.snbtToStructure(nbtStr); }
        catch (CommandSyntaxException e) { return getDefaultValue(); }
    }
}

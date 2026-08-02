package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackField extends SerialField<ItemStack> {

    public ItemStackField(@NotNull String name, @NotNull ItemStack defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull ItemStack value) {
        CompoundTag itemTag = new CompoundTag();
        value.save(itemTag);
        String itemTagStr = NbtUtils.structureToSnbt(itemTag);
        return new JsonPrimitive(itemTagStr);
    }

    @Override
    protected ItemStack read(@NotNull JsonElement valueJson) {
        String itemTagStr = valueJson.getAsString();
        CompoundTag itemTag;
        try {
            itemTag = NbtUtils.snbtToStructure(itemTagStr);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return ItemStack.EMPTY;
        }
        return ItemStack.of(itemTag);
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull ItemStack value, boolean encodeAll) {
        CompoundTag itemTag = new CompoundTag();
        value.save(itemTag);
        String itemTagStr = NbtUtils.structureToSnbt(itemTag);
        buffer.writeUtf(itemTagStr);
    }

    @Override
    protected ItemStack read(@NotNull FriendlyByteBuf buffer) {
        String itemTagStr = buffer.readUtf();
        CompoundTag itemTag;
        try {
            itemTag = NbtUtils.snbtToStructure(itemTagStr);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return ItemStack.EMPTY;
        }
        return ItemStack.of(itemTag);
    }
}

package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.onewhohears.onewholibs.util.UtilParse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Vec3Field extends SerialField<Vec3> {

    public Vec3Field(@NotNull String name, @NotNull Vec3 defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull Vec3 value) {
        return UtilParse.writeVec3Direct(value);
    }

    @Override
    protected Vec3 read(@NotNull JsonElement valueJson) {
        return UtilParse.readVec3Direct(valueJson.getAsJsonObject());
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull Vec3 value, boolean encodeAll) {
        buffer.writeDouble(get().x());
        buffer.writeDouble(get().y());
        buffer.writeDouble(get().z());
    }

    @Override
    protected Vec3 read(@NotNull FriendlyByteBuf buffer) {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        return new Vec3(x, y, z);
    }
}

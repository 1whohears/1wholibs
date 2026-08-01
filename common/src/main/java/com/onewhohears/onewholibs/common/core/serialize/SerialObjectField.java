package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class SerialObjectField<S extends SerialObject> extends SerialField<S> {

    public SerialObjectField(@NotNull String name, @NotNull S defaultValue) {
        super(name, defaultValue);
    }

    @Override
    protected JsonElement write(@NotNull S value) {
        return value.getSaveData();
    }

    @Override
    protected S read(@NotNull JsonElement valueJson) {
        get().loadSaveData(valueJson.getAsJsonObject());
        return get();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull S value, boolean encodeAll) {
        value.writePacket(buffer, encodeAll);
    }

    @Override
    protected S read(@NotNull FriendlyByteBuf buffer) {
        get().readPacket(buffer);
        return get();
    }

    @Override
    public boolean isEqual(@NotNull Object other) {
        if (other instanceof SerialObject otherSer) return get().isEqual(otherSer);
        return false;
    }

    @Override
    public void resetChanged() {
        super.resetChanged();
        get().resetChanged();
    }

    @Override
    public boolean isChanged() {
        return super.isChanged() || get().isChanged();
    }

    @Override
    public boolean isNetworkChanged() {
        return get().isNetworkChanged();
    }
}

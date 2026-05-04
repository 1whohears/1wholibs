package com.onewhohears.onewholibs.common.core;

import com.google.gson.JsonObject;
import com.onewhohears.onewholibs.util.UtilCompression;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class Serializable {

    private boolean isDirty = true;
    private final Set<String> dirtyReasons = new HashSet<>();

    public final void loadSaveData(@NotNull JsonObject data) {
        readSaveData(data);
    }

    @NotNull
    public final JsonObject getSaveData() {
        JsonObject data = new JsonObject();
        addSaveData(data);
        return data;
    }

    public final void readPacket(@NotNull FriendlyByteBuf buffer) {
        JsonObject data = UtilCompression.readCompressedJson(buffer);
        readSaveDataFromPacket(data);
    }

    public final void writePacket(@NotNull FriendlyByteBuf buffer) {
        JsonObject data = new JsonObject();
        addSaveDataForPacket(data);
        UtilCompression.writeCompressedJson(data, buffer);
    }

    protected void addSaveDataForPacket(@NotNull JsonObject data) {
        addSaveData(data);
    }

    protected void readSaveDataFromPacket(@NotNull JsonObject data) {
        readSaveData(data);
    }

    protected abstract void addSaveData(@NotNull JsonObject data);
    protected abstract void readSaveData(@NotNull JsonObject data);

    public final boolean isDirty() {
        return isDirty;
    }

    public void setDirty() {
        setDirty(null);
    }

    public void setDirty(@Nullable String reason) {
        isDirty = true;
        if (reason != null) dirtyReasons.add(reason);
    }

    public final void resetDirty() {
        isDirty = false;
        dirtyReasons.clear();
    }

    public Set<String> getDirtyReasons() {
        return dirtyReasons;
    }

}

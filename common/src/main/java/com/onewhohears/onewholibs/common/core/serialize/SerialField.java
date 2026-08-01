package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class SerialField<E> {

    private @NotNull final String name;
    private @NotNull final E defaultValue;

    private @NotNull E value;
    private boolean changed;

    public SerialField(@NotNull String name, @NotNull E defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;
    }

    protected abstract JsonElement write(@NotNull E value);
    protected abstract E read(@NotNull JsonElement valueJson);

    public void save(@NotNull JsonObject parentData) {
        JsonElement valueJson = write(get());
        parentData.add(getName(), valueJson);
    }

    public void load(@NotNull JsonObject parentData) {
        JsonElement valueJson = parentData.get(getName());
        if (valueJson == null) set(getDefaultValue());
        else set(read(valueJson));
    }

    protected abstract void write(@NotNull FriendlyByteBuf buffer, @NotNull E value, boolean encodeAll);
    protected abstract E read(@NotNull FriendlyByteBuf buffer);

    public void encode(@NotNull FriendlyByteBuf buffer, boolean encodeAll) {
        buffer.writeUtf(getName());
        write(buffer, value, encodeAll);
    }

    public void decode(@NotNull FriendlyByteBuf buffer) {
        this.value = read(buffer);
    }

    public @NotNull E getDefaultValue() {
        return defaultValue;
    }

    public E get() {
        return value;
    }

    public void set(@NotNull E value) {
        if (!isEqual(value)) changed = true;
        this.value = value;
    }

    public void setNoCheck(@NotNull E value) {
        this.value = value;
    }

    public boolean isEqual(@NotNull Object other) {
        return Objects.equals(value, other);
    }

    public @NotNull String getName() {
        return name;
    }

    public boolean isChanged() {
        return changed;
    }

    public boolean isNetworkChanged() {
        return isChanged();
    }

    public void setChanged() {
        changed = true;
    }

    public void resetChanged() {
        changed = false;
    }

    @Override
    public String toString() {
        return get().toString();
    }

}

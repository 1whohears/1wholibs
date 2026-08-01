package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ListField<L extends List<F>, F extends SerialField<E>, E> extends SerialField<L> {

    private final Supplier<F> entryGen;

    public ListField(@NotNull String name, @NotNull L defaultValue, @NotNull Supplier<F> entryGen) {
        super(name, defaultValue);
        this.entryGen = entryGen;
    }

    public E get(int index) {
        return get().get(index).get();
    }

    public boolean add(E value) {
        F field = entryGen.get();
        field.set(value);
        setChanged();
        return get().add(field);
    }

    public void add(int index, E value) {
        F field = entryGen.get();
        field.set(value);
        get().add(index, field);
        setChanged();
    }

    public boolean remove(int index) {
        boolean removed = get().remove(index) != null;
        if (removed) setChanged();
        return removed;
    }

    public void removeIf(Predicate<E> test) {
        int sizePre = get().size();
        get().removeIf(entry -> test.test(entry.get()));
        if (get().size() != sizePre) setChanged();
    }

    public int size() {
        return get().size();
    }

    public boolean isEmpty() {
        return get().isEmpty();
    }

    @Override
    protected JsonElement write(@NotNull L value) {
        JsonArray list = new JsonArray();
        for (F field : value) {
            list.add(field.write(field.get()));
        }
        return list;
    }

    @Override
    protected L read(@NotNull JsonElement valueJson) {
        get().clear();
        JsonArray list = valueJson.getAsJsonArray();
        for (int i = 0; i < list.size(); ++i) {
            F entry = entryGen.get();
            entry.setNoCheck(entry.read(list.get(i)));
            get().add(entry);
        }
        return get();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull L value, boolean encodeAll) {
        /*buffer.writeBoolean(encodeAll);
        if (encodeAll) {
            buffer.writeInt(value.size());
            for (F field : value) {
                field.encode(buffer, true);
            }
        } else {
            List<F> list = value.stream().filter(SerialField::isChanged).toList();

        }*/
        buffer.writeInt(value.size());
        for (F field : value) {
            field.write(buffer, field.get(), encodeAll);
        }
    }

    @Override
    protected L read(@NotNull FriendlyByteBuf buffer) {
        /*boolean encodeAll = buffer.readBoolean();
        if (encodeAll) {
            get().clear();
            int num = buffer.readInt();
            for (int i = 0; i < num; ++i) {
                F entry = entryGen.get();
                buffer.readUtf();
                entry.decode(buffer);
                get().add(entry);
            }
        } else {

        }*/
        get().clear();
        int num = buffer.readInt();
        for (int i = 0; i < num; ++i) {
            F entry = entryGen.get();
            entry.setNoCheck(entry.read(buffer));
            get().add(entry);
        }
        return get();
    }

    @Override
    public void resetChanged() {
        super.resetChanged();
        get().forEach(SerialField::resetChanged);
    }

    @Override
    public boolean isChanged() {
        return super.isChanged() || get().stream().anyMatch(SerialField::isChanged);
    }

    @Override
    public boolean isNetworkChanged() {
        return super.isNetworkChanged() || get().stream().anyMatch(SerialField::isNetworkChanged);
    }
}

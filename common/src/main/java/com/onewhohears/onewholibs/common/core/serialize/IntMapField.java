package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class IntMapField<VF extends SerialField<V>, V> extends SerialField<IntObjectMap<VF>> {

    private final Supplier<VF> valueFieldGen;

    public IntMapField(@NotNull String name, @NotNull IntObjectMap<VF> defaultValue,
                       @NotNull Supplier<VF> valueFieldGen) {
        super(name, defaultValue);
        this.valueFieldGen = valueFieldGen;
    }

    public void put(int key, V value) {
        if (get().containsKey(key)) {
            get().get(key).set(value);
        } else {
            VF valueField = valueFieldGen.get();
            valueField.set(value);
            valueField.setChanged();
            get().put(key, valueField);
        }
    }

    public boolean remove(int key) {
        boolean removed = get().remove(key) != null;
        if (removed) setChanged();
        return removed;
    }

    public void removeIf(Predicate<V> test) {
        int sizePre = get().size();
        get().entrySet().removeIf((entry) -> test.test(entry.getValue().get()));
        if (get().size() != sizePre) setChanged();
    }

    public void clear() {
        int sizePre = get().size();
        get().clear();
        if (get().size() != sizePre) setChanged();
    }

    public int size() {
        return get().size();
    }

    public boolean isEmpty() {
        return get().isEmpty();
    }

    public Collection<VF> values() {
        return get().values();
    }

    @Override
    protected JsonElement write(@NotNull IntObjectMap<VF> map) {
        JsonArray list = new JsonArray();
        map.forEach((key, value) -> {
            JsonObject entryJson = new JsonObject();
            entryJson.addProperty("key", key);
            entryJson.add("value", value.write(value.get()));
            list.add(entryJson);
        });
        return list;
    }

    @Override
    protected IntObjectMap<VF> read(@NotNull JsonElement valueJson) {
        get().clear();
        JsonArray list = valueJson.getAsJsonArray();
        for (int i = 0; i < list.size(); ++i) {
            JsonObject entryJson = list.get(i).getAsJsonObject();
            int key = entryJson.get("key").getAsInt();
            VF valueField = valueFieldGen.get();
            valueField.setNoCheck(valueField.read(entryJson.get("value")));
            get().put(key, valueField);
        }
        return get();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull IntObjectMap<VF> map, boolean encodeAll) {
        buffer.writeBoolean(encodeAll);
        buffer.writeInt(map.size());
        if (encodeAll) {
            map.forEach((key, value) -> {
                buffer.writeInt(key);
                value.write(buffer, value.get(), true);
            });
        } else {
            Set<Map.Entry<Integer, VF>> entries = map.entrySet();
            for (Map.Entry<Integer, VF> entry : entries) {
                buffer.writeInt(entry.getKey());
                if (entry.getValue().isNetworkChanged()) {
                    buffer.writeBoolean(true);
                    entry.getValue().write(buffer, entry.getValue().get(), false);
                } else {
                    buffer.writeBoolean(false);
                }
            }
        }
    }

    @Override
    protected IntObjectMap<VF> read(@NotNull FriendlyByteBuf buffer) {
        boolean encodeAll = buffer.readBoolean();
        int num = buffer.readInt();
        if (encodeAll) {
            get().clear();
            for (int i = 0; i < num; ++i) {
                int key = buffer.readInt();
                VF valueField = valueFieldGen.get();
                valueField.setNoCheck(valueField.read(buffer));
                get().put(key, valueField);
            }
        } else {
            Set<Integer> keys = new HashSet<>();
            for (int i = 0; i < num; ++i) {
                int key = buffer.readInt();
                keys.add(key);
                boolean includeValue = buffer.readBoolean();
                if (includeValue) {
                    if (get().containsKey(key)) {
                        get().get(key).read(buffer);
                    } else {
                        VF valueField = valueFieldGen.get();
                        valueField.setNoCheck(valueField.read(buffer));
                        get().put(key, valueField);
                    }
                }
            }
            get().entrySet().removeIf(entry -> !keys.contains(entry.getKey()));
        }
        return get();
    }

    @Override
    public void resetChanged() {
        super.resetChanged();
        get().forEach((key, value) -> value.resetChanged());
    }

    @Override
    public boolean isChanged() {
        return super.isChanged() || get().values().stream().anyMatch(SerialField::isChanged);
    }

    @Override
    public boolean isNetworkChanged() {
        return super.isNetworkChanged() || get().values().stream().anyMatch(SerialField::isNetworkChanged);
    }
}

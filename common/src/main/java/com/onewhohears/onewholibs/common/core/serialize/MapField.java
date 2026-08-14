package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MapField<KF extends SerialField<K>, VF extends SerialField<V>, K, V> extends SerialField<Map<K, VF>> {

    private final Supplier<KF> keyFieldGen;
    private final Supplier<VF> valueFieldGen;

    public MapField(@NotNull String name, @NotNull Map<K, VF> defaultValue,
                    @NotNull Supplier<KF> keyFieldGen, @NotNull Supplier<VF> valueFieldGen) {
        super(name, defaultValue);
        this.keyFieldGen = keyFieldGen;
        this.valueFieldGen = valueFieldGen;
    }

    public void put(K key, V value) {
        if (get().containsKey(key)) {
            get().get(key).set(value);
        } else {
            VF valueField = valueFieldGen.get();
            valueField.set(value);
            valueField.setChanged();
            get().put(key, valueField);
        }
    }

    public boolean remove(K key) {
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
    protected JsonElement write(@NotNull Map<K, VF> map) {
        JsonArray list = new JsonArray();
        KF keyField = keyFieldGen.get();
        map.forEach((key, value) -> {
            JsonObject entryJson = new JsonObject();
            entryJson.add("key", keyField.write(key));
            entryJson.add("value", value.write(value.get()));
            list.add(entryJson);
        });
        return list;
    }

    @Override
    protected Map<K, VF> read(@NotNull JsonElement valueJson) {
        get().clear();
        JsonArray list = valueJson.getAsJsonArray();
        KF keyField = keyFieldGen.get();
        for (int i = 0; i < list.size(); ++i) {
            JsonObject entryJson = list.get(i).getAsJsonObject();
            K key = keyField.read(entryJson.get("key"));
            VF valueField = valueFieldGen.get();
            valueField.setNoCheck(valueField.read(entryJson.get("value")));
            get().put(key, valueField);
        }
        return get();
    }

    @Override
    protected void write(@NotNull FriendlyByteBuf buffer, @NotNull Map<K, VF> map, boolean encodeAll) {
        if (SerialObject.DEBUG) {
            SerialObject.LOGGER.info("Writing Map Field: {} encodeAll: {}", getName(), encodeAll);
        }
        buffer.writeBoolean(encodeAll);
        KF keyField = keyFieldGen.get();
        buffer.writeInt(map.size());
        if (encodeAll) {
            map.forEach((key, value) -> {
                keyField.write(buffer, key, true);
                value.write(buffer, value.get(), true);
            });
        } else {
            Set<Map.Entry<K, VF>> entries = map.entrySet();
            for (Map.Entry<K, VF> entry : entries) {
                keyField.write(buffer, entry.getKey(), false);
                if (entry.getValue().isNetworkChanged()) {
                    buffer.writeBoolean(true);
                    entry.getValue().write(buffer, entry.getValue().get(), false);
                    if (SerialObject.DEBUG) {
                        SerialObject.LOGGER.info("{}: {}", entry.getKey(), entry.getValue().get());
                    }
                } else {
                    buffer.writeBoolean(false);
                }
            }
        }
    }

    @Override
    protected Map<K, VF> read(@NotNull FriendlyByteBuf buffer) {
        KF keyField = keyFieldGen.get();
        boolean encodeAll = buffer.readBoolean();
        if (SerialObject.DEBUG) {
            SerialObject.LOGGER.info("Reading Map Field: {} encodeAll: {}", getName(), encodeAll);
        }
        int num = buffer.readInt();
        if (encodeAll) {
            get().clear();
            for (int i = 0; i < num; ++i) {
                K key = keyField.read(buffer);
                VF valueField = valueFieldGen.get();
                valueField.setNoCheck(valueField.read(buffer));
                get().put(key, valueField);
            }
        } else {
            Set<K> keys = new HashSet<>();
            for (int i = 0; i < num; ++i) {
                K key = keyField.read(buffer);
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
                    if (SerialObject.DEBUG) {
                        SerialObject.LOGGER.info("{}: {}", key, get().get(key));
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

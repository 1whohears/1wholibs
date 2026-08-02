package com.onewhohears.onewholibs.common.core.serialize;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Stream;

public abstract class SerialObject {

    public static boolean DEBUG = false;
    protected static final Logger LOGGER = LogUtils.getLogger();

    private final Map<String,SerialField<?>> fields = new HashMap<>();
    private final Set<String> networkSyncs = new HashSet<>();

    protected <E extends SerialField<B>, B> E registerField(E field, boolean networkSync) {
        fields.put(field.getName(), field);
        if (networkSync) networkSyncs.add(field.getName());
        return field;
    }

    public final void loadSaveData(@NotNull JsonObject data) {
        fields.forEach((name, field) -> field.load(data));
    }

    @NotNull
    public final JsonObject getSaveData() {
        JsonObject data = new JsonObject();
        fields.forEach((name, field) -> field.save(data));
        return data;
    }

    public final void readPacket(@NotNull FriendlyByteBuf buffer) {
        long num = buffer.readLong();
        if (DEBUG) {
            LOGGER.info("READ PACKET {} fields: {}", getClass().getSimpleName(), num);
        }
        for (int i = 0; i < num; ++i) {
            String name = buffer.readUtf();
            fields.get(name).decode(buffer);
        }
    }

    public final void writePacket(@NotNull FriendlyByteBuf buffer, boolean encodeAll) {
        Stream<SerialField<?>> stream;
        if (encodeAll) {
            stream = fields.values().stream().filter(field ->
                    networkSyncs.contains(field.getName()));
        } else {
            stream = fields.values().stream().filter(field ->
                    field.isNetworkChanged() && networkSyncs.contains(field.getName()));
        }
        List<SerialField<?>> toEncode = stream.toList();
        long size = toEncode.size();
        buffer.writeLong(size);
        if (DEBUG) {
            LOGGER.info("WRITE PACKET {} fields: {} encodeAll: {}", getClass().getSimpleName(), size, encodeAll);
        }
        toEncode.forEach(field -> field.encode(buffer, encodeAll));
    }

    public final boolean isChanged() {
        return fields.values().stream().anyMatch(SerialField::isChanged);
    }

    public final boolean isNetworkChanged() {
        return fields.values().stream().anyMatch(field ->
                field.isChanged() && networkSyncs.contains(field.getName()));
    }

    public boolean isEqual(@NotNull SerialObject other) {
        return fields.values().stream().allMatch(field -> {
            if (!other.fields.containsKey(field.getName())) return false;
            return other.fields.get(field.getName()).isEqual(field.get());
        });
    }

    public void resetChanged() {
        fields.forEach((name, field) -> field.resetChanged());
    }
}

package com.onewhohears.onewholibs.data.jsonpreset;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface JsonPresetReloader<T extends JsonPresetStats> {

    /**
     * @param id same as name in the builder
     * @return an immutable preset stats object. null if it doesn't exist.
     */
    @Nullable
    default T get(String id) {
        if (id == null) return null;
        if (!has(id)) return null;
        return getPresetMap().get(id);
    }

    /**
     * the holder auto updates the stats when the world/assets are reloaded
     * @param id same as name in the builder
     * @return null if there is no preset with this id
     */
    @Nullable
    default PresetStatsHolder<T> getHolder(String id) {
        if (id == null) return null;
        if (!has(id)) return null;
        return new PresetStatsHolder<>(this, id);
    }

    default boolean has(String id) {
        return getPresetMap().containsKey(id);
    }

    T[] getAll();

    T[] getNewArray(int size);

    default public String[] getAllIds() {
        String[] names = new String[getAll().length];
        for (int i = 0; i < names.length; ++i)
            names[i] = getAll()[i].getId();
        return names;
    }

    int getReloads();

    default int getNum() {
        return getPresetMap().size();
    }

    Map<String, T> getPresetMap();

    Map<String, JsonPresetType> getTypeMap();

    @Nullable
    default T getFromJson(ResourceLocation key, JsonObject json) {
        if (!json.has("presetType")) return null;
        String presetType = json.get("presetType").getAsString();
        JsonPresetType type = getTypeMap().get(presetType);
        if (type == null) {
            getLogger().warn("ERROR: Preset Type {} has not been registered!", presetType);
            return null;
        }
        return type.createStats(key, json);
    }

    /**
     * to add a custom preset type, call this in the
     * {@link com.onewhohears.onewholibs.common.event.RegisterPresetTypesEvent} event.
     * that event gets called on every reload, so you can register the preset type,
     * and then this reload listener reads all the json files.
     */
    default void addPresetType(JsonPresetType type) {
        getTypeMap().put(type.getId(), type);
    }

    Logger getLogger();

    default void mergeCopyWithParentPresets() {
        getLogger().info("MERGING COPIES WITH PARENT PRESETS {}", getName());
        getPresetMap().forEach(this::mergeWithParent);
        getPresetMap().forEach((id, preset) -> {
            if (preset.isCopy()) getPresetMap().put(id, preset.getType()
                    .createStats(preset.getKey(), preset.copyJsonData()));
        });
    }

    default void mergeWithParent(String id, T preset) {
        if (!preset.isCopy() || preset.hasBeenMerged()) return;
        if (!has(preset.getCopyId())) {
            getLogger().warn("ERROR: Preset {} does not exist so {} can't be merged!", preset.getCopyId(), id);
            return;
        }
        T copy = get(preset.getCopyId());
        if (copy == null) {
            getLogger().warn("MERGE FAIL: {} can't merge because {} doesn't exist!", id, preset.getCopyId());
            return;
        }
        if (copy.isCopy() && !copy.hasBeenMerged())
            mergeWithParent(copy.getId(), copy);
        if (!preset.mergeWithParent(copy)) {
            getLogger().warn("MERGE FAIL: {} with {}", id, copy.getId());
            return;
        }
        getLogger().info("MERGED: {} with {}", id, copy.getId());
    }

    String getName();

    @SuppressWarnings("unchecked")
    default <K extends T> List<K> getPresetsOfType(JsonPresetType type) {
        List<K> presets = new ArrayList<>();
        getPresetMap().forEach((id, preset) -> {
            if (preset.getType().is(type))
                presets.add((K) preset);
        });
        return presets;
    }

    default void applyJson(ResourceLocation key, JsonObject json) {
        T data = getFromJson(key, json);
        if (data == null) {
            getLogger().error("ERROR: failed to parse preset {}", key.toString());
            return;
        }
        if (!getPresetMap().containsKey(data.getId())) getPresetMap().put(data.getId(), data);
        else {
            T otherData = getPresetMap().get(data.getId());
            if (data.getPriority() >= otherData.getPriority()) {
                getPresetMap().put(data.getId(), data);
                getLogger().debug("Preset {} is overriding {}!", key.toString(), otherData.getKey().toString());
            } else {
                getLogger().debug("Preset {} was overriden by {}.", key.toString(), otherData.getKey().toString());
            }
        }
    }

    default void sort(List<T> presets) {
        presets.sort(JsonPresetStats::compare);
    }

    default void sort(T[] presets) {
        Arrays.sort(presets, JsonPresetStats::compare);
    }
}

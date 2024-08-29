package com.onewhohears.onewholibs.data.jsonpreset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.util.UtilParse;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

/**
 * A json file from datapacks reader for 1whohears mod's preset system.
 * Can sort the presets, has a built-in preset inheritance system, and server-client syncing system
 * see {@link JsonPresetGenerator} for a way to generate json presets.
 * Your custom JsonPresetReloadListeners must be registered in
 * {@link com.onewhohears.onewholibs.common.event.GetJsonPresetListenersEvent}.
 * Child classes should have a static getInstance style method!
 * 
 * @author 1whohears
 * @param <T> the type of preset this reader builds from json files
 */
public abstract class JsonPresetReloadListener<T extends JsonPresetStats> extends SimpleJsonResourceReloadListener {
	
	protected final Logger LOGGER = LogUtils.getLogger();
	protected final Map<String, PresetStatsHolder<T>> presetMap = new HashMap<>();
	protected final Map<String, JsonPresetType> typeMap = new HashMap<>();
	protected boolean setup = false;
	
	public JsonPresetReloadListener(String directory) {
		super(UtilParse.GSON, directory);
	}
	/**
	 * @param id same as name in the builder
	 * @return an immutable preset stats object. null if it doesn't exist. 
	 */
	@Nullable
	public T get(String id) {
		if (id == null) return null;
		if (!has(id)) return null;
		return presetMap.get(id).get();
	}

	@Nullable
	public PresetStatsHolder<T> getHolder(String id) {
		if (id == null) return null;
		if (!has(id)) return null;
		return presetMap.get(id);
	}
	
	@Nullable
	public T getFromNbt(CompoundTag nbt) {
		if (nbt == null) return null;
		if (!nbt.contains("presetId")) return null;
		String presetId = nbt.getString("presetId");
		return get(presetId);
	}
	
	@Nullable
	public JsonPresetInstance<?> createInstanceFromNbt(CompoundTag nbt) {
		T stats = getFromNbt(nbt);
		if (stats == null) return null;
		return stats.createPresetInstance(nbt);
 	}
	
	public boolean has(String id) {
		return presetMap.containsKey(id);
	}

	/**
	 * @return an array containing all presets
	 */
	public abstract T[] getAll();
	
	public String[] getAllIds() {
		String[] names = new String[getAll().length];
		for (int i = 0; i < names.length; ++i) 
			names[i] = getAll()[i].getId();
		return names;
	}
	/**
	 * Clear the array created for {@link #getAll()}
	 */
	protected abstract void resetCache();
	
	public int getNum() {
		return presetMap.size();
	}
	
	public boolean isSetup() {
		return setup;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        LOGGER.info("APPLYING PRESETS TO COMMON CACHE {}", getName());
		setup = false;
		map.forEach((key, je) -> { try {
            LOGGER.info("ADD: {}", key.toString());
			JsonObject json = UtilParse.GSON.fromJson(je, JsonObject.class);
			T data = getFromJson(key, json);
			if (data == null) {
                LOGGER.error("ERROR: failed to parse preset {}", key.toString());
				return;
			}
			if (!presetMap.containsKey(data.getId())) presetMap.put(data.getId(), new PresetStatsHolder<>(data));
			else {
				T otherData = presetMap.get(data.getId()).get();
				if (data.getPriority() >= otherData.getPriority()) {
					presetMap.put(data.getId(), new PresetStatsHolder<>(data));
					LOGGER.debug("Preset {} is overriding {}!", key.toString(), otherData.getKey().toString());
				} else {
					LOGGER.debug("Preset {} was overriden by {}.", key.toString(), otherData.getKey().toString());
				}
			}
		} catch (Exception e) {
            LOGGER.error("ERROR: SKIPPING {} because {}", key.toString(), e.getMessage());
			e.printStackTrace();
		}});
		mergeCopyWithParentPresets();
		resetCache();
		setup = true;
	}
	
	protected void mergeCopyWithParentPresets() {
        LOGGER.info("MERGING COPIES WITH PARENT PRESETS {}", getName());
		presetMap.forEach((id, preset) -> mergeWithParent(id, preset.get()));
		presetMap.forEach((id, preset) -> { 
			if (preset.get().isCopy()) presetMap.put(id, new PresetStatsHolder<>(
					preset.get().getType().createStats(preset.get().getKey(), preset.get().copyJsonData())));
		});
	}
	
	protected void mergeWithParent(String id, T preset) {
		if (!preset.isCopy() || preset.hasBeenMerged()) return;
		if (!has(preset.getCopyId())) {
            LOGGER.warn("ERROR: Preset {} does not exist so {} can't be merged!", preset.getCopyId(), id);
			return;
		}
		T copy = get(preset.getCopyId());
		if (copy == null) {
            LOGGER.warn("MERGE FAIL: {} can't merge because {} doesn't exist!", id, preset.getCopyId());
			return;
		}
		if (copy.isCopy() && !copy.hasBeenMerged()) 
			mergeWithParent(copy.getId(), copy);
		if (!preset.mergeWithParent(copy)) {
            LOGGER.warn("MERGE FAIL: {} with {}", id, copy.getId());
			return;
		}
        LOGGER.info("MERGED: {} with {}", id, copy.getId());
	}
	
	@Nullable
	public T getFromJson(ResourceLocation key, JsonObject json) {
		if (!json.has("presetType")) return null;
		String presetType = json.get("presetType").getAsString();
		JsonPresetType type = typeMap.get(presetType);
		if (type == null) {
			LOGGER.warn("ERROR: Preset Type "+presetType+" has not been registered!");
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
	public void addPresetType(JsonPresetType type) {
		typeMap.put(type.getId(), type);
	}
	/**
	 * used to add a bunch of default preset types. 
	 * call {@link #addPresetType(JsonPresetType)} 
	 * in the event {@link com.onewhohears.onewholibs.common.event.RegisterPresetTypesEvent}
	 * to add custom preset types. 
	 */
	public abstract void registerDefaultPresetTypes();
	
	public void writeToBuffer(FriendlyByteBuf buffer) {
		buffer.writeInt(getNum());
		presetMap.forEach((id, preset) -> {
			buffer.writeUtf(preset.get().getKey().toString());
			buffer.writeUtf(preset.get().getJsonData().toString());
		});
	}
	
	public void readBuffer(FriendlyByteBuf buffer) {
        LOGGER.debug("RECEIVING DATA FROM SERVER {}", getName());
		setup = false;
		int length = buffer.readInt();
		for (int i = 0; i < length; ++i) {
			String key_string = buffer.readUtf();
			String json_string = buffer.readUtf();
			ResourceLocation key = new ResourceLocation(key_string);
			JsonObject json = UtilParse.GSON.fromJson(json_string, JsonObject.class);
			T data = getFromJson(key, json);
			if (data == null) continue;
            LOGGER.debug("ADD: {}", key.toString());
			presetMap.put(data.getId(), new PresetStatsHolder<>(data));
		}
		resetCache();
		setup = true;
	}
	
	public void sort(List<T> presets) {
		presets.sort(JsonPresetStats::compare);
	}
	
	public void sort(T[] presets) {
		Arrays.sort(presets, JsonPresetStats::compare);
	}
	
	@SuppressWarnings("unchecked")
	public <K extends T> List<K> getPresetsOfType(JsonPresetType type) {
		List<K> presets = new ArrayList<>();
		presetMap.forEach((id, preset) -> {
			if (preset.get().getType().is(type))
				presets.add((K) preset.get());
		});
		return presets;
	}

}

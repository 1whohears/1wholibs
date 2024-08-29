package com.onewhohears.onewholibs.data.jsonpreset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
public abstract class JsonPresetReloadListener<T extends JsonPresetStats> extends SimpleJsonResourceReloadListener implements JsonPresetReloader<T> {
	
	protected final Logger LOGGER = LogUtils.getLogger();
	protected final Map<String, T> presetMap = new HashMap<>();
	protected final Map<String, JsonPresetType> typeMap = new HashMap<>();
	protected boolean setup = false;
	private T[] allPresets;
	private int reloads = 0;
	
	public JsonPresetReloadListener(String directory) {
		super(UtilParse.GSON, directory);
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

	/**
	 * @return an array containing all presets
	 */
	public T[] getAll() {
		if (allPresets == null) {
			allPresets = getNewArray(getNum());
			AtomicInteger i = new AtomicInteger(0);
			presetMap.forEach((id, preset) -> allPresets[i.getAndIncrement()] = preset);
		}
		return allPresets;
	}

	@Override
	public abstract T[] getNewArray(int size);

	@Override
	public String[] getAllIds() {
		return JsonPresetReloader.super.getAllIds();
	}

	/**
	 * Clear the array created for {@link #getAll()}
	 */
	protected abstract void resetCache();
	
	public boolean isSetup() {
		return setup;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        getLogger().info("APPLYING PRESETS TO COMMON CACHE {}", getName());
		setup = false;
		presetMap.clear();
		map.forEach((key, je) -> { try {
            getLogger().info("ADD: {}", key.toString());
			JsonObject json = UtilParse.GSON.fromJson(je, JsonObject.class);
			applyJson(key, json);
		} catch (Exception e) {
            getLogger().error("ERROR: SKIPPING {} because {}", key.toString(), e.getMessage());
			e.printStackTrace();
		}});
		mergeCopyWithParentPresets();
		allPresets = null;
		resetCache();
		++reloads;
		setup = true;
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
			buffer.writeUtf(preset.getKey().toString());
			buffer.writeUtf(preset.getJsonData().toString());
		});
	}
	
	public void readBuffer(FriendlyByteBuf buffer) {
		getLogger().debug("RECEIVING DATA FROM SERVER {}", getName());
		setup = false;
		int length = buffer.readInt();
		for (int i = 0; i < length; ++i) {
			String key_string = buffer.readUtf();
			String json_string = buffer.readUtf();
			ResourceLocation key = new ResourceLocation(key_string);
			JsonObject json = UtilParse.GSON.fromJson(json_string, JsonObject.class);
			T data = getFromJson(key, json);
			if (data == null) continue;
			getLogger().debug("ADD: {}", key.toString());
			presetMap.put(data.getId(), data);
		}
		allPresets = null;
		resetCache();
		++reloads;
		setup = true;
	}

	public int getReloads() {
		return reloads;
	}

	@Override
	public int getNum() {
		return JsonPresetReloader.super.getNum();
	}

	@Override
	public Map<String, T> getPresetMap() {
		return presetMap;
	}

	@Override
	public Map<String, JsonPresetType> getTypeMap() {
		return typeMap;
	}

	@Override
	public @org.jetbrains.annotations.Nullable T getFromJson(ResourceLocation key, JsonObject json) {
		return JsonPresetReloader.super.getFromJson(key, json);
	}

	@Override
	public void addPresetType(JsonPresetType type) {
		JsonPresetReloader.super.addPresetType(type);
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	@Override
	public void mergeCopyWithParentPresets() {
		JsonPresetReloader.super.mergeCopyWithParentPresets();
	}

	@Override
	public void mergeWithParent(String id, T preset) {
		JsonPresetReloader.super.mergeWithParent(id, preset);
	}

	@Override
	public <K extends T> List<K> getPresetsOfType(JsonPresetType type) {
		return JsonPresetReloader.super.getPresetsOfType(type);
	}

	@Override
	public void applyJson(ResourceLocation key, JsonObject json) {
		JsonPresetReloader.super.applyJson(key, json);
	}

	@Override
	public void sort(List<T> presets) {
		JsonPresetReloader.super.sort(presets);
	}

	@Override
	public void sort(T[] presets) {
		JsonPresetReloader.super.sort(presets);
	}

	@Override
	public @org.jetbrains.annotations.Nullable T get(String id) {
		return JsonPresetReloader.super.get(id);
	}

	@Override
	public @org.jetbrains.annotations.Nullable PresetStatsHolder<T> getHolder(String id) {
		return JsonPresetReloader.super.getHolder(id);
	}

	@Override
	public boolean has(String id) {
		return JsonPresetReloader.super.has(id);
	}

	@Override
	public String getName() {
		return super.getName();
	}
}

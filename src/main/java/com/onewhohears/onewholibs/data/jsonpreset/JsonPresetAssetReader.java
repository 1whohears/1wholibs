package com.onewhohears.onewholibs.data.jsonpreset;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.util.UtilParse;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * a json file from asset pack reader for this mod's asset system.
 * see {@link JsonPresetGenerator} for a way to generate json assets.
 * 
 * @author 1whohears
 * @param <T> the type of preset this reader builds from json files
 */
public abstract class JsonPresetAssetReader<T extends JsonPresetStats> implements ResourceManagerReloadListener {
	
	protected final Logger LOGGER = LogUtils.getLogger();
	protected final Map<String, T> presetMap = new HashMap<>();
	protected final Map<String, JsonPresetType> typeMap = new HashMap<>();
	protected final String directory;
	
	public JsonPresetAssetReader(String directory) {
		this.directory = directory;
	}
	
	@Override
	public void onResourceManagerReload(ResourceManager manager) {
        LOGGER.info("RELOAD ASSETS: {}", directory);
		presetMap.clear();
		registerPresetTypes();
		manager.listResources(directory, (key) -> {return key.getPath().endsWith(".json");}).forEach((key, resource) -> {
			try {
				LOGGER.info("ADD: {}", key.toString());
				JsonObject json = UtilParse.GSON.fromJson(resource.openAsReader(), JsonObject.class);
				T data = getFromJson(key, json);
				if (data == null) {
					LOGGER.error("ERROR: failed to parse preset {}", key.toString());
					return;
				}
				if (!presetMap.containsKey(data.getId())) presetMap.put(data.getId(), data);
				else {
					T otherData = presetMap.get(data.getId());
					if (data.getPriority() >= otherData.getPriority()) {
						presetMap.put(data.getId(), data);
						LOGGER.debug("Preset {} is overriding {}!", key.toString(), otherData.getKey().toString());
					} else {
						LOGGER.debug("Preset {} was overriden by {}.", key.toString(), otherData.getKey().toString());
					}
				}
			} catch (Exception e) {
				LOGGER.error("ERROR: SKIPPING {} because {}", key.toString(), e.getMessage());
				e.printStackTrace();
			}
		});
		resetCache();
	}
	
	/**
	 * @param id same as name in the builder
	 * @return an immutable preset stats object. null if it doesn't exist. 
	 */
	@Nullable
	public T get(String id) {
		if (!presetMap.containsKey(id)) return null;
		return presetMap.get(id);
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
	
	protected void addPresetType(JsonPresetType type) {
		typeMap.put(type.getId(), type);
	}
	
	protected abstract void registerPresetTypes();
	
	public abstract T[] getAll();
	
	protected abstract void resetCache();

}

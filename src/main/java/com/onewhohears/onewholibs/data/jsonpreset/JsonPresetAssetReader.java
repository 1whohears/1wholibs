package com.onewhohears.onewholibs.data.jsonpreset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
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
public abstract class JsonPresetAssetReader<T extends JsonPresetStats> implements ResourceManagerReloadListener, JsonPresetReloader<T> {

	protected final Logger LOGGER = LogUtils.getLogger();
	protected final Map<String, T> presetMap = new HashMap<>();
	protected final Map<String, JsonPresetType> typeMap = new HashMap<>();
	protected boolean setup = false;
	private int reloads = 0;
	private T[] allPresets;
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
				applyJson(key, json);
			} catch (Exception e) {
				LOGGER.error("ERROR: SKIPPING {} because {}", key.toString(), e.getMessage());
				e.printStackTrace();
			}
		});
		mergeCopyWithParentPresets();
		resetCache();
		allPresets = null;
		++reloads;
	}
	
	protected abstract void registerPresetTypes();

	@Override
	public @Nullable T get(String id) {
		return JsonPresetReloader.super.get(id);
	}

	@Override
	public @Nullable PresetStatsHolder<T> getHolder(String id) {
		return JsonPresetReloader.super.getHolder(id);
	}

	@Override
	public boolean has(String id) {
		return JsonPresetReloader.super.has(id);
	}

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

	@Override
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
	public @Nullable T getFromJson(ResourceLocation key, JsonObject json) {
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

	protected abstract void resetCache();

	@Override
	public CompletableFuture<Void> reload(PreparationBarrier pStage, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler,
										  ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
		return ResourceManagerReloadListener.super.reload(pStage, pResourceManager, pPreparationsProfiler, pReloadProfiler, pBackgroundExecutor, pGameExecutor);
	}

	@Override
	public String getName() {
		return ResourceManagerReloadListener.super.getName();
	}

}

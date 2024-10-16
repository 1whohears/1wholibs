package com.onewhohears.onewholibs.data.jsonpreset;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.data.PackOutput;

/**
 * Use this to generate JSON preset files.
 * Call {@link JsonPresetGenerator#addPresetToGenerate(JsonPresetStats)} inside a
 * {@link JsonPresetGenerator#registerPresets()} override.
 * Use a {@link PresetBuilder} to make the presets to register.
 * See {@link JsonPresetReloadListener} for a way to read these JSON presets.
 *
 * @author 1whohears
 * @param <T> the type of preset this reader builds from JSON files
 */
public abstract class JsonPresetGenerator<T extends JsonPresetStats> implements DataProvider {

	protected final Logger LOGGER = LogUtils.getLogger();
	protected final PackOutput.PathProvider pathProvider;
	public final Map<ResourceLocation, T> GEN_MAP = new HashMap<>();

	/**
	 * For data pack data generation
	 */
	public JsonPresetGenerator(DataGenerator output, PackOutput packOutput, String kind) {
		this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, kind);
	}

	/**
	 * Override this method if you want to add your own default presets
	 */
	protected abstract void registerPresets();

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		GEN_MAP.clear();
		registerPresets();
		Set<ResourceLocation> set = Sets.newHashSet();
		Consumer<T> consumer = (preset) -> {
			LOGGER.debug("GENERATING: {}", preset.getKey().toString());
			if (!set.add(preset.getKey())) {
				throw new IllegalStateException("Duplicate Preset! " + preset.getKey());
			} else {
				Path path = pathProvider.json(preset.getKey());
                DataProvider.saveStable(cache, preset.getJsonData(), path);
            }
		};
		generatePresets(consumer);
		return null;
	}

	protected void generatePresets(Consumer<T> consumer) {
		GEN_MAP.forEach((key, preset) -> consumer.accept(preset));
	}

	public void addPresetToGenerate(T preset) {
		GEN_MAP.put(preset.getKey(), preset);
	}

}

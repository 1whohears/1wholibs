package com.onewhohears.onewholibs.data.jsonpreset;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * use this to generate json preset files. 
 * call {@link JsonPresetGenerator#addPresetToGenerate(JsonPresetStats)} inside a
 * {@link JsonPresetGenerator#registerPresets()} override.
 * use a {@link PresetBuilder} to make the presets to register.
 * see {@link JsonPresetReloadListener} for a way to read these json presets.
 * 
 * @author 1whohears
 * @param <T> the type of preset this reader builds from json files
 */
public abstract class JsonPresetGenerator<T extends JsonPresetStats> implements DataProvider {
	
	protected final Logger LOGGER = LogUtils.getLogger();
	protected final PackOutput.PathProvider pathProvider;
    public final Map<ResourceLocation, T> GEN_MAP = new HashMap<>();
    /**
     * for data pack data generation
     */
    public JsonPresetGenerator(PackOutput output, String kind) {
        this(output, kind, PackOutput.Target.DATA_PACK);
    }
    
    public JsonPresetGenerator(PackOutput output, String kind, PackOutput.Target target) {
		this.pathProvider = output.createPathProvider(target, kind);
	}
    /**
     * override this method if you want to add your own default presets
     */
    protected abstract void registerPresets();
	
	@Override
	public @NotNull CompletableFuture<?> run(CachedOutput cache) {
		GEN_MAP.clear();
		registerPresets();
		Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> futures = new ArrayList<>();
		Consumer<T> consumer = (preset) -> {
            LOGGER.debug("GENERATING: {}", preset.getKey().toString());
			if (!set.add(preset.getKey())) {
				throw new IllegalStateException("Duplicate Preset! " + preset.getKey());
			} else {
				Path path = pathProvider.json(preset.getKey());
                futures.add(DataProvider.saveStable(cache, preset.getJsonData(), path));
			}
		};
		generatePresets(consumer);
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}
	
	protected void generatePresets(Consumer<T> consumer) {
		GEN_MAP.forEach((key, preset) -> consumer.accept(preset));
	}
	
	public void addPresetToGenerate(T preset) {
		GEN_MAP.put(preset.getKey(), preset);
	}

}

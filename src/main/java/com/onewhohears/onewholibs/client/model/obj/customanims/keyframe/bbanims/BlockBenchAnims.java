package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.bbanims;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimParser;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimation;
import com.onewhohears.onewholibs.util.UtilParse;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Set;

public class BlockBenchAnims implements ResourceManagerReloadListener, KeyframeAnimParser {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static BlockBenchAnims instance;

    public static BlockBenchAnims get() {
        if (instance == null) instance = new BlockBenchAnims();
        return instance;
    }

    public static void close() {
        instance = null;
    }

    public static final String DIRECTORY = "bb_animations";
    public static final String ANIM_FILE_TYPE = ".json";
    public static final String COMPATIBLE_FORMAT_VERSIONS = "1.8.0";

    private static Map<String, BBAnim> animMap = new HashMap<>();

    private BlockBenchAnims() {}

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        LOGGER.info("RELOAD ASSETS: "+DIRECTORY);
        manager.listResources(DIRECTORY, (key) -> key.getPath().endsWith(ANIM_FILE_TYPE))
                .forEach((key, resource) -> {
            try {
                String name = new File(key.getPath()).getName().replace(ANIM_FILE_TYPE, "");
                if (animMap.containsKey(name)) {
                    LOGGER.warn("ERROR: Can't have 2 animations with the same name! {}", key);
                    return;
                }
                JsonObject json = UtilParse.GSON.fromJson(resource.openAsReader(), JsonObject.class);
                processBBJson(name, json);
            } catch (Exception e) {
                LOGGER.error("ERROR: SKIPPING {} because {}", key, e.getMessage());
                e.printStackTrace();
            }
        });
    }

    protected void processBBJson(String fileName, JsonObject json) throws Exception {
        //animMap.put(name, getBBAnimFromJson(json));
        String formatVersion = json.get("format_version").getAsString();
        if (!formatVersion.equals(COMPATIBLE_FORMAT_VERSIONS)) // TODO version checking system
            throw new InvalidPropertiesFormatException(
                    "Block Bench animation format version "+formatVersion+" is not supported!");
        JsonObject animations = json.get("animations").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> animJsons = animations.entrySet();
        for (Map.Entry<String, JsonElement> animJson : animJsons) {
            String animName = fileName + "." + animJson.getKey();
            animMap.put(animName, getBBAnimFromJson(animJson.getValue().getAsJsonObject()));
        }
    }

    public static BBAnim<?> getBBAnimFromJson(JsonObject json) throws Exception {

    }

    @Override
    public KeyframeAnimation getAnimation(String animation_id) {
        return animMap.get(animation_id);
    }
}

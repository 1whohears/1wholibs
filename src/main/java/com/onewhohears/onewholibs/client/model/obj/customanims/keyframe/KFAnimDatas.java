package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.util.UtilParse;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class KFAnimDatas implements ResourceManagerReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static KFAnimDatas instance;

    public static KFAnimDatas get() {
        if (instance == null) instance = new KFAnimDatas();
        return instance;
    }

    public static void close() {
        instance = null;
    }

    public static final String DIRECTORY = "animation_data";
    public static final String FILE_TYPE = ".json";

    private static Map<String, KFAnimData> animMap = new HashMap<>();

    private KFAnimDatas() {}

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        LOGGER.info("RELOAD ASSETS: "+DIRECTORY);
        manager.listResources(DIRECTORY, (key) -> key.getPath().endsWith(FILE_TYPE))
                .forEach((key, resource) -> {
            try {
                String name = new File(key.getPath()).getName().replace(FILE_TYPE, "");
                if (animMap.containsKey(name)) {
                    LOGGER.warn("ERROR: Can't have 2 animations with the same name! {}", key);
                    return;
                }
                JsonObject json = UtilParse.GSON.fromJson(resource.openAsReader(), JsonObject.class);

            } catch (Exception e) {
                LOGGER.error("ERROR: SKIPPING {} because {}", key, e.getMessage());
                e.printStackTrace();
            }
        });
    }
}

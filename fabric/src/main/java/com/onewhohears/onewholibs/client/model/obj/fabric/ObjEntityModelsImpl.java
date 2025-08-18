package com.onewhohears.onewholibs.client.model.obj.fabric;

import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import com.onewhohears.onewholibs.client.model.obj.ObjModelHandler;
import dev.felnull.specialmodelloader.api.SpecialModelLoaderAPI;
import dev.felnull.specialmodelloader.api.model.LoadedResource;
import dev.felnull.specialmodelloader.impl.model.obj.ObjUnbakedModelModel;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ObjEntityModelsImpl extends ObjEntityModels {

    public static ObjEntityModels createNew() {
        return new ObjEntityModelsImpl();
    }

    private final Map<String, ObjUnbakedModelModel> unbakedModels = new HashMap<>();
    private final Map<String, BakedModel> models = new HashMap<>();

    protected ObjEntityModelsImpl() {
    }

    @Override
    public ObjModelHandler createObjModelHandler(String id) {
        return new FabricObjModelHandler(id, getBakedModel(id), getUnbakedModel(id));
    }

    public ObjUnbakedModelModel getUnbakedModel(String name) {
        if (!unbakedModels.containsKey(name)) return unbakedModels.get(NULL_MODEL_NAME);
        return unbakedModels.get(name);
    }

    public BakedModel getBakedModel(String name) {
        if (!models.containsKey(name)) return models.get(NULL_MODEL_NAME);
        return models.get(name);
    }

    public boolean hasModel(String id) {
        return models.containsKey(id);
    }

    public void bakeModels() {
        LOGGER.info("BAKING OBJ MODELS");
        models.clear();
        unbakedModels.forEach((key, obj) -> {

            //BakedModel bakedModel = obj.bake();
            //models.put(key, bakedModel);
            //LOGGER.debug("BAKED {}", key);
        });
    }

    @Override
    protected void setupObjModels(ResourceManager manager) {
        readUnbakedModels(manager);
        bakeModels();
    }

    public void readUnbakedModels(ResourceManager manager) {
        unbakedModels.clear();
        manager.listResources(DIRECTORY, (key) -> key.getPath().endsWith(MODEL_FILE_TYPE))
                .forEach((key, resource) -> {
                    try {
                        String name = new File(key.getPath()).getName().replace(MODEL_FILE_TYPE, "");
                        if (unbakedModels.containsKey(name)) {
                            LOGGER.debug("The model {} is overriding {}!", key, unbakedModels.get(name));
                        }
                        LoadedResource lr = SpecialModelLoaderAPI.getInstance().loadResource(manager, key);
                        if (lr == null) {
                            throw new ResourceLocationException("Resource "+key+" doesn't exist!");
                        }
                        ObjUnbakedModelModel unbakedModel = (ObjUnbakedModelModel) SpecialModelLoaderAPI
                                .getInstance().makeModel(lr);
                        unbakedModels.put(name, unbakedModel);
                        LOGGER.debug("ADDING MODEL = {}", key);
                    } catch (Exception e) {
                        LOGGER.error("ERROR: SKIPPING {} because {}", key, e.getMessage());
                        e.printStackTrace();
                    }
                });
    }
}

package com.onewhohears.onewholibs.client.model.obj;

import com.google.common.collect.ImmutableBiMap;
import de.javagl.obj.Mtl;
import de.javagl.obj.MtlReader;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ObjEntityModelsImpl extends ObjEntityModels {

    private final Map<String, ObjUnbakedModel> unbakedModels = new HashMap<>();
    private final Map<String, ObjBakedModel> models = new HashMap<>();

    protected ObjEntityModelsImpl() {
    }

    @Override
    public ObjModelHandler createObjModelHandler(String id) {
        return new ObjModelHandlerImpl(id, getBakedModel(id), getUnbakedModel(id));
    }

    @Override
    public ObjUnbakedModel getUnbakedModel(String name) {
        if (!unbakedModels.containsKey(name)) return unbakedModels.get(NULL_MODEL_NAME);
        return unbakedModels.get(name);
    }

    @Override
    public ObjBakedModel getBakedModel(String name) {
        if (!models.containsKey(name)) return models.get(NULL_MODEL_NAME);
        return models.get(name);
    }

    public boolean hasModel(String id) {
        return models.containsKey(id);
    }

    @Override
    public void bakeModels() {
        LOGGER.info("BAKING OBJ MODELS");
        models.clear();
        unbakedModels.forEach((key, obj) -> {
            try {
                ObjBakedModel bakedModel = obj.bake();
                models.put(key, bakedModel);
                LOGGER.info("BAKED {}", key);
            } catch (Exception e) {
                LOGGER.error("ERROR: OBJ BAKING FAILED {} because {}", key, e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void setupObjModels(ResourceManager manager) {
        readUnbakedModels(manager);
        if (Platform.isFabric()) bakeModels();
    }

    public void readUnbakedModels(ResourceManager manager) {
        unbakedModels.clear();
        manager.listResources(DIRECTORY, (key) -> key.getPath().endsWith(MODEL_FILE_TYPE))
                .forEach((key, resource) -> {
                    try {
                        String name = new File(key.getPath()).getName().replace(MODEL_FILE_TYPE, "");
                        if (unbakedModels.containsKey(name)) {
                            LOGGER.info("The model {} is overriding {}!", key, unbakedModels.get(name));
                        }
                        Obj obj = ObjReader.read(resource.openAsReader());
                        Optional<Resource> mtlRes = manager.getResource(ResourceLocation.tryBuild(
                                key.getNamespace(), DIRECTORY+"/"+name+MATERIAL_FILE_TYPE));
                        Map<String, Mtl> mtl;
                        if (mtlRes.isPresent()) {
                            List<Mtl> mtlList = MtlReader.read(mtlRes.get().openAsReader());
                            mtlList.removeIf(m -> m.getMapKd() == null || m.getMapKd().equals("null"));
                            mtl = new ImmutableBiMap.Builder<String, Mtl>()
                                    .putAll(mtlList.stream().collect(Collectors.toMap(
                                            Mtl::getName, m -> m,
                                            (a, b) -> a // merge function in case of duplicates
                                    )))
                                    .build();
                        } else {
                            mtl = new ImmutableBiMap.Builder<String, Mtl>().build();
                        }
                        ObjUnbakedModel unbakedModel = new ObjUnbakedModel(key, obj, mtl);
                        unbakedModels.put(name, unbakedModel);
                        LOGGER.info("ADDING MODEL = {}", key);
                    } catch (Exception e) {
                        LOGGER.error("ERROR: OBJ PARSING FAILED {} because {}", key, e.getMessage());
                        e.printStackTrace();
                    }
                });
    }
}

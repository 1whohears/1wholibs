package com.onewhohears.onewholibs.client.obj.forge;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import com.onewhohears.onewholibs.client.model.obj.ObjModelHandler;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.obj.ObjModel.ModelSettings;
import net.minecraftforge.client.model.obj.ObjTokenizer;
import net.minecraftforge.client.model.renderable.CompositeRenderable;

/**
 * {@link ForgeObjModelHandler}
 * uses {@link CompositeRenderable} to render obj models.
 * ObjEntityModels is where all {@link CompositeRenderable} are baked and stored.
 * @author 1whohears
 */
public class ObjEntityModelsImpl extends ObjEntityModels {

    public static ObjEntityModels createNew() {
        return new ObjEntityModelsImpl();
    }

    private final Map<String, ObjModel> unbakedModels = new HashMap<>();
    private final Map<String, CompositeRenderable> models = new HashMap<>();

    protected ObjEntityModelsImpl() {
    }

    @Override
    public ObjModelHandler createObjModelHandler(String id) {
        return new ForgeObjModelHandler(id, getBakedModel(id), getUnbakedModel(id));
    }

    public ObjModel getUnbakedModel(String name) {
        if (!unbakedModels.containsKey(name)) return unbakedModels.get(NULL_MODEL_NAME);
        return unbakedModels.get(name);
    }

    public CompositeRenderable getBakedModel(String name) {
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
            StandaloneGeometryBakingContext ctx = StandaloneGeometryBakingContext.create(obj.modelLocation);
            CompositeRenderable comp = obj.bakeRenderable(ctx);
            models.put(key, comp);
            LOGGER.debug("BAKED {} {} {}", key, obj.getRootComponentNames().size(), obj.getConfigurableComponentNames());
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
                            LOGGER.debug("The model {} is overriding {}!", key, unbakedModels.get(name).modelLocation);
                        }
                        ObjTokenizer tokenizer = new ObjTokenizer(resource.open());
                        String mtlOverride = key.toString().replace(".obj", ".mtl");
                        ObjModel model = ObjModelParser.parse(tokenizer, new ModelSettings(key,
                                false, false, true, false, mtlOverride));
                        tokenizer.close();
                        unbakedModels.put(name, model);
                        LOGGER.debug("ADDING MODEL = {}", key);
                    } catch (Exception e) {
                        LOGGER.error("ERROR: SKIPPING {} because {}", key, e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

}

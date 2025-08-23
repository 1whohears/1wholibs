package com.onewhohears.onewholibs.client.model.obj.fabric;

import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import com.onewhohears.onewholibs.item.ObjModelItem;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BakedItemObjModels {

    private static final Map<String, BakedModel> models = new HashMap<>();

    public static BakedModel getItemModel(ObjModelItem objItem, ItemStack stack) {
        String preset = objItem.getPreset(stack);
        String modelId = objItem.getObjModelId(preset);
        if (models.containsKey(modelId)) return models.get(modelId);
        System.out.println("CREATING OBJ ITEM MODEL "+modelId);
        FabricObjModelHandler handler = (FabricObjModelHandler) ObjEntityModels.get().getObjModelHandler(modelId);
        BakedModel model = createItemModel(handler.getUnbakedModel(), handler.getBakedModel());
        models.put(modelId, model);
        return model;
    }

    public static BakedModel createItemModel(ObjUnbakedModel unbakedObjModel, ObjBakedModel bakedObjModel) {
        return new BakedItemObjModelFabric(unbakedObjModel, bakedObjModel);
    }

}

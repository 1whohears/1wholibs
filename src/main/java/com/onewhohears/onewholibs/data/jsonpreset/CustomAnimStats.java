package com.onewhohears.onewholibs.data.jsonpreset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewhohears.onewholibs.client.model.obj.customanims.CustomAnimsEntityModel;
import com.onewhohears.onewholibs.util.UtilParse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class CustomAnimStats<M extends CustomAnimsEntityModel<E>, E extends Entity> extends JsonPresetStats {

    private final String model_id;
    private final JsonArray custom_anims;
    private M model;

    public CustomAnimStats(ResourceLocation key, JsonObject json) {
        super(key, json);
        if (json.has("model_data")) {
            JsonObject model_data = json.get("model_data").getAsJsonObject();
            model_id = UtilParse.getStringSafe(model_data, "model_id", getId());
            if (model_data.has("custom_anims"))
                custom_anims = model_data.get("custom_anims").getAsJsonArray();
            else custom_anims = new JsonArray();
        } else {
            model_id = getId();
            custom_anims = new JsonArray();
        }
    }

    protected abstract M createModel();

    public M getModel() {
        if (model == null) model = createModel();
        return model;
    }

    public String getModelId() {
        return model_id;
    }

    public JsonArray getCustomAnims() {
        return custom_anims;
    }

}

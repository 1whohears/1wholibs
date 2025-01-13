package com.onewhohears.onewholibs.data.jsonpreset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimsEntityModel;
import com.onewhohears.onewholibs.util.UtilParse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class CustomAnimStats<M extends KeyframeAnimsEntityModel<E>, E extends Entity> extends JsonPresetStats {

    private final String model_id;
    private final JsonArray custom_anims;
    private final String[] keyframe_anims;
    private ObjEntityModels.ModelOverrides itemModelOverrides;
    private M model;

    public CustomAnimStats(ResourceLocation key, JsonObject json) {
        super(key, json);
        if (json.has("model_data")) {
            JsonObject model_data = json.get("model_data").getAsJsonObject();
            model_id = UtilParse.getStringSafe(model_data, "model_id", getId());
            if (model_data.has("custom_anims"))
                custom_anims = model_data.get("custom_anims").getAsJsonArray();
            else custom_anims = new JsonArray();
            keyframe_anims = UtilParse.getStringArraySafe(model_data, "anim_data");
        } else {
            model_id = getId();
            custom_anims = new JsonArray();
            keyframe_anims = new String[0];
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

    public String[] getKeyframeAnimIds() {
        return keyframe_anims;
    }

    public ObjEntityModels.ModelOverrides getItemModelOverrides() {
        if (itemModelOverrides == null) {
            if (getJsonData().has("item_model_overrides")) {
                itemModelOverrides = new ObjEntityModels.ModelOverrides(
                        UtilParse.getJsonSafe(getJsonData(), "item_model_overrides"));
            } else {
                itemModelOverrides = ObjEntityModels.NO_OVERRIDES;
            }
        }
        return itemModelOverrides;
    }

    public static abstract class CustomAnimStatsBuilder<B extends CustomAnimStatsBuilder<B>> extends PresetBuilder<B> {
        protected JsonObject getModelData() {
            if (!getData().has("model_data")) {
                getData().add("model_data", new JsonObject());
            }
            return getData().get("model_data").getAsJsonObject();
        }
        public CustomAnimStatsBuilder<B> setKFAnimDataIds(String model_id, String... animDataIds) {
            setKFAnimsDataIds(animDataIds);
            return setSimpleModelId(model_id);
        }
        public CustomAnimStatsBuilder<B> setKFAnimsDataIds(String... animDataIds) {
            getModelData().add("anim_data", UtilParse.stringArrayToJsonArray(animDataIds));
            return this;
        }
        public CustomAnimStatsBuilder<B> setCustomAnims(String model_id, JsonArray anims) {
            getModelData().add("custom_anims", anims);
            return setSimpleModelId(model_id);
        }
        public CustomAnimStatsBuilder<B> setCustomAnims(JsonArray anims) {
            return setCustomAnims(getPresetId(), anims);
        }
        public CustomAnimStatsBuilder<B> setSimpleModelId(String model_id) {
            getModelData().addProperty("model_id", model_id);
            return this;
        }
        public CustomAnimStatsBuilder<B> setItemModelOverrides(float scale_all, Vec3 scale,
                                                               Vec3 translate, Vec3 rotation) {
            JsonObject overrides = new JsonObject();
            overrides.addProperty("scale_all", scale_all);
            UtilParse.writeVec3(overrides, "scale", scale);
            UtilParse.writeVec3(overrides, "translate", translate);
            UtilParse.writeVec3(overrides, "rotation", rotation);
            getData().add("item_model_overrides", overrides);
            return this;
        }
        protected CustomAnimStatsBuilder(String namespace, String name, JsonPresetType type) {
            super(namespace, name, type);
        }
        protected CustomAnimStatsBuilder(String namespace, String name, JsonPresetType type, CustomAnimStats copy) {
            super(namespace, name, type, copy.getJsonData().deepCopy());
        }
    }

}

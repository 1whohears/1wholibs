package com.onewhohears.onewholibs.client.model.obj;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import com.onewhohears.onewholibs.util.UtilParse;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * @author 1whohears
 */
public abstract class ObjEntityModels implements ResourceManagerReloadListener {
	
	public static final Logger LOGGER = LogUtils.getLogger();
	private static ObjEntityModels instance;
	
	public static ObjEntityModels get() {
		return instance;
	}
	
	public static void close() {
		instance = null;
	}

    public static void register() {
        instance = createNew();
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, instance);
    }

    @ExpectPlatform
    public static ObjEntityModels createNew() {
        throw new AssertionError();
    }

	public static final String DIRECTORY = "models/entity";
	public static final String MODEL_FILE_TYPE = ".obj";
    public static final String MATERIAL_FILE_TYPE = ".mtl";
	public static final String OVERRIDE_FILE_TYPE = ".json";
	public static final String NULL_MODEL_NAME = "simple_test";
	
	private final Map<String, ModelOverrides> modelOverrides = new HashMap<>();
    private final Map<String, ObjModelHandler> modelHandlers = new HashMap<>();
	
	protected ObjEntityModels() {
	}

    public ObjModelHandler getObjModelHandler(String id) {
        if (!modelHandlers.containsKey(id)) modelHandlers.put(id, createObjModelHandler(id));
        return modelHandlers.get(id);
    }

    protected abstract ObjModelHandler createObjModelHandler(String id);

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        LOGGER.info("RELOAD ASSETS: "+DIRECTORY);
        readModelOverrides(manager);
        modelHandlers.clear();
        setupObjModels(manager);
    }

    protected abstract void setupObjModels(ResourceManager manager);
	
	public final static ModelOverrides NO_OVERRIDES = new ModelOverrides();
	
	public ModelOverrides getModelOverride(String name) {
		if (!modelOverrides.containsKey(name)) return NO_OVERRIDES;
		return modelOverrides.get(name);
	}
	
	public abstract boolean hasModel(String id);
	
	public void readModelOverrides(ResourceManager manager) {
		modelOverrides.clear();
		manager.listResources(DIRECTORY, (key) -> key.getPath().endsWith(OVERRIDE_FILE_TYPE))
				.forEach((key, resource) -> {
			try {
				String name = new File(key.getPath()).getName().replace(OVERRIDE_FILE_TYPE, "");
				if (modelOverrides.containsKey(name)) {
                    LOGGER.warn("ERROR: Can't have 2 model overrides with the same name! {}", key);
					return;
				}
				JsonObject json = UtilParse.GSON.fromJson(resource.openAsReader(), JsonObject.class);
				modelOverrides.put(name, new ModelOverrides(json));
                LOGGER.debug("ADDING OVERRIDE = {}", key);
			} catch (Exception e) {
                LOGGER.warn("ERROR: SKIPPING {} because {}", key.toString(), e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	public static class ModelOverrides {
		public float scale = 1;
		public float[] scale3d = {1, 1, 1};
		public Vector3f translate = new Vector3f();
		public float[] rotation = {0, 0, 0};
		private boolean none = false;
		public ModelOverrides(JsonObject json) {
			if (json.has("scale_all") && json.get("scale_all").isJsonPrimitive())
				scale = UtilParse.getFloatSafe(json, "scale_all", 1);
			if (json.has("scale")) {
				JsonElement scaleEle = json.get("scale");
				if (scaleEle.isJsonObject()) {
					Vec3 scaleVec = UtilParse.readVec3(json, "scale");
					scale3d[0] = (float)scaleVec.x();
					scale3d[1] = (float)scaleVec.y();
					scale3d[2] = (float)scaleVec.z();
				} else if (scaleEle.isJsonPrimitive()) {
					scale = json.get("scale").getAsFloat();
				}
			}
			if (json.has("scalex"))
				scale3d[0] = json.get("scalex").getAsFloat();
			if (json.has("scaley"))
				scale3d[1] = json.get("scaley").getAsFloat();
			if (json.has("scalez"))
				scale3d[2] = json.get("scalez").getAsFloat();
			if (json.has("translate") && json.get("translate").isJsonObject()) {
				translate = UtilParse.readVec3f(json, "translate");
			} else {
				if (json.has("translatex"))
					translate.add(json.get("translatex").getAsFloat(), 0, 0);
				if (json.has("translatey"))
					translate.add(0, json.get("translatey").getAsFloat(), 0);
				if (json.has("translatez"))
					translate.add(0, 0, json.get("translatez").getAsFloat());
			}
			if (json.has("rotation") && json.get("rotation").isJsonObject()) {
				Vec3 rotVec = UtilParse.readVec3(json, "rotation");
				rotation[0] = (float)rotVec.x();
				rotation[1] = (float)rotVec.y();
				rotation[2] = (float)rotVec.z();
			} else {
				if (json.has("rotationx"))
					rotation[0] = json.get("rotationx").getAsFloat();
				if (json.has("rotationy"))
					rotation[1] = json.get("rotationy").getAsFloat();
				if (json.has("rotationz"))
					rotation[2] = json.get("rotationz").getAsFloat();
			}
		}
		private ModelOverrides() {
			none = true;
		}
		public boolean isNone() {
			return none;
		}
		public void apply(PoseStack poseStack) {
			if (isNone()) return;
			applyRotation(poseStack);
			poseStack.translate(translate.x(), translate.y(), translate.z());
			poseStack.scale(scale * scale3d[0], scale * scale3d[1], scale * scale3d[2]);
		}
		public void applyNoTranslate(PoseStack poseStack) {
			if (isNone()) return;
			applyRotation(poseStack);
			poseStack.scale(scale * scale3d[0], scale * scale3d[1], scale * scale3d[2]);
		}
		public void applyRotation(PoseStack poseStack) {
			if (isNone()) return;
			if (rotation[0] != 0) poseStack.mulPose(Vector3f.XP.rotationDegrees(rotation[0]));
			if (rotation[1] != 0) poseStack.mulPose(Vector3f.YN.rotationDegrees(rotation[1]));
			if (rotation[2] != 0) poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation[2]));
		}
	}

}

package com.onewhohears.onewholibs.client.model.obj;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import com.onewhohears.onewholibs.util.UtilParse;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.obj.ObjModel.ModelSettings;
import net.minecraftforge.client.model.obj.ObjTokenizer;
import net.minecraftforge.client.model.renderable.CompositeRenderable;

/**
 * {@link ObjEntityModel} uses {@link CompositeRenderable} to render obj models.
 * ObjEntityModels is where all {@link CompositeRenderable} are baked and stored.
 * @author 1whohears
 */
public class ObjEntityModels implements ResourceManagerReloadListener {
	
	private static final Logger LOGGER = LogUtils.getLogger();
	private static ObjEntityModels instance;
	
	public static ObjEntityModels get() {
		if (instance == null) instance = new ObjEntityModels();
		return instance;
	}
	
	public static void close() {
		instance = null;
	}
	
	public static final String DIRECTORY = "models/entity";
	public static final String MODEL_FILE_TYPE = ".obj";
	public static final String OVERRIDE_FILE_TYPE = ".json";
	public static final String NULL_MODEL_NAME = "simple_test";
	
	private final Map<String, ModelOverrides> modelOverrides = new HashMap<>();
	private final Map<String, ObjModel> unbakedModels = new HashMap<>();
	private final Map<String, CompositeRenderable> models = new HashMap<>();
	
	private ObjEntityModels() {
	}
	
	public ObjModel getUnbakedModel(String name) {
		if (!unbakedModels.containsKey(name)) return unbakedModels.get(NULL_MODEL_NAME);
		return unbakedModels.get(name);
	}
	
	public static ModelOverrides NO_OVERRIDES = new ModelOverrides();
	
	public ModelOverrides getModelOverride(String name) {
		if (!modelOverrides.containsKey(name)) return NO_OVERRIDES;
		return modelOverrides.get(name);
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
	public void onResourceManagerReload(ResourceManager manager) {
		LOGGER.info("RELOAD ASSETS: "+DIRECTORY);
		readUnbakedModels(manager);
		readModelOverrides(manager);
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
			if (json.has("scale")) scale = json.get("scale").getAsFloat();
			if (json.has("scalex")) scale3d[0] = json.get("scalex").getAsFloat();
			if (json.has("scaley")) scale3d[1] = json.get("scaley").getAsFloat(); 
			if (json.has("scalez")) scale3d[2] = json.get("scalez").getAsFloat();
			if (json.has("translatex")) translate.add(json.get("translatex").getAsFloat(), 0, 0);
			if (json.has("translatey")) translate.add(0, json.get("translatey").getAsFloat(), 0); 
			if (json.has("translatez")) translate.add(0, 0, json.get("translatez").getAsFloat());
			if (json.has("rotationx")) rotation[0] = json.get("rotationx").getAsFloat();
			if (json.has("rotationy")) rotation[1] = json.get("rotationy").getAsFloat(); 
			if (json.has("rotationz")) rotation[2] = json.get("rotationz").getAsFloat();
		}
		private ModelOverrides() {
			none = true;
		}
		public boolean isNone() {
			return none;
		}
		public void apply(PoseStack poseStack) {
			if (isNone()) return;
			if (rotation[0] != 0) poseStack.mulPose(Vector3f.XP.rotationDegrees(rotation[0]));
			if (rotation[1] != 0) poseStack.mulPose(Vector3f.YN.rotationDegrees(rotation[1]));
			if (rotation[2] != 0) poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation[2]));
			poseStack.translate(translate.x(), translate.y(), translate.z());
			poseStack.scale(scale * scale3d[0], scale * scale3d[1], scale * scale3d[2]);
		}
		public void applyNoTranslate(PoseStack poseStack) {
			if (isNone()) return;
			if (rotation[0] != 0) poseStack.mulPose(Vector3f.XP.rotationDegrees(rotation[0]));
			if (rotation[1] != 0) poseStack.mulPose(Vector3f.YN.rotationDegrees(rotation[1]));
			if (rotation[2] != 0) poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation[2]));
			poseStack.scale(scale * scale3d[0], scale * scale3d[1], scale * scale3d[2]);
		}
	}

}

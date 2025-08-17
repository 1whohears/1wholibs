package com.onewhohears.onewholibs.client.model.obj;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels.ModelOverrides;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;

import java.util.HashMap;
import java.util.Map;

/**
 * See {@link com.onewhohears.onewholibs.client.renderer.RendererObjEntity}.
 * An obj and mtl file with the same name as {@link #modelId} must be put in
 * //assets/[mod_id]/models/entity/[{@link #modelId}].obj
 * @author 1whohears
 */
public class ObjEntityModelForge<T extends Entity> implements ObjEntityModel<T> {
	
	public final String modelId;

	private final Map<String, Matrix4f> transforms = new HashMap<>();
	
	private CompositeRenderable model;
	private ModelOverrides modelOverride;
	
	public ObjEntityModelForge(String modelId) {
		this.modelId = modelId;
	}

    public void renderModel(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks,
                            int lightmap, int overlay, Map<String, Matrix4f> transforms) {
        getModel().render(poseStack, bufferSource, getTextureRenderTypeLookup(entity),
                lightmap, overlay, partialTicks,
                getComponentTransforms(entity, partialTicks));
    }

	public CompositeRenderable getModel() {
		if (model == null) model = ObjEntityModels.get().getBakedModel(modelId);
		return model;
	}

    @Override
    public ModelOverrides getModelOverride() {
        if (modelOverride == null) modelOverride = ObjEntityModels.get().getModelOverride(modelId);
        return modelOverride;
    }
	
	protected Transforms getComponentTransforms(T entity, float partialTicks) {
		transforms.clear();
		addComponentTransforms(transforms, entity, partialTicks);
		if (transforms.isEmpty()) return Transforms.EMPTY;
		return Transforms.of(ImmutableMap.<String,Matrix4f>builder().putAll(transforms).build());
	}

    @Override
    public void addComponentTransforms(Map<String, Matrix4f> transforms, T entity, float partialTicks) {

    }
	
	protected ITextureRenderTypeLookup getTextureRenderTypeLookup(T entity) {
		return RenderType::entityTranslucent;
	}

    @Override
    public String getModelId() {
        return modelId;
    }
}

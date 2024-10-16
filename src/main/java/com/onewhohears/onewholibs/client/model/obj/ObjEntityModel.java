package com.onewhohears.onewholibs.client.model.obj;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels.ModelOverrides;
import com.onewhohears.onewholibs.util.math.UtilAngles;
import com.onewhohears.onewholibs.util.math.UtilGeometry;

import com.onewhohears.onewholibs.util.math.VectorUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * See {@link com.onewhohears.onewholibs.client.renderer.RendererObjEntity}.
 * An obj and mtl file with the same name as {@link #modelId} must be put in
 * //assets/[mod_id]/models/entity/[{@link #modelId}].obj
 * @author 1whohears
 */
public class ObjEntityModel<T extends Entity> {

	public static final Matrix4f INVISIBLE = new Matrix4f().scaling(0, 0, 0);
	
	public final String modelId;

	private final Map<String, Matrix4f> transforms = new HashMap<>();
	
	private CompositeRenderable model;
	private ModelOverrides modelOverride;
	
	public ObjEntityModel(String modelId) {
		this.modelId = modelId;
	}
	
	public void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, int lightmap, float partialTicks) {
		rotate(entity, partialTicks, poseStack);
		handleGlobalOverrides(entity, partialTicks, poseStack);
		getModel().render(poseStack, bufferSource, getTextureRenderTypeLookup(entity), 
				getLight(entity, lightmap), getOverlay(entity), partialTicks, 
				getComponentTransforms(entity, partialTicks));
	}

	protected void rotate(T entity, float partialTicks, PoseStack poseStack) {
		Vector3f pivot = getGlobalPivot();
		// Replace YN and XP with VectorUtils constants
		Quaternionf yRot = VectorUtils.rotationQuaternion(VectorUtils.NEGATIVE_Y, entity.getViewYRot(partialTicks));
		Quaternionf xRot = VectorUtils.rotationQuaternion(VectorUtils.POSITIVE_X, entity.getViewXRot(partialTicks));

		if (!UtilGeometry.isZero(pivot)) {
			if (globalRotateY()) poseStack.mulPoseMatrix(UtilAngles.pivotInvRot(pivot, yRot));
			if (globalRotateX()) poseStack.mulPoseMatrix(UtilAngles.pivotInvRot(pivot, xRot));
		} else {
			if (globalRotateY()) poseStack.mulPose(yRot);
			if (globalRotateX()) poseStack.mulPose(xRot);
		}
	}
	
	protected void handleGlobalOverrides(T entity, float partialTicks, PoseStack poseStack) {
		Vector3f pivot = getGlobalPivot();
		if (!UtilGeometry.isZero(pivot)) poseStack.translate(pivot.x(), pivot.y(), pivot.z());
		getModelOverride().applyNoTranslate(poseStack);
	}
	
	public CompositeRenderable getModel() {
		if (model == null) model = ObjEntityModels.get().getBakedModel(modelId);
		return model;
	}
	
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

	protected void addComponentTransforms(Map<String, Matrix4f> transforms, T entity, float partialTicks) {

	}
	
	protected ITextureRenderTypeLookup getTextureRenderTypeLookup(T entity) {
		return RenderType::entityTranslucent;
	}
	
	protected int getLight(T entity, int lightmap) {
		return lightmap;
	}
	
	protected int getOverlay(T entity) {
		return OverlayTexture.NO_OVERLAY;
	}
	
	public Vector3f getGlobalPivot() {
		return getModelOverride().translate;
	}

	public boolean globalRotateX() {
		return true;
	}

	public boolean globalRotateY() {
		return true;
	}
	
}

package com.onewhohears.onewholibs.client.model.obj;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.onewhohears.onewholibs.util.math.UtilAngles;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import com.onewhohears.onewholibs.util.math.VectorUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class ObjEntityModel<T extends Entity> {

	public static final Matrix4f INVISIBLE = new Matrix4f().scaling(0, 0, 0);

	public final String modelId;

	private final Map<String, Matrix4f> transforms = new HashMap<>();

	private CompositeRenderable model;
	private ObjEntityModels.ModelOverrides modelOverride;

	public ObjEntityModel(String modelId) {
		this.modelId = modelId;
	}

	public void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, int lightmap, float partialTicks) {
		rotate(entity, partialTicks, poseStack);
		handleGlobalOverrides(entity, partialTicks, poseStack);

		CompositeRenderable modelToRender = getModel();
		if (modelToRender != null) {
			modelToRender.render(poseStack, bufferSource, getTextureRenderTypeLookup(entity),
					getLight(entity, lightmap), getOverlay(entity), partialTicks,
					getComponentTransforms(entity, partialTicks));
		} else {
			// Log or handle missing model
			System.err.println("Error: Model is null, cannot render entity " + entity.getName().getString());
		}
	}


	protected void rotate(T entity, float partialTicks, PoseStack poseStack) {
		Vector3f pivot = getGlobalPivot();
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
		if (model == null) {
			model = ObjEntityModels.get().getBakedModel(modelId);
			if (model == null) {
				// Log error if the model is not found
				System.err.println("Error: Baked model for '" + modelId + "' not found.");
			}
		}
		return model;
	}


	public ObjEntityModels.ModelOverrides getModelOverride() {
		if (modelOverride == null) modelOverride = ObjEntityModels.get().getModelOverride(modelId);
		return modelOverride;
	}

	protected CompositeRenderable.Transforms getComponentTransforms(T entity, float partialTicks) {
		transforms.clear();
		addComponentTransforms(transforms, entity, partialTicks);
		if (transforms.isEmpty()) return CompositeRenderable.Transforms.EMPTY;
		return CompositeRenderable.Transforms.of(ImmutableMap.<String,Matrix4f>builder().putAll(transforms).build());
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
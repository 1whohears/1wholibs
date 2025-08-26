package com.onewhohears.onewholibs.client.model.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels.ModelOverrides;
import com.onewhohears.onewholibs.util.math.*;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * See {@link com.onewhohears.onewholibs.client.renderer.RendererObjEntity}.
 * An obj and mtl file with the same name as {@link #modelId} must be put in
 * //assets/[mod_id]/models/entity/[{@link #modelId}].obj
 * @author 1whohears
 */
public class ObjEntityModel<T extends Entity> {
	
	public static final Mat4f INVISIBLE = Mat4f.createScaleMatrix(0, 0, 0);
	
	public final String modelId;

	private final Map<String, Mat4f> transforms = new HashMap<>();

	private ModelOverrides modelOverride;
    private ObjModelHandler modelHandler;
	
	public ObjEntityModel(String modelId) {
		this.modelId = modelId;
	}
	
	public void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, int lightmap, float partialTicks) {
		rotate(entity, partialTicks, poseStack);
		handleGlobalOverrides(entity, partialTicks, poseStack);
        transforms.clear();
        addComponentTransforms(transforms, entity, partialTicks);
		getObjModelHandler().render(poseStack, bufferSource, partialTicks,
                getLight(entity, lightmap), getOverlay(entity), transforms,
                getTextureRenderTypeLookup(entity));
	}

    protected ObjModelHandler getObjModelHandler() {
        if (modelHandler == null) modelHandler = ObjEntityModels.get().getObjModelHandler(modelId);
        return modelHandler;
    }
	
	protected void rotate(T entity, float partialTicks, PoseStack poseStack) {
		Vec3f pivot = getGlobalPivot();
		QuaternionF yRot = Vec3f.YN.rotationDegrees(entity.getViewYRot(partialTicks));
		QuaternionF xRot = Vec3f.XP.rotationDegrees(entity.getViewXRot(partialTicks));
		if (!UtilGeometry.isZero(pivot)) {
			if (globalRotateY()) poseStack.mulPoseMatrix(UtilAngles.pivotInvRot(pivot, yRot).convert());
			if (globalRotateX()) poseStack.mulPoseMatrix(UtilAngles.pivotInvRot(pivot, xRot).convert());
		} else {
			if (globalRotateY()) poseStack.mulPose(yRot.convert());
			if (globalRotateX()) poseStack.mulPose(xRot.convert());
		}
	}
	
	protected void handleGlobalOverrides(T entity, float partialTicks, PoseStack poseStack) {
		Vec3f pivot = getGlobalPivot();
		if (!UtilGeometry.isZero(pivot)) poseStack.translate(pivot.x(), pivot.y(), pivot.z());
		getModelOverride().applyNoTranslate(poseStack);
	}
	
	public ModelOverrides getModelOverride() {
		if (modelOverride == null) modelOverride = ObjEntityModels.get().getModelOverride(modelId);
		return modelOverride;
	}

	protected void addComponentTransforms(Map<String, Mat4f> transforms, T entity, float partialTicks) {

	}
	
	protected Function<ResourceLocation, RenderType> getTextureRenderTypeLookup(T entity) {
		return RenderType::entityTranslucent;
	}
	
	protected int getLight(T entity, int lightmap) {
		return lightmap;
	}
	
	protected int getOverlay(T entity) {
		return OverlayTexture.NO_OVERLAY;
	}
	
	public Vec3f getGlobalPivot() {
		return getModelOverride().translate;
	}

	public boolean globalRotateX() {
		return true;
	}

	public boolean globalRotateY() {
		return true;
	}
	
}

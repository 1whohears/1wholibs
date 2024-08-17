package com.onewhohears.onewholibs.client.renderer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModel;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Use this renderer to put obj models over entities.
 * See {@link ObjEntityModel}. Register this renderer in the
 * {@link net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers} event.
 * @author 1whohears
 */
public class RendererObjEntity<T extends Entity> extends EntityRenderer<T> {
	
	protected final ObjEntityModel<T> model;
	
	public RendererObjEntity(Context ctx, ObjEntityModel<T> model) {
		super(ctx);
		this.model = model;
	}
	
	protected RendererObjEntity(Context ctx) {
		this(ctx, null);
	}
	
	@Override
	public void render(T entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int lightmap) {
		poseStack.pushPose();
		getModel(entity).render(entity, poseStack, bufferSource, lightmap, partialTicks);
		poseStack.popPose();
		super.render(entity, yaw, partialTicks, poseStack, bufferSource, lightmap);
	}
	
	protected ObjEntityModel<T> getModel(T entity) {
		return model;
	}
	
	@Nullable
	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return null;
	}

}

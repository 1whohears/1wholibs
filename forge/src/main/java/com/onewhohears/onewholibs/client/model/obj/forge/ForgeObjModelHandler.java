package com.onewhohears.onewholibs.client.model.obj.forge;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.onewhohears.onewholibs.client.model.obj.ObjModelHandler;
import com.onewhohears.onewholibs.mixin.ObjModelAccess;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class ForgeObjModelHandler implements ObjModelHandler {

    @NotNull private final String modelId;
    @NotNull private final CompositeRenderable model;
    @NotNull private final ObjModel unbakedModel;
    @Nullable private Vec3 size = null;
    @Nullable private Vec3 center = null;

    public ForgeObjModelHandler(@NotNull String modelId, @NotNull CompositeRenderable model,
                                @NotNull ObjModel unbakedModel) {
        this.modelId = modelId;
        this.model = model;
        this.unbakedModel = unbakedModel;
    }

    @Override
    public @NotNull String getModelId() {
        return modelId;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks,
                       int lightmap, int overlay, Map<String, Matrix4f> transforms,
                       Function<ResourceLocation, RenderType> renderType) {
        model.render(poseStack, bufferSource,
                renderType::apply,
                lightmap, overlay, partialTicks,
                getComponentTransforms(transforms));
    }

    @Override
    public @NotNull Vec3 getSize() {
        if (size == null) {
            Vec3[] sizeCenter = UtilGeometry.getSizeCenter(((ObjModelAccess)unbakedModel).getPositions());
            size = sizeCenter[0];
            center = sizeCenter[1];
        }
        return size;
    }

    @Override
    public @NotNull Vec3 getCenter() {
        if (center == null) {
            Vec3[] sizeCenter = UtilGeometry.getSizeCenter(((ObjModelAccess)unbakedModel).getPositions());
            size = sizeCenter[0];
            center = sizeCenter[1];
        }
        return center;
    }

    protected CompositeRenderable.Transforms getComponentTransforms(Map<String, Matrix4f> transforms) {
        if (transforms.isEmpty()) return CompositeRenderable.Transforms.EMPTY;
        return CompositeRenderable.Transforms.of(ImmutableMap.<String,Matrix4f>builder().putAll(transforms).build());
    }
}

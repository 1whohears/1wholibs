package com.onewhohears.onewholibs.client.model.obj.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.onewhohears.onewholibs.client.model.obj.ObjModelHandler;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import de.javagl.obj.FloatTuple;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class FabricObjModelHandler implements ObjModelHandler {

    @NotNull private final String modelId;
    @NotNull private final ObjBakedModel bakedModel;
    @NotNull private final ObjUnbakedModel unbakedModel;
    @Nullable private Vec3 size = null;
    @Nullable private Vec3 center = null;

    public FabricObjModelHandler(@NotNull String modelId, @NotNull ObjBakedModel bakedModel,
                                 @NotNull ObjUnbakedModel unbakedModel) {
        this.modelId = modelId;
        this.bakedModel = bakedModel;
        this.unbakedModel = unbakedModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks,
                       int lightmap, int overlay, Map<String, Matrix4f> transforms,
                       Function<ResourceLocation, RenderType> renderType) {
        bakedModel.render(poseStack, bufferSource, renderType,
                lightmap, overlay, partialTicks, transforms);
    }

    @Override
    public @NotNull String getModelId() {
        return modelId;
    }

    @Override
    public @NotNull Vec3 getSize() {
        if (size == null) calcSizeCenter();
        return size;
    }

    @Override
    public @NotNull Vec3 getCenter() {
        if (center == null) calcSizeCenter();
        return center;
    }

    private void calcSizeCenter() {
        int numV = unbakedModel.getObj().getNumVertices();
        Vec3[] vertices = new Vec3[numV];
        for (int i = 0; i < vertices.length; ++i) {
            FloatTuple ft = unbakedModel.getObj().getVertex(i);
            vertices[i] = new Vec3(ft.getX(), ft.getY(), ft.getZ());
        }
        Vec3[] sizeCenter = UtilGeometry.getSizeCenter(vertices);
        size = sizeCenter[0];
        center = sizeCenter[1];
    }
}

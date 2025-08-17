package com.onewhohears.onewholibs.client.obj.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.onewhohears.onewholibs.client.model.obj.ObjModelHandler;
import com.onewhohears.onewholibs.mixin.ObjUnbakedModelModelAccess;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import de.javagl.obj.FloatTuple;
import dev.felnull.specialmodelloader.impl.model.obj.ObjUnbakedModelModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class FabricObjModelHandler implements ObjModelHandler {

    @NotNull private final String modelId;
    @NotNull private final BakedModel bakedModel;
    @NotNull private final ObjUnbakedModelModel unbakedModel;
    @Nullable private Vec3 size = null;
    @Nullable private Vec3 center = null;

    public FabricObjModelHandler(@NotNull String modelId, @NotNull BakedModel bakedModel,
                                 @NotNull ObjUnbakedModelModel unbakedModel) {
        this.modelId = modelId;
        this.bakedModel = bakedModel;
        this.unbakedModel = unbakedModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks,
                       int lightmap, int overlay, Map<String, Matrix4f> transforms,
                       Function<ResourceLocation, RenderType> renderType) {
        // FIXME render a baked model???
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
        int numV = ((ObjUnbakedModelModelAccess)unbakedModel).getObj().getNumVertices();
        Vec3[] vertices = new Vec3[numV];
        for (int i = 0; i < vertices.length; ++i) {
            FloatTuple ft = ((ObjUnbakedModelModelAccess) unbakedModel).getObj().getVertex(i);
            vertices[i] = new Vec3(ft.getX(), ft.getY(), ft.getZ());
        }
        Vec3[] sizeCenter = UtilGeometry.getSizeCenter(vertices);
        size = sizeCenter[0];
        center = sizeCenter[1];
    }
}

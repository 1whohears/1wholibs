package com.onewhohears.onewholibs.client.model.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public interface ObjModelHandler {
    @NotNull String getModelId();
    void render(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks,
                int lightmap, int overlay, Map<String, Matrix4f> transforms,
                Function<ResourceLocation, RenderType> renderType);
    Vec3 getSize();
    Vec3 getCenter();
}

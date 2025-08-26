package com.onewhohears.onewholibs.client.model.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.onewhohears.onewholibs.util.math.Mat4f;
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
                int lightmap, int overlay, Map<String, Mat4f> transforms,
                Function<ResourceLocation, RenderType> renderType);
    @NotNull Vec3 getSize();
    @NotNull Vec3 getCenter();
}

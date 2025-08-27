package com.onewhohears.onewholibs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.onewhohears.onewholibs.client.renderer.RendererObjModelItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {
    @Inject(method = "renderByItem", at = @At("TAIL"))
    private static void onewholibs_renderObjModelItem(CallbackInfo info,
                                                      @Local(argsOnly = true) ItemStack itemStack,
                                                      @Local(argsOnly = true) ItemTransforms.TransformType transformType,
                                                      @Local(argsOnly = true) PoseStack poseStack,
                                                      @Local(argsOnly = true) MultiBufferSource multiBufferSource,
                                                      @Local(argsOnly = true, ordinal = 0) int i,
                                                      @Local(argsOnly = true, ordinal = 1) int j) {
        RendererObjModelItems.get().renderByItem(itemStack, transformType, poseStack, multiBufferSource, i, j);
    }
}

package com.onewhohears.onewholibs.mixin;

import net.minecraftforge.client.model.renderable.CompositeRenderable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CompositeRenderable.PartBuilder.class)
public class CompositeRenderableBuilderMixin {

    @ModifyArg(method = "child", at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/client/model/renderable/CompositeRenderable$Component;" +
                    "<init>(Ljava/lang/String;)V"), remap = false)
    private String onewholibs_changeComponentName(String old) {
        String[] split = old.split("/");
        return split[split.length-1];
    }

}

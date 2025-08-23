package com.onewhohears.onewholibs.mixin;

import com.onewhohears.onewholibs.client.model.obj.fabric.BakedItemObjModels;
import com.onewhohears.onewholibs.item.ObjModelItem;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModelShaper.class)
public class ItemModelShaperMixin {
    @Inject(method = "getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At(value = "RETURN"), cancellable = true)
    private static void onewholibs_useObjModelItem(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        if (!(stack.getItem() instanceof ObjModelItem objItem)) {
            cir.setReturnValue(cir.getReturnValue());
            return;
        }
        cir.setReturnValue(BakedItemObjModels.getItemModel(objItem, stack));
    }
}

package com.onewhohears.onewholibs.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.obj.ObjLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ObjLoader.class)
public class ObjLoaderMixin {

    @Shadow(remap = false)
    private ResourceManager manager;

    //@Inject(method = "onResourceManagerReload(Lnet/minecraft/server/packs/resources/ResourceManager)V", remap = false, at = @At("TAIL"))
    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    private void onewholibs_LoadMtlFix(ResourceManager resourceManager, CallbackInfo info) {
        manager = Minecraft.getInstance().getResourceManager();
    }

}

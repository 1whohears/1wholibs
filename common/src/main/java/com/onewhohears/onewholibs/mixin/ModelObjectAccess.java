package com.onewhohears.onewholibs.mixin;

import net.minecraftforge.client.model.obj.ObjModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ObjModel.ModelObject.class)
public interface ModelObjectAccess {
    @Accessor(value = "meshes", remap = false)
    List<Object> getMeshes();
}

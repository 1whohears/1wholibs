package com.onewhohears.onewholibs.mixin;

import net.minecraftforge.client.model.obj.ObjModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ObjModel.ModelGroup.class)
public interface ModelGroupAccess {
    @Accessor(value = "parts", remap = false)
    Map<String, ObjModel.ModelObject> getParts();
}

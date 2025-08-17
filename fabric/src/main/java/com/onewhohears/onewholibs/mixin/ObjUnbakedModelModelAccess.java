package com.onewhohears.onewholibs.mixin;

import de.javagl.obj.Obj;
import dev.felnull.specialmodelloader.impl.model.obj.ObjUnbakedModelModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ObjUnbakedModelModel.class)
public interface ObjUnbakedModelModelAccess {
    @Accessor(value = "obj", remap = false)
    Obj getObj();

}

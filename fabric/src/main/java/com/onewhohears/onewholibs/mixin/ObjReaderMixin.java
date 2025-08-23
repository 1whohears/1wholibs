package com.onewhohears.onewholibs.mixin;

import de.javagl.obj.ObjReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ObjReader.class)
public class ObjReaderMixin {
    @Inject(method = "readStrings", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void onewholibs_changeGroupsName(CallbackInfoReturnable<String[]> cir) {
        String[] strings = cir.getReturnValue();
        if (strings.length == 0) {
            cir.setReturnValue(strings);
            return;
        }
        StringBuilder namePath = new StringBuilder(strings[strings.length - 1]);
        for (int i = strings.length-2; i >= 0; --i) namePath.append("/").append(strings[i]);
        String[] newStrings = new String[] {namePath.toString()};
        cir.setReturnValue(newStrings);
    }
}

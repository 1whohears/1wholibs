package com.onewhohears.onewholibs.forge;

import net.minecraftforge.fml.ModList;

public class OWLModPlatformUtilsImpl {
    public static boolean isOtherModPresent(String modId) {
        return ModList.get().isLoaded(modId);
    }
}

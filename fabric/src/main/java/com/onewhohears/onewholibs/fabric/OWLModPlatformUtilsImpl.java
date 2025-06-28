package com.onewhohears.onewholibs.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class OWLModPlatformUtilsImpl {
    public static boolean isOtherModPresent(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}

package com.onewhohears.onewholibs.fabric;

import com.onewhohears.onewholibs.OWLMod;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.fabricmc.api.ModInitializer;

public final class OWLModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OWLMod.init();
        if (Platform.getEnvironment() == Env.CLIENT) {
            OWLMod.clientInit();
        }
    }
}

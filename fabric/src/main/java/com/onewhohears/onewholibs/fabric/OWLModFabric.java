package com.onewhohears.onewholibs.fabric;

import com.onewhohears.onewholibs.OWLMod;
import net.fabricmc.api.ModInitializer;

public final class OWLModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OWLMod.init();
    }
}

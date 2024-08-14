package com.onewhohears.onewholibs.common.event;

import com.google.common.collect.ImmutableList;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class GetJsonPresetListenersEvent extends Event {

    private final List<JsonPresetReloadListener<?>> listeners = new ArrayList<>();

    public GetJsonPresetListenersEvent() {
    }

    public void addListener(JsonPresetReloadListener<?> listener) {
        listeners.add(listener);
    }

    public List<JsonPresetReloadListener<?>> getListeners() {
        return ImmutableList.copyOf(listeners);
    }

}

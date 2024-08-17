package com.onewhohears.onewholibs.common.event;

import com.google.common.collect.ImmutableList;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Fired on both client and server side by {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 * Register your {@link JsonPresetReloadListener} child classes to this event.
 * Listeners will automatically get added to {@link net.minecraftforge.event.AddReloadListenerEvent}.
 * @author 1whohears
 */
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

    @Override
    public boolean isCancelable() {
        return false;
    }

}

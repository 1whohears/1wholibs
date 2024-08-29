package com.onewhohears.onewholibs.data.jsonpreset;

import javax.annotation.Nonnull;

/**
 * save this object as a field within entities so their stats auto update when the world reloads
 * @author 1whohears
 */
public class PresetStatsHolder<P extends JsonPresetStats> {

    private final JsonPresetReloader<P> reloader;
    private final String id;
    private int reloads = 0;
    private P preset;

    public PresetStatsHolder(JsonPresetReloader<P> reloader, String id) {
        this.reloader = reloader;
        this.id = id;
        this.reloads = reloader.getReloads();
    }

    @Nonnull
    public P get() {
        if (preset == null || getReloader().getReloads() != reloads) {
            preset = getReloader().get(getId());
            reloads = getReloader().getReloads();
        }
        return preset;
    }

    public String getId() {
        return id;
    }

    public JsonPresetReloader<P> getReloader() {
        return reloader;
    }

    public int getHolderReloads() {
        return reloads;
    }

}

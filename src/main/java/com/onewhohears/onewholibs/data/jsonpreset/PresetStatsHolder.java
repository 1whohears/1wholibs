package com.onewhohears.onewholibs.data.jsonpreset;

import javax.annotation.Nonnull;

public class PresetStatsHolder<P extends JsonPresetStats> {

    private P preset;

    public PresetStatsHolder(@Nonnull P preset) {
        this.preset = preset;
    }

    public P get() {
        return preset;
    }

    public void set(@Nonnull P preset) {
        this.preset = preset;
    }

    public String getId() {
        return preset.getId();
    }

}

package com.onewhohears.onewholibs.data.jsonpreset;

public class PresetNotFoundException extends RuntimeException {

    private final String presetId;
    private final JsonPresetReloadListener<?> listener;

    public PresetNotFoundException(String presetId, JsonPresetReloadListener<?> listener) {
        super();
        this.presetId = presetId;
        this.listener = listener;
    }

    @Override
    public String getMessage() {
        return "The Preset with id "+presetId+" was not found in "+listener.getName();
    }
}

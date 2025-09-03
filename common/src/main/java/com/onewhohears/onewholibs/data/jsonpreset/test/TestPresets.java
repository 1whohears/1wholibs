package com.onewhohears.onewholibs.data.jsonpreset.test;

import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;

public class TestPresets extends JsonPresetReloadListener<TestPresetStats> {

    private static TestPresets INSTANCE;

    public static TestPresets get() {
        if (INSTANCE == null) INSTANCE = new TestPresets();
        return INSTANCE;
    }

    public TestPresets() {
        super("test_presets");
    }

    @Override
    public TestPresetStats[] getNewArray(int size) {
        return new TestPresetStats[size];
    }

    @Override
    protected void resetCache() {

    }

    @Override
    public void registerDefaultPresetTypes() {
        addPresetType(TestPresetStats.TEST_TYPE);
    }
}

package com.onewhohears.onewholibs.data.jsonpreset.test;

import com.google.gson.JsonObject;
import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetInstance;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetType;
import com.onewhohears.onewholibs.data.jsonpreset.PresetBuilder;
import com.onewhohears.onewholibs.util.UtilParse;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class TestPresetStats extends JsonPresetStats {

    public static final JsonPresetType TEST_TYPE = new JsonPresetType("test", TestPresetStats::new) {};

    private final int value;

    public TestPresetStats(ResourceLocation key, JsonObject json) {
        super(key, json);
        value = UtilParse.getIntSafe(json, "value", 0);
    }

    public int getValue() {
        return value;
    }

    @Override
    public JsonPresetType getType() {
        return TEST_TYPE;
    }

    @Override
    public @Nullable JsonPresetInstance<?> createPresetInstance() {
        return null;
    }

    public static class Builder<C extends TestPresetStats> extends PresetBuilder<Builder<C>> {
        public static Builder<?> create(String name) {
            return new Builder<>(OWLMod.MOD_ID, name, TEST_TYPE);
        }
        public Builder(String namespace, String name, JsonPresetType type) {
            super(namespace, name, type);
        }
        public Builder<C> setValue(int value) {
            return setInt("value", value);
        }
    }
}

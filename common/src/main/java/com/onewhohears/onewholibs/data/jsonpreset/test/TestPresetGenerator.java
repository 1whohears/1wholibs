package com.onewhohears.onewholibs.data.jsonpreset.test;

import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetGenerator;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.NotNull;

public class TestPresetGenerator extends JsonPresetGenerator<TestPresetStats> {

    public static void register(DataGenerator generator) {
        generator.addProvider(true, new TestPresetGenerator(generator));
    }

    public TestPresetGenerator(DataGenerator output) {
        super(output, "test_presets");
    }

    @Override
    protected void registerPresets() {
        addPresetToGenerate(TestPresetStats.Builder.create("test0")
                .setValue(0)
                .addIngredient("minecraft:dirt", 10)
                .build());
        addPresetToGenerate(TestPresetStats.Builder.create("test1")
                .setValue(1)
                .addIngredient("minecraft:dirt", 20)
                .build());
        addPresetToGenerate(TestPresetStats.Builder.create("test2")
                .setValue(2)
                .addIngredient("minecraft:dirt", 30)
                .build());
    }

    @Override
    public @NotNull String getName() {
        return "test_presets";
    }
}

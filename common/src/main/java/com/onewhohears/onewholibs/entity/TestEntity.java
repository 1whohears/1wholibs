package com.onewhohears.onewholibs.entity;

import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TestEntity extends JsonPresetEntity<TestPresetStats> {

    public TestEntity(EntityType<?> entityType, Level level) {
        super(entityType, level, "test0");
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public @NotNull JsonPresetReloadListener<TestPresetStats> getPresets() {
        return TestPresets.get();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {

    }
}

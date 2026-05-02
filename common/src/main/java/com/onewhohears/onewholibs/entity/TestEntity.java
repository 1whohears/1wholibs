package com.onewhohears.onewholibs.entity;

import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TestEntity extends JsonPresetEntity<TestPresetStats> implements SimulatedEntity {

    private long lastServerTick;

    public TestEntity(EntityType<?> entityType, Level level) {
        super(entityType, level, "test0");
    }

    @Override
    public void tick() {
        SimulatedEntity.super.onVanillaTick();
        super.tick();
    }

    @Override
    public void onAlwaysTickPre(@NotNull MinecraftServer server) {
        //System.out.println("ALWAYS TICK "+tickCount+" "+lastServerTick+" "+this);
    }

    @Override
    public void onSimulatedTick(@NotNull MinecraftServer server) {
        //System.out.println("SIMULATED TICK "+tickCount+" "+lastServerTick+" "+this);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public @NotNull JsonPresetReloadListener<TestPresetStats> getPresets() {
        return TestPresets.get();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }

    @Override
    public long getLastServerTick() {
        return lastServerTick;
    }

    @Override
    public void setLastServerTick(long tick) {
        lastServerTick = tick;
    }
}

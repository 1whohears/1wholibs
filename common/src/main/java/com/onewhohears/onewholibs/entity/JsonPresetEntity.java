package com.onewhohears.onewholibs.entity;

import com.onewhohears.onewholibs.common.network.toclient.ClientBoundAddJsonPresetEntityPacket;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.PresetStatsHolder;
import com.onewhohears.onewholibs.util.UtilEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class JsonPresetEntity<P extends JsonPresetStats> extends Entity implements JsonPresetEntityHolder<P> {

    @NotNull final String defaultPreset;
    private String preset;
    private PresetStatsHolder<P> statsHolder;
    private boolean isStatsHolderLoaded = false;

    public JsonPresetEntity(EntityType<?> entityType, Level level, @NotNull String defaultPreset) {
        super(entityType, level);
        this.defaultPreset = defaultPreset;
        setPreset(defaultPreset);
        isStatsHolderLoaded = true;
    }

    @Override
    public boolean isClientSide() {
        return UtilEntity.getLevel(this).isClientSide();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        JsonPresetEntityHolder.super.readAdditionalSaveData(nbt);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        JsonPresetEntityHolder.super.addAdditionalSaveData(nbt);
    }

    @NotNull
    public String getDefaultStatsId() {
        return defaultPreset;
    }

    @NotNull
    public String getStatsId() {
        return preset;
    }

    @Override
    public void setStatsId(@NotNull String id) {
        preset = id;
    }

    @Override
    public void setStatsHolder(@NotNull PresetStatsHolder<P> holder) {
        statsHolder = holder;
    }

    @Override
    public @NotNull PresetStatsHolder<P> getStatsHolder() {
        return statsHolder;
    }

    @Override
    public boolean isStatsHolderLoaded() {
        return isStatsHolderLoaded;
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return new ClientBoundAddJsonPresetEntityPacket(this);
    }
}

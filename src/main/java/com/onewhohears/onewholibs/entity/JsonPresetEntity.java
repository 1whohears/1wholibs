package com.onewhohears.onewholibs.entity;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.PresetNotFoundException;
import com.onewhohears.onewholibs.data.jsonpreset.PresetStatsHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Objects;

public abstract class JsonPresetEntity<P extends JsonPresetStats> extends Entity implements IEntityAdditionalSpawnData {

    protected static final Logger LOGGER = LogUtils.getLogger();

    @NotNull final String defaultPreset;

    @NotNull String preset;
    @NotNull private PresetStatsHolder<P> statsHolder;

    public JsonPresetEntity(EntityType<?> entityType, Level level, @NotNull String defaultPreset) {
        super(entityType, level);
        this.defaultPreset = defaultPreset;
        this.preset = defaultPreset;
        if (!getPresets().has(preset)) {
            throw new PresetNotFoundException(preset, getPresets());
        }
        statsHolder = getStatsHolder(preset);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        preset = nbt.getString("preset"); // check if preset was defined
        if (preset.isEmpty()) preset = defaultPreset; // if not use the default preset
        else if (!getPresets().has(preset)) { // check if the preset exists
            preset = defaultPreset;
            LOGGER.warn("ERROR: preset {} doesn't exist!", preset);
        }
        statsHolder = getStatsHolder(preset); // get the preset data
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putString("preset", preset);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        preset = buffer.readUtf();
        statsHolder = getStatsHolder(preset);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeUtf(preset);
    }

    @NotNull public abstract JsonPresetReloadListener<P> getPresets();

    @NotNull
    protected PresetStatsHolder<P> getStatsHolder(String presetId) {
        return Objects.requireNonNull(getPresets().getHolder(presetId));
    }

    @NotNull
    public String getDefaultStatsId() {
        return defaultPreset;
    }

    @NotNull
    public String getStatsId() {
        return preset;
    }

    @NotNull
    public P getStats() {
        return statsHolder.get();
    }
}

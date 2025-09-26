package com.onewhohears.onewholibs.entity;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.PresetNotFoundException;
import com.onewhohears.onewholibs.data.jsonpreset.PresetStatsHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Objects;

public interface JsonPresetEntityHolder<P extends JsonPresetStats> extends AdditionalSpawnDataEntity {

    Logger LOGGER = LogUtils.getLogger();

    @NotNull JsonPresetReloadListener<P> getPresets();
    @NotNull String getDefaultStatsId();
    @NotNull String getStatsId();
    @NotNull PresetStatsHolder<P> getStatsHolder();
    @NotNull
    default PresetStatsHolder<P> createStatsHolder(@NotNull String presetId) {
        return Objects.requireNonNull(getPresets().getHolder(presetId));
    }
    default void updateStatsHolder(@NotNull String presetId) {
        PresetStatsHolder<P> holder = createStatsHolder(presetId);
        setStatsHolder(holder);
    }
    @NotNull
    default P getStats() {
        return getStatsHolder().get();
    }
    default void setPreset(@NotNull String preset) {
        if (!getPresets().has(preset)) {
            LOGGER.warn("Preset id {} does not exist. Using default id {}", preset, getDefaultStatsId());
            preset = getDefaultStatsId();
            if (!getPresets().has(preset)) {
                throw new PresetNotFoundException(preset, getPresets());
            }
        }
        if (isStatsHolderLoaded() && getStatsId().equals(preset)) return;
        setStatsId(preset);
        updateStatsHolder(preset);
    }
    /**
     * this is only used internally! you probably want to call
     * {@link #setPreset(String)} instead!
     */
    void setStatsId(@NotNull String id);
    /**
     * this is only used internally! you probably want to call
     * {@link #updateStatsHolder(String)} instead!
     */
    void setStatsHolder(@NotNull PresetStatsHolder<P> holder);
    default void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        String preset = nbt.getString("preset"); // check if preset was defined
        if (preset.isEmpty()) preset = getDefaultStatsId(); // if not use the default preset
        else if (!getPresets().has(preset)) { // check if the preset exists
            LOGGER.warn("ERROR: preset {} doesn't exist!", preset);
            preset = getDefaultStatsId();
        }
        setStatsId(preset);
        updateStatsHolder(getStatsId()); // get the preset data
    }
    default void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        nbt.putString("preset", getStatsId());
    }
    @Override
    default void readSpawnData(FriendlyByteBuf buffer) {
        setStatsId(buffer.readUtf());
        updateStatsHolder(getStatsId());
    }
    @Override
    default void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeUtf(getStatsId());
    }
    /**
     * this should return true at the end of the constructor block
     */
    boolean isStatsHolderLoaded();
    boolean isClientSide();
    int getId();
}

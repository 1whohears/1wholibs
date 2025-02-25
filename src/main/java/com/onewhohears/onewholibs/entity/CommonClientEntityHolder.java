package com.onewhohears.onewholibs.entity;

import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetAssetReader;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.PresetStatsHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommonClientEntityHolder<P extends JsonPresetStats, C extends JsonPresetStats> extends JsonPresetEntityHolder<P> {
    @Override
    default void readSpawnData(FriendlyByteBuf buffer) {
        JsonPresetEntityHolder.super.readSpawnData(buffer);
        updateClientStatsHolder();
    }
    /**
     * @return NULL IF SERVER SIDE
     */
    @Nullable String getAssetId();
    /**
     * @return NULL IF SERVER SIDE
     */
    @Nullable JsonPresetAssetReader<C> getClientPresets();
    @Nullable default PresetStatsHolder<C> createClientStatsHolder() {
        String assetId = getAssetId();
        if (assetId == null) return null;
        JsonPresetAssetReader<C> assetReader = getClientPresets();
        if (assetReader == null) return null;
        return assetReader.getHolder(assetId);
    }
    /**
     * @return NULL IF SERVER SIDE
     */
    @Nullable default C getAssets() {
        if (getClientStatsHolder() == null) return null;
        return getClientStatsHolder().get();
    }
    @Override
    default void setPreset(@NotNull String preset) {
        String oldPreset = getStatsId();
        JsonPresetEntityHolder.super.setPreset(preset);
        if (!getLevel().isClientSide()) return;
        if (isStatsHolderLoaded() && getStatsId().equals(oldPreset)) return;
        updateClientStatsHolder();
    }
    Level getLevel();
    @Nullable PresetStatsHolder<C> getClientStatsHolder();
    /**
     * this is only used internally! you probably want to call
     * {@link #setPreset(String)} instead! note asset id is meant
     * to be obtained from the server side Json Preset.
     */
    void setClientStatsHolder(PresetStatsHolder<C> holder);
    default void updateClientStatsHolder() {
        setClientStatsHolder(createClientStatsHolder());
    }
}

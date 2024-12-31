package com.onewhohears.onewholibs.entity;

import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetAssetReader;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.PresetStatsHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommonClientPresetEntity<P extends JsonPresetStats, C extends JsonPresetStats> extends JsonPresetEntity<P> {

    @Nullable private PresetStatsHolder<C> clientStatsHolder = null;

    public CommonClientPresetEntity(EntityType<?> entityType, Level level, @NotNull String defaultPreset) {
        super(entityType, level, defaultPreset);
        if (level.isClientSide()) {
            clientStatsHolder = getClientStatsHolder();
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        super.readSpawnData(buffer);
        clientStatsHolder = getClientStatsHolder();
    }
    /**
     * @return NULL IF SERVER SIDE
     */
    @Nullable public abstract String getAssetId();
    /**
     * @return NULL IF SERVER SIDE
     */
    @Nullable public abstract JsonPresetAssetReader<C> getClientPresets();

    private PresetStatsHolder<C> getClientStatsHolder() {
        String assetId = getAssetId();
        if (assetId == null) return null;
        JsonPresetAssetReader<C> assetReader = getClientPresets();
        if (assetReader == null) return null;
        if (!assetReader.has(assetId)) return null;
        return assetReader.getHolder(assetId);
    }
    /**
     * @return NULL IF SERVER SIDE
     */
    @Nullable public C getAssets() {
        if (clientStatsHolder == null) return null;
        return clientStatsHolder.get();
    }
}

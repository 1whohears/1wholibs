package com.onewhohears.onewholibs.entity;

import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.PresetStatsHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommonClientPresetEntity<P extends JsonPresetStats, C extends JsonPresetStats> extends JsonPresetEntity<P> implements CommonClientEntityHolder<P, C> {

    @Nullable private PresetStatsHolder<C> clientStatsHolder = null;

    public CommonClientPresetEntity(EntityType<?> entityType, Level level, @NotNull String defaultPreset) {
        super(entityType, level, defaultPreset);
    }

    @Override
    public @Nullable PresetStatsHolder<C> getClientStatsHolder() {
        return clientStatsHolder;
    }

    @Override
    public void setClientStatsHolder(PresetStatsHolder<C> holder) {
        clientStatsHolder = holder;
    }
}

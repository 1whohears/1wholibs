package com.onewhohears.onewholibs.entity;

import com.onewhohears.onewholibs.data.jsonpreset.CustomAnimStats;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class CustomAnimEntity<P extends JsonPresetStats, C extends CustomAnimStats> extends CommonClientPresetEntity<P, C>{
    public CustomAnimEntity(EntityType<?> entityType, Level level, @NotNull String defaultPreset) {
        super(entityType, level, defaultPreset);
    }
}

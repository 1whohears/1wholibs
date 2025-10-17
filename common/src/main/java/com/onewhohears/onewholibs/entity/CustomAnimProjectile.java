package com.onewhohears.onewholibs.entity;

import com.onewhohears.onewholibs.common.network.toclient.ClientBoundSpawnDataPacket;
import com.onewhohears.onewholibs.data.jsonpreset.CustomAnimStats;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.PresetStatsHolder;
import com.onewhohears.onewholibs.util.UtilEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CustomAnimProjectile<P extends JsonPresetStats, C extends CustomAnimStats> extends Projectile implements CustomAnimEntityHolder<P, C> {

    @NotNull final String defaultPreset;
    private String preset;
    private PresetStatsHolder<P> statsHolder;
    @Nullable private PresetStatsHolder<C> clientStatsHolder = null;
    private boolean isStatsHolderLoaded = false;

    public CustomAnimProjectile(EntityType<? extends Projectile> type, Level level, @NotNull String defaultPreset) {
        super(type, level);
        this.defaultPreset = defaultPreset;
        setPreset(defaultPreset);
        isStatsHolderLoaded = true;
    }

    @Override
    public boolean isClientSide() {
        return UtilEntity.getLevel(this).isClientSide();
    }

    @Override
    public @Nullable PresetStatsHolder<C> getClientStatsHolder() {
        return clientStatsHolder;
    }

    @Override
    public void setClientStatsHolder(PresetStatsHolder<C> holder) {
        this.clientStatsHolder = holder;
    }

    @Override
    public @NotNull String getDefaultStatsId() {
        return defaultPreset;
    }

    @Override
    public @NotNull String getStatsId() {
        return preset;
    }

    @Override
    public @NotNull PresetStatsHolder<P> getStatsHolder() {
        return statsHolder;
    }

    @Override
    public void setStatsId(@NotNull String id) {
        this.preset = id;
    }

    @Override
    public void setStatsHolder(@NotNull PresetStatsHolder<P> holder) {
        this.statsHolder = holder;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        CustomAnimEntityHolder.super.readAdditionalSaveData(nbt);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        CustomAnimEntityHolder.super.addAdditionalSaveData(nbt);
    }

    @Override
    public boolean isStatsHolderLoaded() {
        return isStatsHolderLoaded;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientBoundSpawnDataPacket(this, this);
    }
}

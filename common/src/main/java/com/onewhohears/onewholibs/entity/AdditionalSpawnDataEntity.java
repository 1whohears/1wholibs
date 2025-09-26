package com.onewhohears.onewholibs.entity;

import net.minecraft.network.FriendlyByteBuf;

public interface AdditionalSpawnDataEntity {
    void readSpawnData(FriendlyByteBuf buffer);
    void writeSpawnData(FriendlyByteBuf buffer);
}

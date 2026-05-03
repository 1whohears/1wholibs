package com.onewhohears.onewholibs.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UtilServer {

    @Nullable
    public static ServerLevel getLevel(@NotNull MinecraftServer server, @NotNull String dimensionId) {
        ResourceLocation id = ResourceLocation.tryParse(dimensionId);
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, id);
        return server.getLevel(key);
    }

}

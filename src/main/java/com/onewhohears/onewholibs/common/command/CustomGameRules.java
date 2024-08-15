package com.onewhohears.onewholibs.common.command;

import com.onewhohears.onewholibs.util.UtilSync;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.BiConsumer;

public class CustomGameRules {

    private static BiConsumer<MinecraftServer, GameRules.BooleanValue> clientSyncListener() {
        return (server, value) -> UtilSync.syncGameRules(PacketDistributor.ALL.noArg(), server);
    }

    public static GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, boolean defaultValue) {
        return registerBoolean(name, defaultValue, GameRules.Category.MISC);
    }

    public static GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, boolean defaultValue, GameRules.Category category) {
        return GameRules.register(name, category, GameRules.BooleanValue.create(defaultValue));
    }

    public static GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, boolean defaultValue, GameRules.Category category,
                                                                        BiConsumer<MinecraftServer, GameRules.BooleanValue> listener) {
        return GameRules.register(name, category,
                GameRules.BooleanValue.create(defaultValue, listener));
    }

    public static GameRules.Key<GameRules.BooleanValue> registerSyncBoolean(String name, boolean defaultValue, GameRules.Category category) {
        return registerBoolean(name, defaultValue, category, clientSyncListener());
    }

    public static GameRules.Key<GameRules.IntegerValue> registerInteger(String name, int defaultValue, GameRules.Category category) {
        return GameRules.register(name, category, GameRules.IntegerValue.create(defaultValue));
    }

    // TODO register sync integer gamerule

}

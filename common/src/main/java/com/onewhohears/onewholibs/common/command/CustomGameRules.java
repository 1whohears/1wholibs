package com.onewhohears.onewholibs.common.command;

import com.onewhohears.onewholibs.util.UtilSync;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author 1whohears
 */
public class CustomGameRules {

    private static final List<GameRules.Key<GameRules.BooleanValue>> SYNC_BOOLS = new ArrayList<>();
    private static final List<GameRules.Key<GameRules.IntegerValue>> SYNC_INTS = new ArrayList<>();

    public static List<GameRules.Key<GameRules.BooleanValue>> getSyncBools() {
        return SYNC_BOOLS;
    }

    public static List<GameRules.Key<GameRules.IntegerValue>> getSyncInts() {
        return SYNC_INTS;
    }

    public static BiConsumer<MinecraftServer, GameRules.BooleanValue> clientSyncListener() {
        return (server, value) -> UtilSync.syncGameRules(server);
    }
    /**
     * Should ideally be called some time during common-side lifecycle setup on the loader
     * @param name a unique game rule id
     * @return the key used to access this game rule with {@link GameRules#getBoolean(GameRules.Key)}
     */
    public static GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, boolean defaultValue) {
        return registerBoolean(name, defaultValue, GameRules.Category.MISC);
    }
    /**
     * Should ideally be called some time during common-side lifecycle setup on the loader
     * @param name a unique game rule id
     * @return the key used to access this game rule with {@link GameRules#getBoolean(GameRules.Key)}
     */
    public static GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, boolean defaultValue, GameRules.Category category) {
        return GameRules.register(name, category, GameRules.BooleanValue.create(defaultValue));
    }
    /**
     * Should ideally be called some time during common-side lifecycle setup on the loader
     * @param name a unique game rule id
     * @return the key used to access this game rule with {@link GameRules#getBoolean(GameRules.Key)}
     */
    public static GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, boolean defaultValue, GameRules.Category category,
                                                                        BiConsumer<MinecraftServer, GameRules.BooleanValue> listener) {
        return GameRules.register(name, category,
                GameRules.BooleanValue.create(defaultValue, listener));
    }
    /**
     * Should ideally be called some time during common-side lifecycle setup on the loader.
     * Use {@link com.onewhohears.onewholibs.common.event.OWLEvents#SYNC_BOOL_GAME_RULE} to handle game rule sync packet.
     * @param name a unique game rule id
     * @return the key used to access this game rule with {@link GameRules#getBoolean(GameRules.Key)}
     */
    public static GameRules.Key<GameRules.BooleanValue> registerSyncBoolean(String name, boolean defaultValue, GameRules.Category category) {
        GameRules.Key<GameRules.BooleanValue> bool = registerBoolean(name, defaultValue, category, clientSyncListener());
        SYNC_BOOLS.add(bool);
        return bool;
    }
    /**
     * Should ideally be called some time during common-side lifecycle setup on the loader
     * @param name a unique game rule id
     * @return the key used to access this game rule with {@link GameRules#getInt(GameRules.Key)}
     */
    public static GameRules.Key<GameRules.IntegerValue> registerInteger(String name, int defaultValue, GameRules.Category category) {
        return GameRules.register(name, category, GameRules.IntegerValue.create(defaultValue));
    }

    // TODO register sync integer gamerule
}

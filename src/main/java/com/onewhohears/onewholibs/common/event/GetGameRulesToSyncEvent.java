package com.onewhohears.onewholibs.common.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.Map;

public class GetGameRulesToSyncEvent extends Event {

    private final MinecraftServer server;
    private final Map<String, Boolean> bools = new HashMap<>();
    private final Map<String, Integer> ints = new HashMap<>();

    public GetGameRulesToSyncEvent(MinecraftServer server) {
        this.server = server;
    }

    public void addBool(String id, boolean bool) {
        bools.put(id, bool);
    }

    public void addInt(String id, int integer) {
        ints.put(id, integer);
    }

    public void addBool(GameRules.Key<GameRules.BooleanValue> key) {
        addBool(key.getId(), server.getGameRules().getBoolean(key));
    }

    public void addInt(GameRules.Key<GameRules.IntegerValue> key) {
        addInt(key.getId(), server.getGameRules().getInt(key));
    }

    public Map<String, Boolean> getBools() {
        return bools;
    }

    public Map<String, Integer> getInts() {
        return ints;
    }

    public MinecraftServer getServer() {
        return server;
    }

}

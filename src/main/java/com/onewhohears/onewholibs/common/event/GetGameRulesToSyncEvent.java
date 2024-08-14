package com.onewhohears.onewholibs.common.event;

import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.Map;

public class GetGameRulesToSyncEvent extends Event {

    private final Map<String, Boolean> bools = new HashMap<>();
    private final Map<String, Integer> ints = new HashMap<>();

    public GetGameRulesToSyncEvent() {
    }

    public void addBool(String id, boolean bool) {
        bools.put(id, bool);
    }

    public void addInt(String id, int integer) {
        ints.put(id, integer);
    }

    public Map<String, Boolean> getBools() {
        return bools;
    }

    public Map<String, Integer> getInts() {
        return ints;
    }
}

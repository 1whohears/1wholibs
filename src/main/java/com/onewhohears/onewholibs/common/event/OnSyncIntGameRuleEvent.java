package com.onewhohears.onewholibs.common.event;

import net.minecraftforge.eventbus.api.Event;

public class OnSyncIntGameRuleEvent extends Event {

    private final String id;
    private final int integer;

    public OnSyncIntGameRuleEvent(String id, int integer) {
        this.id = id;
        this.integer = integer;
    }

    public String getId() {
        return id;
    }

    public int getInt() {
        return integer;
    }
    
}

package com.onewhohears.onewholibs.common.event;

import net.minecraftforge.eventbus.api.Event;

public class OnSyncBoolGameRuleEvent extends Event {

    private final String id;
    private final boolean bool;

    public OnSyncBoolGameRuleEvent(String id, boolean bool) {
        this.id = id;
        this.bool = bool;
    }

    public String getId() {
        return id;
    }

    public boolean getBool() {
        return bool;
    }

}

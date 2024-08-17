package com.onewhohears.onewholibs.common.event;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on client side by {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 * Game rules registered with {@link com.onewhohears.onewholibs.common.command.CustomGameRules#registerSyncBoolean(String, boolean, GameRules.Category)}
 * will fire this event on the client side.
 * @author 1whohears
 */
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

    @Override
    public boolean isCancelable() {
        return false;
    }

}

package com.onewhohears.onewholibs.common.event;

import com.onewhohears.onewholibs.util.UtilSync;
import dev.architectury.event.events.common.PlayerEvent;

import java.util.Collections;

public class OWLArchEventHandlers {

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            UtilSync.syncPresets(Collections.singleton(player));
            UtilSync.syncGameRules(Collections.singleton(player), player.getServer());
        });
    }

}

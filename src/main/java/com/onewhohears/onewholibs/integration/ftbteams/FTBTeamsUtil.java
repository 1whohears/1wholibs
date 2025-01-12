package com.onewhohears.onewholibs.integration.ftbteams;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import net.minecraft.server.level.ServerPlayer;

/**
 * DO NOT CALL DIRECTLY!
 * See {@link com.onewhohears.onewholibs.util.UtilEntity#arePlayersAllied(ServerPlayer, ServerPlayer)}
 */
public class FTBTeamsUtil {
    /**
     * DO NOT CALL DIRECTLY!
     * See {@link com.onewhohears.onewholibs.util.UtilEntity#arePlayersAllied(ServerPlayer, ServerPlayer)}
     */
    public static boolean arePlayersFTBAllied(ServerPlayer player1, ServerPlayer player2) {
        return FTBTeamsAPI.getManager().arePlayersInSameTeam(player1, player2);
    }

}

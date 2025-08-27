package com.onewhohears.onewholibs.integration.ftbteams;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.Team;
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
        Team team = FTBTeamsAPI.getManager().getPlayerTeam(player1.getUUID());
        if (team == null) return false;
        if (team.isMember(player2.getUUID())) return true;
        if (team.isAlly(player2.getUUID())) return true;
        return false;
    }

}

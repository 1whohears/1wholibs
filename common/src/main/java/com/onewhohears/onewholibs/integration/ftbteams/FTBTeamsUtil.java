package com.onewhohears.onewholibs.integration.ftbteams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

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
        Optional<Team> team = FTBTeamsAPI.api().getManager().getTeamForPlayer(player1);
        return team.map(value -> value.getRankForPlayer(player2.getUUID()).isAllyOrBetter()).orElse(false);
    }

}

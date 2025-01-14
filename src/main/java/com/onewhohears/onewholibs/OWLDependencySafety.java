package com.onewhohears.onewholibs;

import com.onewhohears.onewholibs.integration.ftbteams.FTBTeamsUtil;
import com.onewhohears.onewholibs.integration.openpartiesandclaims.PACUtil;
import net.minecraft.server.level.ServerPlayer;

public class OWLDependencySafety {

    public static boolean arePlayersFTBAllied(ServerPlayer player1, ServerPlayer player2) {
        if (OWLMod.FTB_TEAMS_LOADED) return FTBTeamsUtil.arePlayersFTBAllied(player1, player2);
        return false;
    }

    public static boolean arePlayersPACAllied(ServerPlayer player1, ServerPlayer player2) {
        if (OWLMod.PAC_LOADED) return PACUtil.arePlayersPACAllied(player1, player2);
        return false;
    }

    public static boolean arePlayersAlliedModdedTeamSystem(ServerPlayer player1, ServerPlayer player2) {
        if (arePlayersFTBAllied(player1, player2)) return true;
        if (arePlayersPACAllied(player1, player2)) return true;
        return false;
    }
}

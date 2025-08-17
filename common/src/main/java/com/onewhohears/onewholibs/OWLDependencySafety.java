package com.onewhohears.onewholibs;

import com.onewhohears.onewholibs.integration.ftbteams.FTBTeamsUtil;
import com.onewhohears.onewholibs.integration.openpartiesandclaims.PACUtil;
import dev.architectury.platform.Platform;
import net.minecraft.server.level.ServerPlayer;

/**
 * Avoids those crunchy {@code ClassNotFoundException}s
 */
public class OWLDependencySafety {
    public static boolean arePlayersFTBAllied(ServerPlayer player1, ServerPlayer player2) {
        if (Platform.isModLoaded("ftbteams")) {
            return FTBTeamsUtil.arePlayersFTBAllied(player1, player2);
        }
        return false;
    }

    public static boolean arePlayersPACAllied(ServerPlayer player1, ServerPlayer player2) {
        if (Platform.isModLoaded("openpartiesandclaims")) {
            return PACUtil.arePlayersPACAllied(player1, player2);
        }
        return false;
    }

    public static boolean arePlayersAlliedModdedTeamSystem(ServerPlayer player1, ServerPlayer player2) {
        if (arePlayersFTBAllied(player1, player2)) return true;
        return arePlayersPACAllied(player1, player2);
    }
}

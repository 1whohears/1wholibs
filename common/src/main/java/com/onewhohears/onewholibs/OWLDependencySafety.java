package com.onewhohears.onewholibs;

import com.onewhohears.onewholibs.integration.distanthorizons.DHUtil;
import com.onewhohears.onewholibs.integration.ftbteams.FTBTeamsUtil;
import com.onewhohears.onewholibs.integration.openpartiesandclaims.PACUtil;
import dev.architectury.platform.Platform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

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

    /**
     * CLIENT ONLY!
     * If Distant Horizons (DH) is loaded, it uses the DH api to do a raycast on the LODs.
     * Returns false if something obstructs between start and end.
     * Returns true if there is nothing in between start and end or DH is not loaded.
     */
    public static boolean distantHorizonsRaycast(Vec3 start, Vec3 end, int maxDistance) {
        if (Platform.isModLoaded("distanthorizons")) {
            return DHUtil.raycast(start, end, maxDistance);
        }
        return true;
    }
}

package com.onewhohears.onewholibs.integration.openpartiesandclaims.forge;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.parties.party.api.IServerPartyAPI;

/**
 * DO NOT CALL DIRECTLY!
 * See {@link com.onewhohears.onewholibs.util.UtilEntity#arePlayersAllied(ServerPlayer, ServerPlayer)}
 */
public class PACUtilImpl {
    /**
     * DO NOT CALL DIRECTLY!
     * See {@link com.onewhohears.onewholibs.util.UtilEntity#arePlayersAllied(ServerPlayer, ServerPlayer)}
     */
    public static boolean arePlayersPACAllied(@NotNull ServerPlayer player1, @NotNull ServerPlayer player2) {
        IServerPartyAPI party = OpenPACServerAPI.get(player1.getServer()).getPartyManager().getPartyByMember(player1.getUUID());
        if (party == null) return false;
        if (party.getMemberInfo(player2.getUUID()) != null) return true;
        if (party.isAlly(player2.getUUID())) return true;
        return false;
    }


}

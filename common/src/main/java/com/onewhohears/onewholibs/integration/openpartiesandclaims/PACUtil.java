package com.onewhohears.onewholibs.integration.openpartiesandclaims;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * DO NOT CALL DIRECTLY!
 * See {@link com.onewhohears.onewholibs.util.UtilEntity#arePlayersAllied(ServerPlayer, ServerPlayer)}
 */
public class PACUtil {
    /**
     * DO NOT CALL DIRECTLY!
     * See {@link com.onewhohears.onewholibs.util.UtilEntity#arePlayersAllied(ServerPlayer, ServerPlayer)}
     */
    @ExpectPlatform
    public static boolean arePlayersPACAllied(@NotNull ServerPlayer player1, @NotNull ServerPlayer player2) {
        throw new AssertionError();
    }


}

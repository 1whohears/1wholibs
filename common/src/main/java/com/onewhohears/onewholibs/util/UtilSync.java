package com.onewhohears.onewholibs.util;

import com.onewhohears.onewholibs.common.network.OWLPacketHandler;
import com.onewhohears.onewholibs.common.network.toclient.ToClientDataPackSync;
import com.onewhohears.onewholibs.common.network.toclient.ToClientSyncGameRules;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;

/**
 * @author 1whohears
 */
public class UtilSync {
    /**
     * syncs custom gamerules with client and server.
     * the custom gamerules must be defined with
     * {@link com.onewhohears.onewholibs.common.command.CustomGameRules#registerSyncBoolean(String, boolean, GameRules.Category)}
     * see {@link com.onewhohears.onewholibs.common.event.OWLEvents#SYNC_BOOL_GAME_RULE} and
     * {@link com.onewhohears.onewholibs.common.event.OWLEvents#SYNC_BOOL_GAME_RULE}
     * for handeling syncable gamerules.
     * @param players the clients to send the gamerules too.
     */
    public static void syncGameRules(Iterable<ServerPlayer> players, @NotNull MinecraftServer server) {
        OWLPacketHandler.INSTANCE.sendToPlayers(players, new ToClientSyncGameRules(server));
    }
    /**
     * syncs custom gamerules with client and server for all players.
     * the custom gamerules must be defined with
     * {@link com.onewhohears.onewholibs.common.command.CustomGameRules#registerSyncBoolean(String, boolean, GameRules.Category)}
     * see {@link com.onewhohears.onewholibs.common.event.OWLEvents#SYNC_BOOL_GAME_RULE} and
     * {@link com.onewhohears.onewholibs.common.event.OWLEvents#SYNC_BOOL_GAME_RULE}
     * for collecting and handeling syncable gamerules.
     */
    public static void syncGameRules(@NotNull MinecraftServer server) {
        syncGameRules(server.getPlayerList().getPlayers(), server);
    }
    /**
     * Syncs preset data with clients defined by target.
     * You should not need to use this as it is already called by
     * {@link com.onewhohears.onewholibs.common.event.OWLReloadListener}
     * See {@link com.onewhohears.onewholibs.data.jsonpreset.JsonPresetInstance} and
     * {@link com.onewhohears.onewholibs.common.event.OWLEvents#GET_JSON_PRESET_LISTENERS}
     * @param players the clients to send the preset data to.
     */
    public static void syncPresets(Iterable<ServerPlayer> players) {
        OWLPacketHandler.INSTANCE.sendToPlayers(players, new ToClientDataPackSync());
    }
    /**
     * Syncs preset data with all clients.
     * You should not need to use this as it is already called by
     * {@link com.onewhohears.onewholibs.common.event.OWLReloadListener}
     * See {@link com.onewhohears.onewholibs.data.jsonpreset.JsonPresetInstance} and
     * {@link com.onewhohears.onewholibs.common.event.OWLEvents#GET_JSON_PRESET_LISTENERS}
     */
    public static void syncPresets(MinecraftServer server) {
        syncPresets(server.getPlayerList().getPlayers());
    }

}

package com.onewhohears.onewholibs.util;

import com.onewhohears.onewholibs.common.network.PacketHandler;
import com.onewhohears.onewholibs.common.network.toclient.ToClientDataPackSync;
import com.onewhohears.onewholibs.common.network.toclient.ToClientSyncGameRules;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.network.PacketDistributor;

/**
 * @author 1whohears
 */
public class UtilSync {
    /**
     * syncs custom gamerules with client and server.
     * the custom gamerules must be defined with
     * {@link com.onewhohears.onewholibs.common.command.CustomGameRules#registerSyncBoolean(String, boolean, GameRules.Category)}
     * see {@link com.onewhohears.onewholibs.common.event.OnSyncBoolGameRuleEvent} and
     * {@link com.onewhohears.onewholibs.common.event.OnSyncIntGameRuleEvent}
     * for handeling syncable gamerules.
     * @param target the clients to send the gamerules too.
     */
    public static void syncGameRules(PacketDistributor.PacketTarget target, MinecraftServer server) {
        PacketHandler.INSTANCE.send(target, new ToClientSyncGameRules(server));
    }
    /**
     * syncs custom gamerules with client and server for all players.
     * the custom gamerules must be defined with
     * {@link com.onewhohears.onewholibs.common.command.CustomGameRules#registerSyncBoolean(String, boolean, GameRules.Category)}
     * see {@link com.onewhohears.onewholibs.common.event.OnSyncBoolGameRuleEvent} and
     * {@link com.onewhohears.onewholibs.common.event.OnSyncIntGameRuleEvent}
     * for collecting and handeling syncable gamerules.
     */
    public static void syncGameRules(MinecraftServer server) {
        syncGameRules(PacketDistributor.ALL.noArg(), server);
    }
    /**
     * Syncs preset data with clients defined by target.
     * You should not need to use this as it is already called in
     * {@link com.onewhohears.onewholibs.common.event.handler.CommonForgeEvents} by
     * {@link net.minecraftforge.event.OnDatapackSyncEvent}
     * See {@link com.onewhohears.onewholibs.data.jsonpreset.JsonPresetInstance} and
     * {@link com.onewhohears.onewholibs.common.event.GetJsonPresetListenersEvent}
     * @param target the clients to send the preset data to.
     */
    public static void syncPresets(PacketDistributor.PacketTarget target) {
        PacketHandler.INSTANCE.send(target, new ToClientDataPackSync());
    }
    /**
     * Syncs preset data with all clients.
     * You should not need to use this as it is already called in
     * {@link com.onewhohears.onewholibs.common.event.handler.CommonForgeEvents} by
     * {@link net.minecraftforge.event.OnDatapackSyncEvent}
     * See {@link com.onewhohears.onewholibs.data.jsonpreset.JsonPresetInstance} and
     * {@link com.onewhohears.onewholibs.common.event.GetJsonPresetListenersEvent}
     */
    public static void syncPresets() {
        syncPresets(PacketDistributor.ALL.noArg());
    }

}

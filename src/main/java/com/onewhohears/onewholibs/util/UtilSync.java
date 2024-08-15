package com.onewhohears.onewholibs.util;

import com.onewhohears.onewholibs.common.network.PacketHandler;
import com.onewhohears.onewholibs.common.network.toclient.ToClientDataPackSync;
import com.onewhohears.onewholibs.common.network.toclient.ToClientSyncGameRules;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.PacketDistributor;

public class UtilSync {

    public static void syncGameRules(PacketDistributor.PacketTarget target, MinecraftServer server) {
        PacketHandler.INSTANCE.send(target, new ToClientSyncGameRules(server));
    }

    public static void syncPresets(PacketDistributor.PacketTarget target) {
        PacketHandler.INSTANCE.send(target, new ToClientDataPackSync());
    }

}

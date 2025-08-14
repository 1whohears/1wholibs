package com.onewhohears.onewholibs.common.network;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.common.network.toclient.ToClientDataPackSync;
import com.onewhohears.onewholibs.common.network.toclient.ToClientSyncGameRules;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public final class PacketHandler {

    private PacketHandler() {}

    public static final NetworkChannel INSTANCE = NetworkChannel.create(new ResourceLocation(
            OWLMod.MOD_ID, "networking_channel"));

    public static void register() {
        INSTANCE.register(ToClientDataPackSync.class,
                ToClientDataPackSync::encode,
                ToClientDataPackSync::new,
                ToClientDataPackSync::handle);
        INSTANCE.register(ToClientSyncGameRules.class,
                ToClientSyncGameRules::encode,
                ToClientSyncGameRules::new,
                ToClientSyncGameRules::handle);
    }
}

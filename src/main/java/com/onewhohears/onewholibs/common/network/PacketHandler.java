package com.onewhohears.onewholibs.common.network;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.common.network.toclient.ToClientDataPackSync;
import com.onewhohears.onewholibs.common.network.toclient.ToClientSyncGameRules;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler {

    private PacketHandler() {}

    private static final String PROTOCOL_VERSION = "1.0";

    public static SimpleChannel INSTANCE;

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(OWLMod.MODID, "messages"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(s -> s.equals(PROTOCOL_VERSION))
                .serverAcceptedVersions(s -> s.equals(PROTOCOL_VERSION))
                .simpleChannel();
        INSTANCE = net;
        int index = 0;
        net.messageBuilder(ToClientDataPackSync.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ToClientDataPackSync::encode)
                .decoder(ToClientDataPackSync::new)
                .consumerMainThread(ToClientDataPackSync::handle)
                .add();
        net.messageBuilder(ToClientSyncGameRules.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ToClientSyncGameRules::encode)
                .decoder(ToClientSyncGameRules::new)
                .consumerMainThread(ToClientSyncGameRules::handle)
                .add();
    }
}

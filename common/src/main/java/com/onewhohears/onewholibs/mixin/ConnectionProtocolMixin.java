package com.onewhohears.onewholibs.mixin;

import com.onewhohears.onewholibs.common.network.toclient.ClientBoundSpawnDataPacket;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectionProtocol.class)
public class ConnectionProtocolMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onewholibs_addCustomPacket(CallbackInfo ci) {
        ConnectionProtocol.PacketSet packetSet = ConnectionProtocol.PLAY.flows.get(PacketFlow.CLIENTBOUND);
        packetSet.addPacket(
                ClientBoundSpawnDataPacket.class,
                buffer -> new ClientBoundSpawnDataPacket((FriendlyByteBuf)buffer)
        );
        ConnectionProtocol.PROTOCOL_BY_PACKET.put(ClientBoundSpawnDataPacket.class, ConnectionProtocol.PLAY);
    }
}

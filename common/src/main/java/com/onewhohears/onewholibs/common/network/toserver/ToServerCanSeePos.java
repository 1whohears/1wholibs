package com.onewhohears.onewholibs.common.network.toserver;

import com.onewhohears.onewholibs.common.core.DistantRayCastManager;
import com.onewhohears.onewholibs.common.core.RayCastPerspective;
import com.onewhohears.onewholibs.common.network.OWLPacketHandler;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class ToServerCanSeePos extends BaseC2SMessage {

    private final int rayCastId;
    private final RayCastPerspective perspective;
    private final boolean success;

    public ToServerCanSeePos(int rayCastId, RayCastPerspective perspective, boolean success) {
        this.rayCastId = rayCastId;
        this.perspective = perspective;
        this.success = success;
    }

    public ToServerCanSeePos(FriendlyByteBuf buffer) {
        rayCastId = buffer.readInt();
        perspective = buffer.readEnum(RayCastPerspective.class);
        success = buffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(rayCastId);
        buffer.writeEnum(perspective);
        buffer.writeBoolean(success);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> DistantRayCastManager.handleC2SRayCast(rayCastId, perspective, success));
    }

    @Override
    public MessageType getType() {
        return OWLPacketHandler.C2S_RAY_CAST;
    }
}

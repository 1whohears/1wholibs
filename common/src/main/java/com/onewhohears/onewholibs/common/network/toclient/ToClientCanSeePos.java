package com.onewhohears.onewholibs.common.network.toclient;

import com.onewhohears.onewholibs.client.core.DistantRayCastManagerClient;
import com.onewhohears.onewholibs.common.core.RayCastPerspective;
import com.onewhohears.onewholibs.common.network.OWLPacketHandler;
import com.onewhohears.onewholibs.util.UtilPacket;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ToClientCanSeePos extends BaseS2CMessage {

    private final int rayCastId;
    private final RayCastPerspective perspective;
    private final int perspectiveEntityId;
    private final Vec3 targetPos;
    private final double throWater;
    private final double throBlock;

    public ToClientCanSeePos(int rayCastId, RayCastPerspective perspective, Entity perspectiveEntity,
                             Vec3 targetPos, double throWater, double throBlock) {
        this.rayCastId = rayCastId;
        this.perspective = perspective;
        this.perspectiveEntityId = perspectiveEntity.getId();
        this.targetPos = targetPos;
        this.throWater = throWater;
        this.throBlock = throBlock;
    }

    public ToClientCanSeePos(FriendlyByteBuf buffer) {
        rayCastId = buffer.readInt();
        perspective = buffer.readEnum(RayCastPerspective.class);
        perspectiveEntityId = buffer.readInt();
        targetPos = UtilPacket.readVec3(buffer);
        throWater = buffer.readDouble();
        throBlock = buffer.readDouble();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(rayCastId);
        buffer.writeEnum(perspective);
        buffer.writeInt(perspectiveEntityId);
        UtilPacket.writeVec3(targetPos, buffer);
        buffer.writeDouble(throWater);
        buffer.writeDouble(throBlock);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> DistantRayCastManagerClient.handleS2CRayCast(
                rayCastId, perspective, perspectiveEntityId, targetPos, throWater, throBlock));
    }

    @Override
    public MessageType getType() {
        return OWLPacketHandler.S2C_RAY_CAST;
    }
}

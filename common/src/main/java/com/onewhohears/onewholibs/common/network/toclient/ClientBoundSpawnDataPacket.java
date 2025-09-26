package com.onewhohears.onewholibs.common.network.toclient;

import com.onewhohears.onewholibs.common.network.OWLClientPacketHandler;
import com.onewhohears.onewholibs.entity.AdditionalSpawnDataEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;

public class ClientBoundSpawnDataPacket extends ClientboundAddEntityPacket implements Packet<ClientGamePacketListener> {

    private final FriendlyByteBuf buffer;

    public ClientBoundSpawnDataPacket(Entity entity, AdditionalSpawnDataEntity entityAgain) {
        super(entity);
        buffer = new FriendlyByteBuf(Unpooled.buffer());
        entityAgain.writeSpawnData(buffer);
    }

    public ClientBoundSpawnDataPacket(FriendlyByteBuf buffer) {
        super(buffer);
        int bytes = buffer.readInt();
        ByteBuf buf = buffer.readBytes(bytes);
        this.buffer = new FriendlyByteBuf(buf);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        buffer.writeInt(this.buffer.readableBytes());
        buffer.writeBytes(this.buffer);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        super.handle(listener);
        OWLClientPacketHandler.handle(this);
    }

    public FriendlyByteBuf getBuffer() {
        return buffer;
    }

}

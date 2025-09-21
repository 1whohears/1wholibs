package com.onewhohears.onewholibs.common.network.toclient;

import com.onewhohears.onewholibs.entity.CustomAnimProjectile;
import com.onewhohears.onewholibs.entity.JsonPresetEntity;
import com.onewhohears.onewholibs.entity.JsonPresetEntityHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.level.Level;

public class ClientBoundAddJsonPresetEntityPacket extends ClientboundAddEntityPacket {

    private final FriendlyByteBuf buffer;

    public ClientBoundAddJsonPresetEntityPacket(JsonPresetEntity entity) {
        super(entity);
        buffer = new FriendlyByteBuf(Unpooled.buffer());
        entity.writeSpawnData(buffer);
    }

    public ClientBoundAddJsonPresetEntityPacket(CustomAnimProjectile entity) {
        super(entity);
        buffer = new FriendlyByteBuf(Unpooled.buffer());
        entity.writeSpawnData(buffer);
    }

    public ClientBoundAddJsonPresetEntityPacket(FriendlyByteBuf buffer) {
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
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        if (level.getEntity(getId()) instanceof JsonPresetEntityHolder<?> holder) {
            holder.readSpawnData(buffer);
        }
    }
}

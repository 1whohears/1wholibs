package com.onewhohears.onewholibs.common.network.toclient;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.common.network.OWLPacketHandler;
import com.onewhohears.onewholibs.entity.JsonPresetEntityHolder;
import com.onewhohears.onewholibs.util.UtilEntity;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class ToClientSyncPresetEntity extends BaseS2CMessage {

    static final Logger LOGGER = LogUtils.getLogger();

    private final int id;
    private final FriendlyByteBuf buffer;

    public ToClientSyncPresetEntity(JsonPresetEntityHolder<?> holder) {
        id = holder.getId();
        buffer = new FriendlyByteBuf(Unpooled.buffer());
        holder.writeSpawnData(buffer);
    }

    public ToClientSyncPresetEntity(FriendlyByteBuf buffer) {
        id = buffer.readInt();
        int bytes = buffer.readInt();
        ByteBuf buf = buffer.readBytes(bytes);
        this.buffer = new FriendlyByteBuf(buf);
    }

    @Override
    public MessageType getType() {
        return OWLPacketHandler.S2C_SYNC_PRESET_ENTITY;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeInt(this.buffer.readableBytes());
        buffer.writeBytes(this.buffer);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        Level level = UtilEntity.getLevel(context.getPlayer());
        context.queue(() -> {
            if (!(level.getEntity(id) instanceof JsonPresetEntityHolder<?> holder)) {
                LOGGER.error("Received a JsonPresetEntityHolder sync packet for an entity with " +
                        "{} but it doesn't exist on the client side???", id);
                return;
            }
            holder.readSpawnData(buffer);
        });
    }
}

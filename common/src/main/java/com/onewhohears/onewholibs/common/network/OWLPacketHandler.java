package com.onewhohears.onewholibs.common.network;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.common.network.toclient.ToClientDataPackSync;
import com.onewhohears.onewholibs.common.network.toclient.ToClientSyncGameRules;
import com.onewhohears.onewholibs.entity.JsonPresetEntityHolder;
import com.onewhohears.onewholibs.util.UtilEntity;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.util.List;

public final class OWLPacketHandler {

    static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation SYNC_PRESET_ENTITY_ID = ResourceLocation.tryBuild(OWLMod.MOD_ID,
            "sync_preset_entity");

    private OWLPacketHandler() {}

    public static final NetworkChannel INSTANCE = NetworkChannel.create(new ResourceLocation(
            OWLMod.MOD_ID, "networking_channel"));

    public static void sendSyncPresetEntityPacket(JsonPresetEntityHolder<?> holder,
                                                  ServerLevel level, ChunkPos pos) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        holder.writeSpawnData(buf);
        List<ServerPlayer> players = level.getChunkSource().chunkMap.getPlayers(pos, false);
        NetworkManager.sendToPlayers(players, SYNC_PRESET_ENTITY_ID, buf);
    }

    public static void register() {
        INSTANCE.register(ToClientDataPackSync.class,
                ToClientDataPackSync::encode,
                ToClientDataPackSync::new,
                ToClientDataPackSync::handle);
        INSTANCE.register(ToClientSyncGameRules.class,
                ToClientSyncGameRules::encode,
                ToClientSyncGameRules::new,
                ToClientSyncGameRules::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SYNC_PRESET_ENTITY_ID,
                (buf, context) -> {
            Level level = UtilEntity.getLevel(context.getPlayer());
            int id = buf.readInt();
            context.queue(() -> {
                if (!(level.getEntity(id) instanceof JsonPresetEntityHolder<?> holder)) {
                    LOGGER.error("Received a JsonPresetEntityHolder sync packet for an entity with " +
                            "{} but it doesn't exist on the client side???", id);
                    return;
                }
                holder.readSpawnData(buf);
            });
        });
    }
}

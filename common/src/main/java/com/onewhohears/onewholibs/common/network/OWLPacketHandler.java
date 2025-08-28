package com.onewhohears.onewholibs.common.network;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.common.network.toclient.ToClientDataPackSync;
import com.onewhohears.onewholibs.common.network.toclient.ToClientSyncGameRules;
import com.onewhohears.onewholibs.common.network.toclient.ToClientSyncPresetEntity;
import com.onewhohears.onewholibs.entity.JsonPresetEntityHolder;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class OWLPacketHandler {

    private OWLPacketHandler() {}

    public static final SimpleNetworkManager INSTANCE = SimpleNetworkManager.create(OWLMod.MOD_ID);

    public static final MessageType S2C_DATA_PACK_SYNC = INSTANCE.registerS2C(
            "data_pack_sync", ToClientDataPackSync::new);
    public static final MessageType S2C_SYNC_GAME_RULES = INSTANCE.registerS2C(
            "sync_game_rules", ToClientSyncGameRules::new);
    public static final MessageType S2C_SYNC_PRESET_ENTITY = INSTANCE.registerS2C(
            "sync_preset_entity", ToClientSyncPresetEntity::new);

    public static void sendSyncPresetEntityPacket(JsonPresetEntityHolder<?> holder,
                                                  ServerLevel level, ChunkPos pos) {
        new ToClientSyncPresetEntity(holder).sendToChunkListeners(level.getChunk(pos.x, pos.z));
    }

    public static void init() {}
}

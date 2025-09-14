package com.onewhohears.onewholibs.common.network;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.common.network.toclient.*;
import com.onewhohears.onewholibs.common.network.toserver.ToServerCanSeePos;
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
    public static final MessageType S2C_TEST_PRESET = INSTANCE.registerS2C(
            "test_preset", ToClientTestPreset::new);
    public static final MessageType S2C_RAY_CAST = INSTANCE.registerS2C(
            "s2c_ray_cast", ToClientCanSeePos::new);

    public static final MessageType C2S_RAY_CAST = INSTANCE.registerC2S(
            "c2s_ray_cast", ToServerCanSeePos::new);

    public static void sendSyncPresetEntityPacket(JsonPresetEntityHolder<?> holder,
                                                  ServerLevel level, ChunkPos pos) {
        new ToClientSyncPresetEntity(holder).sendToChunkListeners(level.getChunk(pos.x, pos.z));
    }

    public static void init() {}
}

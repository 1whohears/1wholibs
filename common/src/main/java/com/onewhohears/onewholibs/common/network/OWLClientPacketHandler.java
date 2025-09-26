package com.onewhohears.onewholibs.common.network;

import com.onewhohears.onewholibs.common.network.toclient.ClientBoundSpawnDataPacket;
import com.onewhohears.onewholibs.entity.AdditionalSpawnDataEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class OWLClientPacketHandler {

    public static void handle(ClientBoundSpawnDataPacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        if (level.getEntity(packet.getId()) instanceof AdditionalSpawnDataEntity entity) {
            entity.readSpawnData(packet.getBuffer());
        }
    }

}

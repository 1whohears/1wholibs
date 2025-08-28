package com.onewhohears.onewholibs.common.event;

import com.onewhohears.onewholibs.common.network.OWLPacketHandler;
import com.onewhohears.onewholibs.entity.JsonPresetEntityHolder;
import com.onewhohears.onewholibs.util.UtilSync;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Collections;

public class OWLCommonEventHandlers {

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(OWLCommonEventHandlers::onPlayerJoin);
        EntityEvent.ADD.register(OWLCommonEventHandlers::onAddEntity);
    }

    public static EventResult onAddEntity(Entity entity, Level level) {
        if (level.isClientSide()) return EventResult.pass();
        if (!(entity instanceof JsonPresetEntityHolder<?> holder)) return EventResult.pass();
        OWLPacketHandler.sendSyncPresetEntityPacket(holder, (ServerLevel) level, entity.chunkPosition());
        return EventResult.pass();
    }

    public static void onPlayerJoin(ServerPlayer player) {
        UtilSync.syncPresets(Collections.singleton(player));
        UtilSync.syncGameRules(Collections.singleton(player), player.getServer());
    }

}

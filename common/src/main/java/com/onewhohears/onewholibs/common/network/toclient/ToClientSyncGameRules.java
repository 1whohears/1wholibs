package com.onewhohears.onewholibs.common.network.toclient;

import com.onewhohears.onewholibs.common.command.CustomGameRules;
import com.onewhohears.onewholibs.common.event.OWLEvents;
import com.onewhohears.onewholibs.common.network.OWLPacketHandler;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;

public class ToClientSyncGameRules extends BaseS2CMessage {

	private final MinecraftServer server;

	public ToClientSyncGameRules(@NotNull MinecraftServer server) {
		this.server = server;
	}
	
	public ToClientSyncGameRules(FriendlyByteBuf buffer) {
		server = null;
		int boolNum = buffer.readInt();
		for (int i = 0; i < boolNum; ++i) {
			String id = buffer.readUtf();
			boolean bool = buffer.readBoolean();
            OWLEvents.SYNC_BOOL_GAME_RULE.invoker().sync(id, bool);
		}
		int intNum = buffer.readInt();
		for (int i = 0; i < intNum; ++i) {
			String id = buffer.readUtf();
			int integer = buffer.readInt();
            OWLEvents.SYNC_INT_GAME_RULE.invoker().sync(id, integer);
		}
	}

    @Override
    public MessageType getType() {
        return OWLPacketHandler.S2C_SYNC_GAME_RULES;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        GameRules gamerules = server.getGameRules();
        buffer.writeInt(CustomGameRules.getSyncBools().size());
        CustomGameRules.getSyncBools().forEach((booleanValueKey) -> {
            buffer.writeUtf(booleanValueKey.getId());
            buffer.writeBoolean(gamerules.getBoolean(booleanValueKey));
        });
        buffer.writeInt(CustomGameRules.getSyncInts().size());
        CustomGameRules.getSyncInts().forEach((integerValueKey) -> {
            buffer.writeUtf(integerValueKey.getId());
            buffer.writeInt(gamerules.getInt(integerValueKey));
        });
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {

    }
}

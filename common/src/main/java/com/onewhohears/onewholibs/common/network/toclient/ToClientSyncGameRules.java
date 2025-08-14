package com.onewhohears.onewholibs.common.network.toclient;

import java.util.function.Supplier;

import com.onewhohears.onewholibs.common.command.CustomGameRules;
import com.onewhohears.onewholibs.common.event.OWLEvents;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;

public class ToClientSyncGameRules {

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

	public void encode(FriendlyByteBuf buffer) {
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

	public void handle(Supplier<NetworkManager.PacketContext> ctx) {

	}

}

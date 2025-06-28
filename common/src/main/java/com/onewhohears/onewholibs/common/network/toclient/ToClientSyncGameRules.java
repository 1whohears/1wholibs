package com.onewhohears.onewholibs.common.network.toclient;

import java.util.function.Supplier;

import com.onewhohears.onewholibs.common.command.CustomGameRules;
import com.onewhohears.onewholibs.common.event.OnSyncBoolGameRuleEvent;
import com.onewhohears.onewholibs.common.event.OnSyncIntGameRuleEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent.Context;

public class ToClientSyncGameRules {

	private final MinecraftServer server;

	public ToClientSyncGameRules(MinecraftServer server) {
		this.server = server;
	}
	
	public ToClientSyncGameRules(FriendlyByteBuf buffer) {
		server = null;
		int boolNum = buffer.readInt();
		for (int i = 0; i < boolNum; ++i) {
			String id = buffer.readUtf();
			boolean bool = buffer.readBoolean();
			MinecraftForge.EVENT_BUS.post(new OnSyncBoolGameRuleEvent(id, bool));
		}
		int intNum = buffer.readInt();
		for (int i = 0; i < intNum; ++i) {
			String id = buffer.readUtf();
			int integer = buffer.readInt();
			MinecraftForge.EVENT_BUS.post(new OnSyncIntGameRuleEvent(id, integer));
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

	public boolean handle(Supplier<Context> ctx) {
		ctx.get().setPacketHandled(true);
		return true;
	}

}

package com.onewhohears.onewholibs.common.network.toclient;

import java.util.function.Supplier;

import com.onewhohears.onewholibs.common.event.GetGameRulesToSyncEvent;
import com.onewhohears.onewholibs.common.event.OnSyncBoolGameRuleEvent;
import com.onewhohears.onewholibs.common.event.OnSyncIntGameRuleEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent.Context;

public class ToClientSyncGameRules {

	private final GetGameRulesToSyncEvent event;

	public ToClientSyncGameRules(MinecraftServer server) {
		event = new GetGameRulesToSyncEvent(server);
	}
	
	public ToClientSyncGameRules(FriendlyByteBuf buffer) {
		event = null;
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
		MinecraftForge.EVENT_BUS.post(event);
		buffer.writeInt(event.getBools().size());
		event.getBools().forEach((id, bool) -> {
			buffer.writeUtf(id);
			buffer.writeBoolean(bool);
		});
		buffer.writeInt(event.getInts().size());
		event.getInts().forEach((id, integer) -> {
			buffer.writeUtf(id);
			buffer.writeInt(integer);
		});
	}

	public boolean handle(Supplier<Context> ctx) {
		ctx.get().setPacketHandled(true);
		return true;
	}

}

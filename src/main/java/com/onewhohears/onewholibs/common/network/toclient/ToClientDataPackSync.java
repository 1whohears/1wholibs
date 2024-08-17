package com.onewhohears.onewholibs.common.network.toclient;

import java.util.List;
import java.util.function.Supplier;

import com.onewhohears.onewholibs.common.event.GetJsonPresetListenersEvent;
import com.onewhohears.onewholibs.common.event.RegisterPresetTypesEvent;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent.Context;

public class ToClientDataPackSync {
	
	public ToClientDataPackSync() {
	}
	
	public ToClientDataPackSync(FriendlyByteBuf buffer) {
		MinecraftForge.EVENT_BUS.post(new RegisterPresetTypesEvent());
		GetJsonPresetListenersEvent event = new GetJsonPresetListenersEvent();
		MinecraftForge.EVENT_BUS.post(event);
		List<JsonPresetReloadListener<?>> listeners = event.getListeners();
		for (JsonPresetReloadListener<?> listener : listeners)
			listener.readBuffer(buffer);
	}

	public void encode(FriendlyByteBuf buffer) {
		GetJsonPresetListenersEvent event = new GetJsonPresetListenersEvent();
		MinecraftForge.EVENT_BUS.post(event);
		List<JsonPresetReloadListener<?>> listeners = event.getListeners();
		for (JsonPresetReloadListener<?> listener : listeners)
			listener.writeToBuffer(buffer);
	}

	public boolean handle(Supplier<Context> ctx) {
		ctx.get().setPacketHandled(true);
		return true;
	}

}

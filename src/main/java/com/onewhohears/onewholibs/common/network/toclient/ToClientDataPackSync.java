package com.onewhohears.onewholibs.common.network.toclient;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.common.event.GetJsonPresetListenersEvent;
import com.onewhohears.onewholibs.common.event.RegisterPresetTypesEvent;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import com.sun.nio.sctp.IllegalReceiveException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent.Context;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public class ToClientDataPackSync {

	static final Logger LOGGER = LogUtils.getLogger();

	public ToClientDataPackSync() {
	}
	
	public ToClientDataPackSync(FriendlyByteBuf buffer) {
		MinecraftForge.EVENT_BUS.post(new RegisterPresetTypesEvent());
		GetJsonPresetListenersEvent event = new GetJsonPresetListenersEvent();
		MinecraftForge.EVENT_BUS.post(event);
		List<JsonPresetReloadListener<?>> listeners = event.getListeners();
		int num = buffer.readInt();
		for (int i = 0; i < num; ++i) {
			String name = buffer.readUtf();
			JsonPresetReloadListener<?> listener = getListenerByName(name, listeners);
			if (listener == null) {
                LOGGER.error("Received Json preset data for unknown super type {}. " +
						"The super type must be registered on the client side as well. Contact developer.", name);
				throw new IllegalReceiveException("Received Json preset data for unknown super type "+name+". " +
						"The super type must be registered on the client side as well. Contact developer.");
			}
			listener.registerDefaultPresetTypes();
			listener.readBuffer(buffer);
		}
	}

	@Nullable
	private JsonPresetReloadListener<?> getListenerByName(String name, List<JsonPresetReloadListener<?>> listeners) {
		for (JsonPresetReloadListener<?> listener : listeners)
			if (listener.getName().equals(name))
				return listener;
		return null;
	}

	public void encode(FriendlyByteBuf buffer) {
		GetJsonPresetListenersEvent event = new GetJsonPresetListenersEvent();
		MinecraftForge.EVENT_BUS.post(event);
		List<JsonPresetReloadListener<?>> listeners = event.getListeners();
		buffer.writeInt(listeners.size());
		for (JsonPresetReloadListener<?> listener : listeners) {
			buffer.writeUtf(listener.getName());
			listener.writeToBuffer(buffer);
		}
	}

	public boolean handle(Supplier<Context> ctx) {
		ctx.get().setPacketHandled(true);
		return true;
	}

}

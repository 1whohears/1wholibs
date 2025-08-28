package com.onewhohears.onewholibs.common.network.toclient;

import java.util.List;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.common.event.OWLEvents;
import com.onewhohears.onewholibs.common.network.OWLPacketHandler;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import com.sun.nio.sctp.IllegalReceiveException;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ToClientDataPackSync extends BaseS2CMessage {

	static final Logger LOGGER = LogUtils.getLogger();

	public ToClientDataPackSync() {
	}

    @Override
    public MessageType getType() {
        return OWLPacketHandler.S2C_DATA_PACK_SYNC;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        List<JsonPresetReloadListener<?>> listeners = OWLEvents.getJsonPresetReloadListeners();
        buffer.writeInt(listeners.size());
        for (JsonPresetReloadListener<?> listener : listeners) {
            buffer.writeUtf(listener.getName());
            listener.writeToBuffer(buffer);
        }
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {

    }

    public ToClientDataPackSync(FriendlyByteBuf buffer) {
        OWLEvents.registerPresetTypesEvent();
        List<JsonPresetReloadListener<?>> listeners = OWLEvents.getJsonPresetReloadListeners();
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

}

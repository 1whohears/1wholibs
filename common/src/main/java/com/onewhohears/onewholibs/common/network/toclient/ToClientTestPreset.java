package com.onewhohears.onewholibs.common.network.toclient;

import com.onewhohears.onewholibs.common.network.OWLPacketHandler;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresets;
import com.onewhohears.onewholibs.util.UtilMCText;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class ToClientTestPreset extends BaseS2CMessage {

    private final String presetId;

    public ToClientTestPreset(String presetId) {
        this.presetId = presetId;
    }

    public ToClientTestPreset(FriendlyByteBuf buffer) {
        this.presetId = buffer.readUtf();
    }

    @Override
    public MessageType getType() {
        return OWLPacketHandler.S2C_TEST_PRESET;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(presetId);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            TestPresetStats stats = TestPresets.get().get(presetId);
            if (stats == null) {
                Minecraft.getInstance().player.displayClientMessage(UtilMCText.literal(
                        "The Test Preset "+presetId+" does not exist on the client side!"), false);
                return;
            }
            Minecraft.getInstance().player.displayClientMessage(UtilMCText.literal(
                    "The Test Preset "+presetId+" has a value of "+stats.getValue()+" on the client side!"), false);
        });
    }
}

package com.onewhohears.onewholibs.common.event.handler;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.common.event.GetJsonPresetListenersEvent;
import com.onewhohears.onewholibs.common.event.RegisterPresetTypesEvent;
import com.onewhohears.onewholibs.util.UtilSync;
import com.onewhohears.onewholibs.common.network.PacketHandler;
import com.onewhohears.onewholibs.common.network.toclient.ToClientDataPackSync;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = OWLMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonForgeEvents {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void addReloadListener(AddReloadListenerEvent event) {
        MinecraftForge.EVENT_BUS.post(new RegisterPresetTypesEvent());
        GetJsonPresetListenersEvent listenersEvent = new GetJsonPresetListenersEvent();
        MinecraftForge.EVENT_BUS.post(listenersEvent);
        listenersEvent.getListeners().forEach(event::addListener);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        PacketDistributor.PacketTarget target;
        if (event.getPlayer() == null) target = PacketDistributor.ALL.noArg();
        else target = PacketDistributor.PLAYER.with(event::getPlayer);
        UtilSync.syncPresets(target);
        UtilSync.syncGameRules(target, event.getPlayer().getServer());
    }

}

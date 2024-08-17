package com.onewhohears.onewholibs.common.event.handler;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.common.event.GetJsonPresetListenersEvent;
import com.onewhohears.onewholibs.common.event.RegisterPresetTypesEvent;
import com.onewhohears.onewholibs.util.UtilSync;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/**
 * @author 1whohears
 */
@Mod.EventBusSubscriber(modid = OWLMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonForgeEvents {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void addReloadListener(AddReloadListenerEvent event) {
        MinecraftForge.EVENT_BUS.post(new RegisterPresetTypesEvent());
        GetJsonPresetListenersEvent listenersEvent = new GetJsonPresetListenersEvent();
        MinecraftForge.EVENT_BUS.post(listenersEvent);
        listenersEvent.getListeners().forEach((listener -> {
            listener.registerDefaultPresetTypes();
            event.addListener(listener);
        }));
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

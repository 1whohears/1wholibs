package com.onewhohears.onewholibs.client.event.handler;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.client.event.RegisterCustomAnimsEvent;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import com.onewhohears.onewholibs.client.model.obj.customanims.CustomAnims;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OWLMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerClientReloadListener(RegisterClientReloadListenersEvent event) {
        RegisterCustomAnimsEvent animsEvent = new RegisterCustomAnimsEvent();
        MinecraftForge.EVENT_BUS.post(animsEvent);
        CustomAnims.copyAnimMap(animsEvent.getAnims());
        event.registerReloadListener(ObjEntityModels.get());
    }

}
